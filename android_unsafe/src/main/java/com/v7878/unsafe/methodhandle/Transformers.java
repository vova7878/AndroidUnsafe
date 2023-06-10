package com.v7878.unsafe.methodhandle;

import static com.v7878.unsafe.AndroidUnsafe3.arrayCast;
import static com.v7878.unsafe.AndroidUnsafe3.unreflectDirect;
import static com.v7878.unsafe.AndroidUnsafe5.allocateInstance;
import static com.v7878.unsafe.AndroidUnsafe5.fullFence;
import static com.v7878.unsafe.AndroidUnsafe5.getDeclaredMethod;
import static com.v7878.unsafe.AndroidUnsafe5.getExecutableAccessFlags;
import static com.v7878.unsafe.AndroidUnsafe5.loadClass;
import static com.v7878.unsafe.AndroidUnsafe5.openDexFile;
import static com.v7878.unsafe.AndroidUnsafe5.setExecutableAccessFlags;
import static com.v7878.unsafe.AndroidUnsafe5.setTrusted;
import static com.v7878.unsafe.Utils.getSdkInt;
import static com.v7878.unsafe.Utils.nothrows_run;

import com.v7878.unsafe.AndroidUnsafe3.MethodHandleMirror;
import com.v7878.unsafe.dex.ClassDef;
import com.v7878.unsafe.dex.CodeItem;
import com.v7878.unsafe.dex.Dex;
import com.v7878.unsafe.dex.EncodedField;
import com.v7878.unsafe.dex.EncodedMethod;
import com.v7878.unsafe.dex.FieldId;
import com.v7878.unsafe.dex.MethodId;
import com.v7878.unsafe.dex.PCList;
import com.v7878.unsafe.dex.ProtoId;
import com.v7878.unsafe.dex.TypeId;
import com.v7878.unsafe.dex.bytecode.CheckCast;
import com.v7878.unsafe.dex.bytecode.IInstanceOp;
import com.v7878.unsafe.dex.bytecode.IInstanceOp.IGetObject;
import com.v7878.unsafe.dex.bytecode.Instruction;
import com.v7878.unsafe.dex.bytecode.InvokeKind;
import com.v7878.unsafe.dex.bytecode.InvokeKind.InvokeVirtual;
import com.v7878.unsafe.dex.bytecode.InvokePolymorphic;
import com.v7878.unsafe.dex.bytecode.MoveResult;
import com.v7878.unsafe.dex.bytecode.MoveResultObject;
import com.v7878.unsafe.dex.bytecode.Return;
import com.v7878.unsafe.dex.bytecode.ReturnObject;
import com.v7878.unsafe.dex.bytecode.ReturnVoid;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.lang.invoke.WrongMethodTypeException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import dalvik.system.DexFile;

public class Transformers {

    @SuppressWarnings("unchecked")
    private static final Class<MethodHandle> invoke_transformer = (Class<MethodHandle>) nothrows_run(
            () -> Class.forName("java.lang.invoke.Transformers$Transformer"));
    private static final Class<MethodHandle> transformer;
    private static final Constructor<MethodHandle> transformer_constructor;
    private static final MethodHandle directAsType = nothrows_run(() -> unreflectDirect(
            getDeclaredMethod(MethodHandle.class, "asType", MethodType.class)));
    private static final MethodHandle directAsVarargsCollector = nothrows_run(() -> unreflectDirect(
            getDeclaredMethod(MethodHandle.class, "asVarargsCollector", Class.class)));
    private static final InvokerI invoker;

