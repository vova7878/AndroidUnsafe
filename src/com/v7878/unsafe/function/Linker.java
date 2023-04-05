package com.v7878.unsafe.function;

import static com.v7878.unsafe.AndroidUnsafe5.*;
import static com.v7878.unsafe.Utils.*;
import com.v7878.unsafe.dex.*;
import com.v7878.unsafe.dex.modifications.Modifications.EmptyClassLoader;
import com.v7878.unsafe.memory.*;
import com.v7878.unsafe.methodhandle.*;
import static com.v7878.unsafe.methodhandle.EmulatedStackFrame.RETURN_VALUE_IDX;
import com.v7878.unsafe.methodhandle.EmulatedStackFrame.StackFrameAccessor;
import static com.v7878.unsafe.methodhandle.Transformers.*;
import com.v7878.unsafe.methodhandle.Transformers.TransformerI;
import dalvik.system.DexFile;
import java.lang.invoke.*;
import java.lang.reflect.*;
import java.util.Objects;

class Test {
}

public class Linker {

    private static final SoftReferenceCache<String, Class<?>> DOWNCALL_CACHE
            = new SoftReferenceCache<>();
    private static final int HANDLER_OFFSET = classSizeField(Test.class);

    private static void copyArg(StackFrameAccessor reader,
            StackFrameAccessor writer, Class<?> type) {
        if (type == Addressable.class) {
            long tmp = reader.nextReference(Addressable.class)
                    .pointer().getRawAddress();
            if (IS64BIT) {
                writer.putNextLong(tmp);
            } else {
                writer.putNextInt((int) tmp);
            }
            return;
        } else if (type == Word.class) {
            long tmp = reader.nextReference(Word.class).longValue();
            if (IS64BIT) {
                writer.putNextLong(tmp);
            } else {
                writer.putNextInt((int) tmp);
            }
            return;
        }
        EmulatedStackFrame.copyNext(reader, writer, type);

    }

    private static void copyRet(StackFrameAccessor reader,
            StackFrameAccessor writer, Class<?> type) {
        if (type == Addressable.class) {
            long value;
            if (IS64BIT) {
                value = reader.nextLong();
            } else {
                value = reader.nextInt() & 0xffffffffL;
            }
            writer.putNextReference(new Pointer(value), type);
            return;
        } else if (type == Word.class) {
            long value;
            if (IS64BIT) {
                value = reader.nextLong();
            } else {
                value = reader.nextInt() & 0xffffffffL;
            }
            writer.putNextReference(new Word(value), type);
        }
        EmulatedStackFrame.copyNext(reader, writer, type);

    }

    private static TransformerI getTransformerI(MethodHandle stub) {
        return (stackFrame) -> {
            MethodType current_type = stackFrame.type();
            int params_count = current_type.parameterCount();
            EmulatedStackFrame stub_frame = EmulatedStackFrame.create(stub.type());
            StackFrameAccessor thiz_acc = stackFrame.createAccessor();
            StackFrameAccessor stub_acc = stub_frame.createAccessor();
            Class<?> type;
            for (int i = 0; i < params_count; i++) {
                type = current_type.parameterType(i);
                copyArg(thiz_acc, stub_acc, type);
            }
            invokeExactFromTransform(stub, stub_frame);
            type = current_type.returnType();
            if (type != void.class) {
                thiz_acc.moveTo(RETURN_VALUE_IDX);
                stub_acc.moveTo(RETURN_VALUE_IDX);
                copyRet(stub_acc, thiz_acc, type);
            }
        };
    }

    public static MethodHandle downcallHandle(Addressable symbol,
            FunctionDescriptor function) {
        assert_(!symbol.pointer().isNull(), IllegalArgumentException::new,
                "symbol == nullptr");
        Objects.requireNonNull(function);
        long raw_address = symbol.pointer().getRawAddress();
        MethodType stub_call_type = inferMethodType(function, true);
        ProtoId proto = ProtoId.of(stub_call_type);
        String stub_name = getStubName(raw_address, proto);
        Class<?> stub = DOWNCALL_CACHE.get(stub_name, unused -> {
            return newStub(symbol, stub_name, stub_call_type, proto);
        });
        MethodHandle handle = (MethodHandle) getObject(stub, HANDLER_OFFSET);
        MethodType handle_call_type = inferMethodType(function, false);
        if (stub_call_type.equals(handle_call_type)) {
            return handle;
        }
        return makeTransformer(handle_call_type, getTransformerI(handle));
    }

    private static String getStubName(long address, ProtoId proto) {
        return Linker.class.getName() + "$Stub_"
                + address + "_" + proto.getShorty();
    }

    private static ClassLoader getStubClassLoader() {
        // new every time, needed for GC
        return new EmptyClassLoader(Linker.class.getClassLoader());
    }

    private static Class<?> newStub(Addressable symbol, String stub_name,
            MethodType stub_call_type, ProtoId proto) {
        TypeId stub_id = TypeId.of(stub_name);
        ClassDef clazz = new ClassDef(stub_id);
        clazz.setSuperClass(TypeId.of(Object.class));
        clazz.setAccessFlags(Modifier.PUBLIC | Modifier.FINAL);
        clazz.getClassData().getDirectMethods().add(
                new EncodedMethod(
                        new MethodId(stub_id, proto, "function"),
                        Modifier.PUBLIC | Modifier.NATIVE | Modifier.STATIC,
                        new AnnotationSet(
                                AnnotationItem.CriticalNative()
                        ), null, null
                )
        );
        clazz.getClassData().getStaticFields().add(
                new EncodedField(
                        new FieldId(stub_id, TypeId.of(
                                MethodHandle.class), "handler"),
                        Modifier.PUBLIC | Modifier.STATIC, null
                )
        );
        DexFile dex = openDexFile(new Dex(clazz).compile());
        Class<?> stub = loadClass(dex, stub_name, getStubClassLoader());
        Method function = getDeclaredMethod(stub, "function",
                stub_call_type.parameterArray());
        setExecutableData(function, symbol);
        MethodHandle handler = nothrows_run(() -> MethodHandles.lookup().unreflect(function));
        Field handler_field = getDeclaredField(stub, "handler");
        assert_(HANDLER_OFFSET == staticFieldOffset(handler_field), AssertionError::new);
        putObject(stub, HANDLER_OFFSET, handler);
        return stub;
    }

    private static MethodType inferMethodType(
            FunctionDescriptor descriptor, boolean forStub) {
        MethodType type;
        if (descriptor.returnLayout().isPresent()) {
            type = MethodType.methodType(carrierFor(
                    descriptor.returnLayout().get(), forStub, false));
        } else {
            type = MethodType.methodType(void.class);
        }
        for (Layout argLayout : descriptor.argumentLayouts()) {
            type = type.appendParameterTypes(
                    carrierFor(argLayout, forStub, true));
        }
        return type;
    }

    private static Class<?> carrierFor(Layout layout, boolean forStub, boolean forArg) {
        if (layout instanceof ValueLayout.OfObject) {
            //Unsupported
        } else if (layout instanceof ValueLayout.OfAddress) {
            return forStub ? (IS64BIT ? long.class : int.class) : Addressable.class;
        } else if (layout instanceof ValueLayout.OfWord) {
            return forStub ? (IS64BIT ? long.class : int.class) : Word.class;
        } else if (layout instanceof ValueLayout) {
            return ((ValueLayout) layout).carrier();
        }
        throw new IllegalArgumentException("Unsupported layout: " + layout);
    }
}
