package com.v7878.unsafe.function;

import static com.v7878.unsafe.AndroidUnsafe3.unreflect;
import static com.v7878.unsafe.AndroidUnsafe5.IS64BIT;
import static com.v7878.unsafe.AndroidUnsafe5.classSizeField;
import static com.v7878.unsafe.AndroidUnsafe5.getDeclaredField;
import static com.v7878.unsafe.AndroidUnsafe5.getDeclaredMethod;
import static com.v7878.unsafe.AndroidUnsafe5.getObject;
import static com.v7878.unsafe.AndroidUnsafe5.loadClass;
import static com.v7878.unsafe.AndroidUnsafe5.openDexFile;
import static com.v7878.unsafe.AndroidUnsafe5.putObject;
import static com.v7878.unsafe.AndroidUnsafe5.setExecutableData;
import static com.v7878.unsafe.AndroidUnsafe5.staticFieldOffset;
import static com.v7878.unsafe.Utils.assert_;
import static com.v7878.unsafe.methodhandle.Transformers.invokeExactWithFrameNoChecks;
import static com.v7878.unsafe.methodhandle.Transformers.makeTransformer;

import android.util.Pair;

import androidx.annotation.Keep;

import com.v7878.unsafe.dex.AnnotationItem;
import com.v7878.unsafe.dex.AnnotationSet;
import com.v7878.unsafe.dex.ClassDef;
import com.v7878.unsafe.dex.Dex;
import com.v7878.unsafe.dex.EncodedField;
import com.v7878.unsafe.dex.EncodedMethod;
import com.v7878.unsafe.dex.FieldId;
import com.v7878.unsafe.dex.MethodId;
import com.v7878.unsafe.dex.ProtoId;
import com.v7878.unsafe.dex.TypeId;
import com.v7878.unsafe.dex.modifications.Modifications.EmptyClassLoader;
import com.v7878.unsafe.memory.Addressable;
import com.v7878.unsafe.memory.Bindable;
import com.v7878.unsafe.memory.Layout;
import com.v7878.unsafe.memory.Pointer;
import com.v7878.unsafe.memory.ValueLayout;
import com.v7878.unsafe.memory.Word;
import com.v7878.unsafe.methodhandle.EmulatedStackFrame;
import com.v7878.unsafe.methodhandle.EmulatedStackFrame.StackFrameAccessor;
import com.v7878.unsafe.methodhandle.Transformers.TransformerI;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Objects;

import dalvik.system.DexFile;

@Keep
class Test {
}

public class Linker {

    private static final SoftReferenceCache<Pair<Pointer, FunctionDescriptor>, Class<?>>
            DOWNCALL_CACHE = new SoftReferenceCache<>();
    private static final int HANDLER_OFFSET = classSizeField(Test.class);

    private static void copyArg(StackFrameAccessor reader,
                                StackFrameAccessor writer, Class<?> type) {
        if (type == Addressable.class) {
            Addressable tmp = reader.nextReference(Addressable.class);
            long value = tmp == null ? 0 : tmp.pointer().getRawAddress();
            if (IS64BIT) {
                writer.putNextLong(value);
            } else {
                writer.putNextInt((int) value);
            }
            return;
        }
        if (type == Word.class) {
            long value = reader.nextReference(Word.class).longValue();
            if (IS64BIT) {
                writer.putNextLong(value);
            } else {
                writer.putNextInt((int) value);
            }
            return;
        }
        EmulatedStackFrame.copyNext(reader, writer, type);
    }

    private static void copyRet(StackFrameAccessor reader,
                                StackFrameAccessor writer, ValueLayout layout) {
        Class<?> type = layout.carrier();
        if (type == Addressable.class) {
            Bindable<?> content = ((ValueLayout.OfAddress<?>) layout).content();
            long value = IS64BIT ? reader.nextLong() : reader.nextInt();
            writer.putNextReference(content.bind(new Pointer(value)), Object.class);
            return;
        }
        if (type == Word.class) {
            long value = IS64BIT ? reader.nextLong() : reader.nextInt();
            writer.putNextReference(new Word(value), type);
            return;
        }
        EmulatedStackFrame.copyNext(reader, writer, type);
    }