    //TODO: bindTo?
    static {
        TypeId mh = TypeId.of(MethodHandle.class);
        TypeId mt = TypeId.of(MethodType.class);

        TypeId esf = TypeId.of("dalvik.system.EmulatedStackFrame");
        TypeId mesf = TypeId.of(EmulatedStackFrame.class);

        //public final class Transformer extends MethodHandle implements Cloneable {
        //    TransformerImpl impl;
        //    <...>
        //}
        String transformer_name = Transformers.class.getName() + "$Transformer";
        TypeId transformer_id = TypeId.of(transformer_name);

        ClassDef transformer_def = new ClassDef(transformer_id);
        transformer_def.setSuperClass(TypeId.of(invoke_transformer));
        transformer_def.getInterfaces().add(TypeId.of(Cloneable.class));
        transformer_def.setAccessFlags(Modifier.PUBLIC | Modifier.FINAL);

        FieldId impl_field = new FieldId(transformer_id,
                TypeId.of(TransformerImpl.class), "impl");
        transformer_def.getClassData().getInstanceFields().add(
                new EncodedField(impl_field, Modifier.PUBLIC, null)
        );

        //public Transformer(MethodType type, TransformerImpl impl) {
        //    super(type);
        //    this.impl = impl;
        //}
        PCList<Instruction> code = PCList.empty();
        code.add(new InvokeKind.InvokeDirect(2,
                MethodId.constructor(TypeId.of(invoke_transformer), mt),
                0, 1, 0, 0, 0));
        code.add(new IInstanceOp.IPutObject(2, 0, impl_field));
        code.add(new ReturnVoid());

        transformer_def.getClassData().getDirectMethods().add(
                new EncodedMethod(
                        MethodId.constructor(transformer_id, mt, TypeId.of(TransformerImpl.class)),
                        Modifier.PUBLIC | 0x10000, null, null,
                        new CodeItem(3, 3, 2, code, null)
                )
        );

        //public void transform(dalvik.system.EmulatedStackFrame stack) {
        //    impl.transform(com.v7878.unsafe.methodhandle.EmulatedStackFrame.wrap(stack));
        //}
        code.clear();
        code.add(new IGetObject(0, 2, impl_field));
        code.add(new InvokeKind.InvokeStatic(1, new MethodId(mesf,
                new ProtoId(mesf, TypeId.of(Object.class)), "wrap"),
                3, 0, 0, 0, 0));
        code.add(new MoveResultObject(1));
        code.add(new InvokeVirtual(3, new MethodId(TypeId.of(TransformerImpl.class),
                new ProtoId(TypeId.V, mh, mesf), "transform"),
                0, 2, 1, 0, 0));
        code.add(new ReturnVoid());

        transformer_def.getClassData().getVirtualMethods().add(
                new EncodedMethod(
                        new MethodId(transformer_id, new ProtoId(TypeId.V, esf), "transform"),
                        Modifier.PUBLIC, null, null,
                        new CodeItem(4, 2, 3, code, null)
                )
        );

        //public boolean isVarargsCollector() {
        //    return impl.isVarargsCollector(this);
        //}
        code.clear();
        code.add(new IGetObject(0, 1, impl_field));
        code.add(new InvokeVirtual(2, new MethodId(TypeId.of(TransformerImpl.class),
                new ProtoId(TypeId.Z, mh), "isVarargsCollector"),
                0, 1, 0, 0, 0));
        code.add(new MoveResult(0));
        code.add(new Return(0));

        transformer_def.getClassData().getVirtualMethods().add(
                new EncodedMethod(
                        new MethodId(transformer_id,
                                new ProtoId(TypeId.Z), "isVarargsCollector"),
                        Modifier.PUBLIC, null, null,
                        new CodeItem(2, 1, 2, code, null)
                )
        );

        //public MethodHandle asVarargsCollector(Class<?> arrayType) {
        //    return impl.asVarargsCollector(this, arrayType);
        //}
        code.clear();
        code.add(new IGetObject(0, 1, impl_field));
        code.add(new InvokeVirtual(3, new MethodId(TypeId.of(TransformerImpl.class),
                new ProtoId(mh, mh, TypeId.of(Class.class)), "asVarargsCollector"),
                0, 1, 2, 0, 0));
        code.add(new MoveResultObject(0));
        code.add(new ReturnObject(0));

        transformer_def.getClassData().getVirtualMethods().add(
                new EncodedMethod(
                        new MethodId(transformer_id, new ProtoId(mh, TypeId.of(Class.class)),
                                "asVarargsCollector"),
                        Modifier.PUBLIC, null, null,
                        new CodeItem(3, 2, 3, code, null)
                )
        );

        //public MethodHandle asFixedArity() {
        //    return impl.asFixedArity(this);
        //}
        code.clear();
        code.add(new IGetObject(0, 1, impl_field));
        code.add(new InvokeVirtual(2, new MethodId(TypeId.of(TransformerImpl.class),
                new ProtoId(mh, mh), "asFixedArity"),
                0, 1, 0, 0, 0));
        code.add(new MoveResultObject(0));
        code.add(new ReturnObject(0));

        transformer_def.getClassData().getVirtualMethods().add(
                new EncodedMethod(
                        new MethodId(transformer_id,
                                new ProtoId(mh), "asFixedArity"),
                        Modifier.PUBLIC, null, null,
                        new CodeItem(2, 1, 2, code, null)
                )
        );

        //public MethodHandle asType(MethodType type) {
        //    return impl.asType(this, type);
        //}
        code.clear();
        code.add(new IGetObject(0, 1, impl_field));
        code.add(new InvokeVirtual(3, new MethodId(TypeId.of(TransformerImpl.class),
                new ProtoId(mh, mh, mt), "asType"),
                0, 1, 2, 0, 0));
        code.add(new MoveResultObject(0));
        code.add(new ReturnObject(0));

        transformer_def.getClassData().getVirtualMethods().add(
                new EncodedMethod(
                        new MethodId(transformer_id,
                                new ProtoId(mh, mt), "asType"),
                        Modifier.PUBLIC, null, null,
                        new CodeItem(3, 2, 3, code, null)
                )
        );

        //public String toString() {
        //    return impl.toString(this);
        //}
        code.clear();
        code.add(new IGetObject(0, 1, impl_field));
        code.add(new InvokeVirtual(2, new MethodId(TypeId.of(TransformerImpl.class),
                new ProtoId(TypeId.of(String.class), mh), "toString"),
                0, 1, 0, 0, 0));
        code.add(new MoveResultObject(0));
        code.add(new ReturnObject(0));

        transformer_def.getClassData().getVirtualMethods().add(
                new EncodedMethod(
                        new MethodId(transformer_id, new ProtoId(
                                TypeId.of(String.class)), "toString"),
                        Modifier.PUBLIC, null, null,
                        new CodeItem(2, 1, 2, code, null)
                )
        );

        //public final class Invoker extends InvokerI {
        //    <...>
        //}
        String invoker_name = Transformers.class.getName() + "$Invoker";
        TypeId invoker_id = TypeId.of(invoker_name);

        ClassDef invoker_def = new ClassDef(invoker_id);
        invoker_def.setSuperClass(TypeId.of(InvokerI.class));
        invoker_def.setAccessFlags(Modifier.PUBLIC | Modifier.FINAL);


        //public void invokeExactWithFrame(MethodHandle handle, Object stack) {
        //    <...>
        //}
        if (getSdkInt() < 33) {
            //handle.invoke((dalvik.system.EmulatedStackFrame) stack);
            code.clear();
            code.add(new CheckCast(2, esf));
            code.add(new InvokePolymorphic(2, new MethodId(mh,
                    new ProtoId(TypeId.of(Object.class),
                            TypeId.of(Object[].class)), "invoke"),
                    1, 2, 0, 0, 0, new ProtoId(TypeId.V, esf)));
            code.add(new ReturnVoid());
        } else {
            Method tmp = getDeclaredMethod(MethodHandle.class,
                    "invokeExactWithFrame", EmulatedStackFrame.esf_class);
            int flags = getExecutableAccessFlags(tmp);
            setExecutableAccessFlags(tmp, flags | Modifier.PUBLIC);
            fullFence();

            //handle.invokeExactWithFrame((dalvik.system.EmulatedStackFrame) stack);
            code.clear();
            code.add(new CheckCast(2, esf));
            code.add(new InvokeVirtual(2, MethodId.of(tmp), 1, 2, 0, 0, 0));
            code.add(new ReturnVoid());
        }

        invoker_def.getClassData().getVirtualMethods().add(
                new EncodedMethod(
                        new MethodId(invoker_id,
                                new ProtoId(TypeId.V, mh, TypeId.of(Object.class)),
                                "invokeExactWithFrame"),
                        Modifier.PUBLIC, null, null,
                        new CodeItem(3, 3, 2, code, null)
                )
        );

        Method tmp = getDeclaredMethod(MethodHandle.class,
                "transform", EmulatedStackFrame.esf_class);
        int flags = getExecutableAccessFlags(tmp);
        setExecutableAccessFlags(tmp, flags | Modifier.PUBLIC);
        fullFence();

        //public void transform(MethodHandle handle, Object stack) {
        //    handle.transform((dalvik.system.EmulatedStackFrame) stack);
        //}
        code.clear();
        code.add(new CheckCast(2, esf));
        code.add(new InvokeVirtual(2, MethodId.of(tmp), 1, 2, 0, 0, 0));
        code.add(new ReturnVoid());

        invoker_def.getClassData().getVirtualMethods().add(
                new EncodedMethod(
                        new MethodId(invoker_id, new ProtoId(TypeId.V,
                                TypeId.of(MethodHandle.class), TypeId.of(Object.class)),
                                "transform"),
                        Modifier.PUBLIC, null, null,
                        new CodeItem(3, 3, 2, code, null)
                )
        );

        //noinspection deprecation
        DexFile dex = openDexFile(new Dex(transformer_def, invoker_def).compile());
        setTrusted(dex);

        ClassLoader loader = Transformers.class.getClassLoader();
        //noinspection unchecked
        transformer = (Class<MethodHandle>) loadClass(dex, transformer_name, loader);
        Class<?> invoker_class = loadClass(dex, invoker_name, loader);
        invoker = (InvokerI) allocateInstance(invoker_class);

        transformer_constructor = nothrows_run(() -> transformer.getDeclaredConstructor(
                MethodType.class, TransformerImpl.class));
    }

    private static MethodHandle makeTransformer(MethodType type, TransformerImpl impl) {
        return nothrows_run(() -> transformer_constructor.newInstance(type, impl));
    }

    public static MethodHandle makeTransformer(MethodType type, TransformerF callback) {
        return makeTransformer(type, regularImpl(callback));
    }

    private static MethodHandle makeVarargsTransformer(MethodType fixed, TransformerImpl impl) {
        MethodHandle out = nothrows_run(() -> transformer_constructor.newInstance(fixed, impl));
        if (getSdkInt() < 33) {
            //TODO: improve
            MethodHandleMirror[] m = arrayCast(MethodHandleMirror.class, out);
            m[0].handleKind = /*INVOKE_CALLSITE_TRANSFORM*/ 6;
        }
        return out;
    }

    public static MethodHandle makeVarargsTransformer(MethodType fixed, TransformerF callback) {
        return makeVarargsTransformer(fixed, varargsImpl(fixed, callback));
    }

    public static MethodHandle makeVarargsTransformer(TransformerF callback) {
        return makeVarargsTransformer(MethodType.methodType(void.class), callback);
    }

    private abstract static class InvokerI {

        abstract void transform(MethodHandle handle, Object stackFrame) throws Throwable;

        abstract void invokeExactWithFrame(MethodHandle handle,
                                           Object stackFrame) throws Throwable;
    }