    private static TransformerI getTransformerI(
            MethodHandle stub, FunctionDescriptor function) {

        Class<?>[] args = function.argumentLayouts().stream()
                .map(l -> ((ValueLayout) l).carrier()).toArray(Class[]::new);
        ValueLayout ret = (ValueLayout) function.returnLayout().orElse(null);

        return stackFrame -> {
            StackFrameAccessor thiz_acc = stackFrame.createAccessor();
            EmulatedStackFrame stub_frame = EmulatedStackFrame.create(stub.type());
            StackFrameAccessor stub_acc = stub_frame.createAccessor();
            for (Class<?> arg : args) {
                copyArg(thiz_acc, stub_acc, arg);
            }
            invokeExactWithFrameNoChecks(stub, stub_frame);
            if (ret != null) {
                thiz_acc.moveToReturn();
                stub_acc.moveToReturn();
                copyRet(stub_acc, thiz_acc, ret);
            }
        };
    }

    public static MethodHandle downcallHandle(Addressable symbol, FunctionDescriptor function) {
        assert_(!symbol.pointer().isNull(), IllegalArgumentException::new,
                "symbol == nullptr");
        Objects.requireNonNull(function);

        Class<?> stub = DOWNCALL_CACHE.get(new Pair<>(symbol.pointer(), function),
                pair -> newStub(pair.first, pair.second));

        return (MethodHandle) getObject(stub, HANDLER_OFFSET);
    }

    private static String getStubName(Pointer symbol, ProtoId proto) {
        return Linker.class.getName() + "$Stub_"
                + symbol.getRawAddress() + "_" + proto.getShorty();
    }

    private static ClassLoader getStubClassLoader() {
        // new every time, needed for GC
        return new EmptyClassLoader(Linker.class.getClassLoader());
    }

    private static Class<?> newStub(Pointer symbol, FunctionDescriptor stub_desc) {
        MethodType stub_call_type = inferMethodType(stub_desc, true);
        ProtoId proto = ProtoId.of(stub_call_type);
        String stub_name = getStubName(symbol, proto);
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
                        new FieldId(stub_id, TypeId.of(MethodHandle.class), "handler"),
                        Modifier.PUBLIC | Modifier.STATIC, null
                )
        );
        //noinspection deprecation
        DexFile dex = openDexFile(new Dex(clazz).compile());
        Class<?> stub = loadClass(dex, stub_name, getStubClassLoader());
        Method function = getDeclaredMethod(stub, "function",
                stub_call_type.parameterArray());
        setExecutableData(function, symbol);
        MethodHandle handler = unreflect(function);
        Field handler_field = getDeclaredField(stub, "handler");
        assert_(HANDLER_OFFSET == staticFieldOffset(handler_field), AssertionError::new);

        //TODO: skip check
        MethodType handle_call_type = inferMethodType(stub_desc, false);
        if (!stub_call_type.equals(handle_call_type)) {
            handler = makeTransformer(handle_call_type, getTransformerI(handler, stub_desc));
        }

        putObject(stub, HANDLER_OFFSET, handler);
        return stub;
    }

    static MethodType inferMethodType(FunctionDescriptor descriptor, boolean forStub) {
        Class<?> ret = !descriptor.returnLayout().isPresent() ? void.class :
                carrierFor(descriptor.returnLayout().get(), forStub, false);
        Class<?>[] args = new Class<?>[descriptor.argumentCount()];
        for (int i = 0; i < args.length; i++) {
            args[i] = carrierFor(descriptor.argumentLayout(i), forStub, true);
        }
        return MethodType.methodType(ret, args);
    }

    private static Class<?> carrierFor(Layout layout, boolean forStub, boolean forArg) {
        //noinspection StatementWithEmptyBody
        if (layout instanceof ValueLayout.OfObject) {
            //Unsupported
        } else if (layout instanceof ValueLayout.OfAddress) {
            if (forStub) {
                return IS64BIT ? long.class : int.class;
            }
            return forArg ? Addressable.class : Object.class;
        } else if (layout instanceof ValueLayout.OfWord) {
            return forStub ? (IS64BIT ? long.class : int.class) : Word.class;
        } else if (layout instanceof ValueLayout) {
            return ((ValueLayout) layout).carrier();
        }
        throw new IllegalArgumentException("Unsupported layout: " + layout);
    }
}