    @FunctionalInterface
    public interface TransformerI extends TransformerF {

        void transform(EmulatedStackFrame stackFrame) throws Throwable;


        default void transform(MethodHandle ignored, EmulatedStackFrame stackFrame) throws Throwable {
            transform(stackFrame);
        }
    }

    @FunctionalInterface
    public interface TransformerF {

        void transform(MethodHandle thiz, EmulatedStackFrame stackFrame) throws Throwable;
    }

    private static String toStringTransformer(MethodType type, boolean isVararg) {
        StringBuilder sb = new StringBuilder();
        sb.append("Transformer(");
        for (int i = 0; i < type.parameterCount(); i++) {
            if (i > 0) sb.append(",");
            sb.append(type.parameterType(i).getSimpleName());
        }
        if (isVararg) {
            if (type.parameterCount() > 0) {
                sb.append(", ");
            }
            sb.append("...");
        }
        sb.append(")");
        sb.append(type.returnType().getSimpleName());
        return sb.toString();
    }

    private static TransformerImpl regularImpl(TransformerF callback) {
        return new TransformerImpl() {
            @Override
            void transform(MethodHandle thiz, EmulatedStackFrame stackFrame) throws Throwable {
                callback.transform(thiz, stackFrame);
            }

            @Override
            boolean isVarargsCollector(MethodHandle thiz) {
                return false;
            }

            @Override
            MethodHandle asFixedArity(MethodHandle thiz) {
                return thiz;
            }

            @Override
            MethodHandle asType(MethodHandle thiz, MethodType newType) {
                return nothrows_run(() -> (MethodHandle) directAsType.invoke(thiz, newType));
            }

            @Override
            MethodHandle asVarargsCollector(MethodHandle thiz, Class<?> arrayType) {
                return (MethodHandle) nothrows_run(
                        () -> directAsVarargsCollector.invoke(thiz, arrayType));
            }

            @Override
            String toString(MethodHandle thiz) {
                return toStringTransformer(thiz.type(), false);
            }
        };
    }

    //TODO: checks
    private static TransformerImpl varargsImpl(MethodType fixed, TransformerF callback) {
        return new TransformerImpl() {
            @Override
            void transform(MethodHandle thiz, EmulatedStackFrame stackFrame) throws Throwable {
                callback.transform(thiz, stackFrame);
            }

            @Override
            boolean isVarargsCollector(MethodHandle ignored) {
                return true;
            }

            @Override
            MethodHandle asFixedArity(MethodHandle thiz) {
                return asType(thiz, fixed);
            }

            @Override
            MethodHandle asType(MethodHandle ignored, MethodType newType) {
                //TODO: maybe caching?
                return makeTransformer(newType, callback);
            }

            @Override
            MethodHandle asVarargsCollector(MethodHandle thiz, Class<?> arrayType) {
                return thiz;
            }

            @Override
            String toString(MethodHandle thiz) {
                return toStringTransformer(thiz.type(), true);
            }
        };
    }

    private abstract static class TransformerImpl {
        abstract void transform(MethodHandle thiz, EmulatedStackFrame stackFrame) throws Throwable;

        abstract boolean isVarargsCollector(MethodHandle thiz);

        abstract MethodHandle asFixedArity(MethodHandle thiz);

        abstract MethodHandle asType(MethodHandle thiz, MethodType newType);

        abstract MethodHandle asVarargsCollector(MethodHandle thiz, Class<?> arrayType);

        abstract String toString(MethodHandle thiz);
    }

    public static void throwWrongMethodTypeException(MethodType from, MethodType to) {
        throw new WrongMethodTypeException("Cannot convert " + from + " to " + to);
    }

    public static void invokeExactWithFrameNoChecks(
            MethodHandle target, EmulatedStackFrame stackFrame) throws Throwable {
        if (invoke_transformer.isInstance(target)) {
            //TODO: android 8-12L nominalType to type
            invoker.transform(target, stackFrame.esf);
        } else {
            invoker.invokeExactWithFrame(target, stackFrame.esf);
        }
    }

    public static void invokeExactWithFrame(
            MethodHandle target, EmulatedStackFrame stackFrame) throws Throwable {
        if (!target.type().equals(stackFrame.type())) {
            throwWrongMethodTypeException(stackFrame.type(), target.type());
        }
        invokeExactWithFrameNoChecks(target, stackFrame);
    }

    public static void invokeWithFrame(
            MethodHandle target, EmulatedStackFrame stackFrame) throws Throwable {
        MethodHandle adaptedTarget = target.asType(stackFrame.type());
        invokeExactWithFrameNoChecks(adaptedTarget, stackFrame);
    }
}
