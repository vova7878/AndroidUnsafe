package com.v7878.unsafe.methodhandle;

import static com.v7878.unsafe.AndroidUnsafe3.ClassMirror;
import static com.v7878.unsafe.AndroidUnsafe3.arrayCast;
import static com.v7878.unsafe.AndroidUnsafe3.unreflectDirect;
import static com.v7878.unsafe.AndroidUnsafe5.allocateInstance;
import static com.v7878.unsafe.AndroidUnsafe5.getDeclaredMethod;
import static com.v7878.unsafe.AndroidUnsafe5.loadClass;
import static com.v7878.unsafe.AndroidUnsafe5.openDexFile;
import static com.v7878.unsafe.AndroidUnsafe5.replaceExecutableAccessModifier;
import static com.v7878.unsafe.AndroidUnsafe5.setTrusted;
import static com.v7878.unsafe.AndroidUnsafe7.setClassStatus;
import static com.v7878.unsafe.Utils.getSdkInt;
import static com.v7878.unsafe.Utils.nothrows_run;
import static com.v7878.unsafe.dex.bytecode.CodeBuilder.InvokeKind.DIRECT;
import static com.v7878.unsafe.dex.bytecode.CodeBuilder.InvokeKind.STATIC;
import static com.v7878.unsafe.dex.bytecode.CodeBuilder.InvokeKind.VIRTUAL;
import static com.v7878.unsafe.dex.bytecode.CodeBuilder.Op.GET_OBJECT;
import static com.v7878.unsafe.dex.bytecode.CodeBuilder.Op.PUT_OBJECT;

import androidx.annotation.Keep;

import com.v7878.unsafe.AndroidUnsafe3.MethodHandleMirror;
import com.v7878.unsafe.AndroidUnsafe5.AccessModifier;
import com.v7878.unsafe.AndroidUnsafe7.ClassStatus;
import com.v7878.unsafe.dex.ClassDef;
import com.v7878.unsafe.dex.Dex;
import com.v7878.unsafe.dex.EncodedField;
import com.v7878.unsafe.dex.EncodedMethod;
import com.v7878.unsafe.dex.FieldId;
import com.v7878.unsafe.dex.MethodId;
import com.v7878.unsafe.dex.ProtoId;
import com.v7878.unsafe.dex.TypeId;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.lang.invoke.WrongMethodTypeException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Objects;

import dalvik.system.DexFile;

@Keep
class VariadicType {
    private VariadicType() {
    }
}

@SuppressWarnings("deprecation")
public class Transformers {

    @SuppressWarnings("unchecked")
    private static final Class<MethodHandle> invoke_transformer = (Class<MethodHandle>) nothrows_run(
            () -> Class.forName("java.lang.invoke.Transformers$Transformer"));
    private static final Constructor<MethodHandle> transformer_constructor;
    private static final MethodHandle directAsType = nothrows_run(() -> unreflectDirect(
            getDeclaredMethod(MethodHandle.class, "asType", MethodType.class)));
    private static final MethodHandle directAsVarargsCollector = nothrows_run(() -> unreflectDirect(
            getDeclaredMethod(MethodHandle.class, "asVarargsCollector", Class.class)));
    private static final MethodHandle directBindTo = nothrows_run(() -> unreflectDirect(
            getDeclaredMethod(MethodHandle.class, "bindTo", Object.class)));
    private static final InvokerI invoker;

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
        transformer_def.getClassData().getDirectMethods().add(new EncodedMethod(
                MethodId.constructor(transformer_id, mt, TypeId.of(TransformerImpl.class)),
                Modifier.PUBLIC | 0x10000).withCode(0, b -> b
                .invoke(DIRECT, MethodId.constructor(TypeId.of(invoke_transformer), mt),
                        b.this_(), b.p(0))
                .iop(PUT_OBJECT, b.p(1), b.this_(), impl_field)
                .return_void()
        ));

        //public void transform(dalvik.system.EmulatedStackFrame stack) {
        //    impl.transform(com.v7878.unsafe.methodhandle.EmulatedStackFrame.wrap(stack));
        //}
        transformer_def.getClassData().getVirtualMethods().add(new EncodedMethod(
                new MethodId(transformer_id, new ProtoId(TypeId.V, esf), "transform"),
                Modifier.PUBLIC).withCode(2, b -> b
                .iop(GET_OBJECT, b.l(0), b.this_(), impl_field)
                .invoke(STATIC, new MethodId(mesf, new ProtoId(mesf,
                        TypeId.of(Object.class)), "wrap"), b.p(0))
                .move_result_object(b.l(1))
                .invoke(VIRTUAL, new MethodId(TypeId.of(TransformerImpl.class),
                                new ProtoId(TypeId.V, mh, mesf), "transform"),
                        b.l(0), b.this_(), b.l(1))
                .return_void()
        ));

        //public boolean isVarargsCollector() {
        //    return impl.isVarargsCollector(this);
        //}
        transformer_def.getClassData().getVirtualMethods().add(new EncodedMethod(
                new MethodId(transformer_id, new ProtoId(TypeId.Z), "isVarargsCollector"),
                Modifier.PUBLIC).withCode(1, b -> b
                .iop(GET_OBJECT, b.l(0), b.this_(), impl_field)
                .invoke(VIRTUAL, new MethodId(TypeId.of(TransformerImpl.class),
                                new ProtoId(TypeId.Z, mh), "isVarargsCollector"),
                        b.l(0), b.this_())
                .move_result(b.l(0))
                .return_(b.l(0))
        ));

        //public MethodHandle asVarargsCollector(Class<?> arrayType) {
        //    return impl.asVarargsCollector(this, arrayType);
        //}
        transformer_def.getClassData().getVirtualMethods().add(new EncodedMethod(
                new MethodId(transformer_id, new ProtoId(mh, TypeId.of(Class.class)),
                        "asVarargsCollector"), Modifier.PUBLIC).withCode(1, b -> b
                .iop(GET_OBJECT, b.l(0), b.this_(), impl_field)
                .invoke(VIRTUAL, new MethodId(TypeId.of(TransformerImpl.class), new ProtoId(mh,
                                mh, TypeId.of(Class.class)), "asVarargsCollector"),
                        b.l(0), b.this_(), b.p(0))
                .move_result_object(b.l(0))
                .return_object(b.l(0))
        ));

        //public MethodHandle asFixedArity() {
        //    return impl.asFixedArity(this);
        //}
        transformer_def.getClassData().getVirtualMethods().add(new EncodedMethod(
                new MethodId(transformer_id, new ProtoId(mh), "asFixedArity"),
                Modifier.PUBLIC).withCode(1, b -> b
                .iop(GET_OBJECT, b.l(0), b.this_(), impl_field)
                .invoke(VIRTUAL, new MethodId(TypeId.of(TransformerImpl.class),
                        new ProtoId(mh, mh), "asFixedArity"), b.l(0), b.this_())
                .move_result_object(b.l(0))
                .return_object(b.l(0))
        ));

        //public MethodHandle asType(MethodType type) {
        //    return impl.asType(this, type);
        //}
        transformer_def.getClassData().getVirtualMethods().add(new EncodedMethod(
                new MethodId(transformer_id, new ProtoId(mh, mt), "asType"),
                Modifier.PUBLIC).withCode(1, b -> b
                .iop(GET_OBJECT, b.l(0), b.this_(), impl_field)
                .invoke(VIRTUAL, new MethodId(TypeId.of(TransformerImpl.class),
                                new ProtoId(mh, mh, mt), "asType"),
                        b.l(0), b.this_(), b.p(0))
                .move_result_object(b.l(0))
                .return_object(b.l(0))
        ));

        //public MethodHandle bindTo(Object value) {
        //    return impl.bindTo(this, value);
        //}
        transformer_def.getClassData().getVirtualMethods().add(new EncodedMethod(
                new MethodId(transformer_id, new ProtoId(mh, TypeId.of(Object.class)), "bindTo"),
                Modifier.PUBLIC).withCode(1, b -> b
                .iop(GET_OBJECT, b.l(0), b.this_(), impl_field)
                .invoke(VIRTUAL, new MethodId(TypeId.of(TransformerImpl.class),
                                new ProtoId(mh, mh, TypeId.of(Object.class)), "bindTo"),
                        b.l(0), b.this_(), b.p(0))
                .move_result_object(b.l(0))
                .return_object(b.l(0))
        ));

        //public String toString() {
        //    return impl.toString(this);
        //}
        transformer_def.getClassData().getVirtualMethods().add(new EncodedMethod(
                new MethodId(transformer_id, new ProtoId(TypeId.of(String.class)), "toString"),
                Modifier.PUBLIC).withCode(1, b -> b
                .iop(GET_OBJECT, b.l(0), b.this_(), impl_field)
                .invoke(VIRTUAL, new MethodId(TypeId.of(TransformerImpl.class),
                                new ProtoId(TypeId.of(String.class), mh), "toString"),
                        b.l(0), b.this_())
                .move_result_object(b.l(0))
                .return_object(b.l(0))
        ));

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
        invoker_def.getClassData().getVirtualMethods().add(new EncodedMethod(
                new MethodId(invoker_id, new ProtoId(TypeId.V, mh, TypeId.of(Object.class)),
                        "invokeExactWithFrame"), Modifier.PUBLIC).withCode(0, b -> {
            //b.check_cast(b.p(1), esf) // verified
            if (getSdkInt() <= 32) {
                //handle.invoke((dalvik.system.EmulatedStackFrame) stack);
                b.invoke_polymorphic(new MethodId(mh, new ProtoId(TypeId.of(Object.class),
                                TypeId.of(Object[].class)), "invoke"),
                        new ProtoId(TypeId.V, esf), b.p(0), b.p(1));
            } else {
                Method tmp = getDeclaredMethod(MethodHandle.class,
                        "invokeExactWithFrame", EmulatedStackFrame.esf_class);
                replaceExecutableAccessModifier(tmp, AccessModifier.PUBLIC);

                //handle.invokeExactWithFrame((dalvik.system.EmulatedStackFrame) stack);
                b.invoke(VIRTUAL, MethodId.of(tmp), b.p(0), b.p(1));
            }
            b.return_void();
        }));

        Method tmp = getDeclaredMethod(MethodHandle.class,
                "transform", EmulatedStackFrame.esf_class);
        replaceExecutableAccessModifier(tmp, AccessModifier.PUBLIC);

        //public void transform(MethodHandle handle, Object stack) {
        //    handle.transform((dalvik.system.EmulatedStackFrame) stack);
        //}
        invoker_def.getClassData().getVirtualMethods().add(new EncodedMethod(
                new MethodId(invoker_id, new ProtoId(TypeId.V, TypeId.of(MethodHandle.class),
                        TypeId.of(Object.class)), "transform"),
                Modifier.PUBLIC).withCode(0, b -> b
                //.check_cast(b.p(1), esf) // verified
                .invoke(VIRTUAL, MethodId.of(tmp), b.p(0), b.p(1))
                .return_void()
        ));

        DexFile dex = openDexFile(new Dex(transformer_def, invoker_def).compile());
        setTrusted(dex);

        ClassLoader loader = Transformers.class.getClassLoader();

        Class<?> invoker_class = loadClass(dex, invoker_name, loader);
        setClassStatus(invoker_class, ClassStatus.Verified);
        invoker = (InvokerI) allocateInstance(invoker_class);

        Class<?> transformer = loadClass(dex, transformer_name, loader);
        //noinspection unchecked
        transformer_constructor = (Constructor<MethodHandle>) nothrows_run(() -> transformer
                .getDeclaredConstructor(MethodType.class, TransformerImpl.class));

        //rename VariadicType to ?
        //TODO: it is safe?
        ClassMirror[] vc = arrayCast(ClassMirror.class, VariadicType.class);
        vc[0].name = "?";
    }

    //TODO: unsafe get ptypes
    private static boolean hasVariadicType(MethodType type) {
        if (type.returnType() == VariadicType.class) {
            return true;
        }
        for (Class<?> arg : type.parameterArray()) {
            if (arg == VariadicType.class) {
                return true;
            }
        }
        return false;
    }

    public static Class<?> variadicType() {
        return VariadicType.class;
    }

    private static MethodHandle makeTransformer(
            MethodType fixed, TransformerImpl impl, boolean variadic) {
        MethodHandle out = nothrows_run(() -> transformer_constructor.newInstance(fixed, impl));
        if (variadic && getSdkInt() < 33) {
            //TODO: safer way
            MethodHandleMirror[] m = arrayCast(MethodHandleMirror.class, out);
            m[0].handleKind = /*INVOKE_CALLSITE_TRANSFORM*/ 6;
        }
        return out;
    }

    public static MethodHandle makeTransformer(
            MethodType fixed, TransformerF callback, boolean varargs) {
        if (varargs || hasVariadicType(fixed)) {
            return makeTransformer(fixed, variadicImpl(fixed, callback, varargs), true);
        }
        return makeTransformer(fixed, regularImpl(callback), false);
    }

    public static MethodHandle makeTransformer(MethodType type, TransformerF callback) {
        return makeTransformer(type, callback, false);
    }

    //Transformer(...)?
    public static MethodHandle makeVariadicTransformer(TransformerF callback) {
        return makeTransformer(MethodType.methodType(VariadicType.class), callback, true);
    }

    @Keep
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

    @Keep
    @FunctionalInterface
    public interface TransformerF {

        void transform(MethodHandle thiz, EmulatedStackFrame stackFrame) throws Throwable;
    }

    private static Class<?> wrapPrimitive(Class<?> prim) {
        if (prim == int.class) {
            return Integer.class;
        }
        if (prim == long.class) {
            return Long.class;
        }
        if (prim == double.class) {
            return Double.class;
        }
        if (prim == float.class) {
            return Float.class;
        }
        if (prim == char.class) {
            return Character.class;
        }
        if (prim == short.class) {
            return Short.class;
        }
        if (prim == byte.class) {
            return Byte.class;
        }
        if (prim == boolean.class) {
            return Boolean.class;
        }
        throw new IllegalArgumentException(prim + " is not primitive class or void");
    }

    private static Class<?> unwrapPrimitive(Class<?> wrapper) {
        if (wrapper == Integer.class) {
            return int.class;
        }
        if (wrapper == Long.class) {
            return long.class;
        }
        if (wrapper == Double.class) {
            return double.class;
        }
        if (wrapper == Float.class) {
            return float.class;
        }
        if (wrapper == Character.class) {
            return char.class;
        }
        if (wrapper == Short.class) {
            return short.class;
        }
        if (wrapper == Byte.class) {
            return byte.class;
        }
        if (wrapper == Boolean.class) {
            return boolean.class;
        }
        return null;
    }

    private static int getWeight(Class<?> prim) {
        if (prim == boolean.class) {
            return 0;
        }
        if (prim == byte.class) {
            return 1;
        }
        if (prim == short.class) {
            return 2;
        }
        if (prim == char.class) {
            return 3;
        }
        if (prim == int.class) {
            return 4;
        }
        if (prim == long.class) {
            return 5;
        }
        if (prim == float.class) {
            return 6;
        }
        if (prim == double.class) {
            return 7;
        }
        throw new IllegalArgumentException(prim + " is not primitive class or void");
    }

    private static boolean canConvertPrimitive(Class<?> from, Class<?> to) {
        //from & to != void
        if (from == to) {
            return true;
        }
        if (getWeight(from) > getWeight(to)) {
            return false;
        }
        if (from == boolean.class || to == boolean.class) {
            return false;
        }
        return to != char.class;
    }

    private static boolean canConvert(Class<?> from, Class<?> to) {
        if (from == to || from == Object.class || to == Object.class
                || from == void.class || to == void.class) {
            return true;
        }
        if (from.isPrimitive()) {
            if (to.isPrimitive()) {
                return canConvertPrimitive(from, to);
            }
            return to.isAssignableFrom(wrapPrimitive(from));
        } else if (to.isPrimitive()) {
            if (from.isAssignableFrom(wrapPrimitive(to))) {
                return true;
            }

            from = unwrapPrimitive(from);
            if (from == null) {
                return false;
            }

            return canConvertPrimitive(from, to);
        } else {
            return true;
        }
    }

    private static Class<?> specify(Class<?> fixed, Class<?> required, boolean isArg) {
        if (fixed == required) {
            return fixed;
        }
        if (fixed == VariadicType.class) {
            return required;
        }
        if (required == VariadicType.class) {
            if (fixed == void.class || fixed == Object.class) {
                return fixed;
            }
            return null;
        }
        if (canConvert(isArg ? required : fixed, isArg ? fixed : required)) {
            return fixed;
        }
        return null;
    }

    //TODO: insert isVararg to MethodType
    //TODO: unsafe get rtype/ptypes;
    private static MethodType specifyNoThrow(MethodType fixed, boolean fixedIsVararg,
                                             MethodType required, boolean requiredIsVararg) {
        Objects.requireNonNull(fixed);
        Objects.requireNonNull(required);
        if (fixedIsVararg) {
            if (!requiredIsVararg) {
                if (required.parameterCount() < fixed.parameterCount()) {
                    //f = (?, ?, ...)?
                    //r = (?)?
                    return null;
                }
            }
        } else {
            if (requiredIsVararg) {
                //f = (?)?
                //r = (?, ...)?
                return null;
            } else if (fixed.parameterCount() != required.parameterCount()) {
                //f = (?)?
                //r = (?, ?)?
                return null;
            }
        }

        Class<?> rtype = specify(fixed.returnType(), required.returnType(), false);
        if (rtype == null) {
            return null;
        }

        int arg_count = Math.min(fixed.parameterCount(), required.parameterCount());
        Class<?>[] ptypes = new Class[Math.max(fixed.parameterCount(), required.parameterCount())];

        for (int i = 0; i < arg_count; i++) {
            ptypes[i] = specify(fixed.parameterType(i), required.parameterType(i), true);
            if (ptypes[i] == null) {
                return null;
            }
        }

        if (arg_count != ptypes.length) {
            Class<?>[] tmp = fixed.parameterCount() > arg_count ?
                    fixed.parameterArray() : required.parameterArray();
            System.arraycopy(tmp, arg_count, ptypes, arg_count, tmp.length - arg_count);
        }

        return MethodType.methodType(rtype, ptypes);
    }


    private static MethodType specify(MethodType fixed, boolean fixedIsVararg,
                                      MethodType required, boolean requiredIsVararg) {
        MethodType out = specifyNoThrow(fixed, fixedIsVararg, required, requiredIsVararg);
        if (out == null) {
            throw newWrongMethodTypeException(fixed, fixedIsVararg, required, requiredIsVararg);
        }
        return out;
    }

    /*//TODO
    private static EmulatedStackFrame convert(EmulatedStackFrame from,
                                              MethodType to, boolean toIsVararg) {
    }*/


    //TODO: insert isVararg to MethodType
    private static String toStringMethodType(MethodType type, boolean isVararg) {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for (int i = 0; i < type.parameterCount(); i++) {
            if (i > 0) sb.append(",");
            sb.append(type.parameterType(i).getSimpleName());
        }
        if (isVararg) {
            if (type.parameterCount() > 0) {
                sb.append(',');
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
                //TODO: maybe caching?
                if (hasVariadicType(newType)) {
                    //TODO
                    throw new UnsupportedOperationException("Not implemented yet");
                }
                return nothrows_run(() -> (MethodHandle) directAsType.invoke(thiz, newType));
            }

            @Override
            MethodHandle bindTo(MethodHandle thiz, Object value) {
                //TODO: bindTo with first primitive parameter
                return nothrows_run(() -> (MethodHandle) directBindTo.invoke(thiz, value));
            }

            @Override
            MethodHandle asVarargsCollector(MethodHandle thiz, Class<?> arrayType) {
                return (MethodHandle) nothrows_run(
                        () -> directAsVarargsCollector.invoke(thiz, arrayType));
            }

            @Override
            String toString(MethodHandle thiz) {
                return "Transformer" + toStringMethodType(thiz.type(), false);
            }
        };
    }

    private static TransformerImpl variadicImpl(
            MethodType fixed, TransformerF callback, boolean varargs) {

        final boolean full_variadic = varargs && (fixed.parameterCount() == 0)
                && (fixed.returnType() == VariadicType.class);
        return new TransformerImpl() {

            @Override
            void transform(MethodHandle thiz, EmulatedStackFrame stackFrame) throws Throwable {
                if (full_variadic) {
                    callback.transform(thiz, stackFrame);
                } else {
                    if (getSdkInt() <= 32) {
                        invokeExactWithFrameNoChecks(asType(thiz, stackFrame.type()), stackFrame);
                    } else {
                        //TODO: maybe check?
                        callback.transform(thiz, stackFrame);
                    }
                }
            }

            @Override
            boolean isVarargsCollector(MethodHandle ignored) {
                return varargs;
            }

            @Override
            MethodHandle asFixedArity(MethodHandle thiz) {
                return asType(thiz, thiz.type());
            }

            @Override
            MethodHandle asType(MethodHandle thiz, MethodType newType) {
                //TODO: maybe caching?
                if (full_variadic) {
                    return makeTransformer(newType, callback);
                } else {
                    if (thiz.type().equals(newType)) {
                        if (varargs) {
                            return makeTransformer(newType, callback);
                        }
                        return thiz;
                    }
                    MethodType specified = specify(thiz.type(), varargs, newType, false);
                    if (getSdkInt() >= 33 && !hasVariadicType(newType)) {
                        return (MethodHandle) nothrows_run(() -> directAsType.invoke(thiz, newType));
                    }
                    //TODO
                    throw new UnsupportedOperationException("Not implemented yet");
                }
            }

            @Override
            MethodHandle bindTo(MethodHandle thiz, Object value) {
                //TODO
                throw new UnsupportedOperationException("Not implemented yet");
            }

            @Override
            MethodHandle asVarargsCollector(MethodHandle thiz, Class<?> arrayType) {
                if (varargs) {
                    return thiz;
                }
                //TODO
                throw new UnsupportedOperationException("Not implemented yet");
            }

            @Override
            String toString(MethodHandle thiz) {
                return "Transformer" + toStringMethodType(thiz.type(), true);
            }
        };
    }

    @Keep
    private abstract static class TransformerImpl {
        abstract void transform(MethodHandle thiz, EmulatedStackFrame stackFrame) throws Throwable;

        abstract boolean isVarargsCollector(MethodHandle thiz);

        abstract MethodHandle asVarargsCollector(MethodHandle thiz, Class<?> arrayType);

        abstract MethodHandle asFixedArity(MethodHandle thiz);

        abstract MethodHandle asType(MethodHandle thiz, MethodType newType);

        abstract MethodHandle bindTo(MethodHandle thiz, Object value);

        abstract String toString(MethodHandle thiz);
    }

    /*
    //TODO: bind to index
    //TODO
    private static class VariadicBindTo implements TransformerF {
        private final TransformerF delegate;
        private final Object receiver;

        VariadicBindTo(TransformerF delegate, Object receiver) {
            this.delegate = delegate;
            this.receiver = receiver;
        }

        @Override
        public void transform(MethodHandle thiz, EmulatedStackFrame stackFrame) throws Throwable {
            // Create a new emulated stack frame with the full type (including the leading
            // receiver reference).
            EmulatedStackFrame stackFrame = EmulatedStackFrame.create(delegate.type());

            // The first reference argument must be the receiver.
            stackFrame.setReference(0, receiver);
            // Copy all other arguments.
            emulatedStackFrame.copyRangeTo(stackFrame, range, 1, 0);

            // Perform the invoke.
            invokeFromTransform(delegate, stackFrame);
            stackFrame.copyReturnValueTo(emulatedStackFrame);

        }
    }*/

    private static WrongMethodTypeException newWrongMethodTypeException(MethodType from, MethodType to) {
        return new WrongMethodTypeException("Cannot convert " + from + " to " + to);
    }

    private static WrongMethodTypeException newWrongMethodTypeException(
            MethodType from, boolean fromIsVararg, MethodType to, boolean toIsVararg) {
        return new WrongMethodTypeException(
                "Cannot convert " + toStringMethodType(from, fromIsVararg)
                        + " to " + toStringMethodType(to, toIsVararg));
    }

    public static void invokeExactWithFrameNoChecks(
            MethodHandle target, EmulatedStackFrame stackFrame) throws Throwable {
        if (invoke_transformer.isInstance(target)) {
            //FIXME: android 8-12L convert nominalType to type
            invoker.transform(target, stackFrame.esf);
        } else {
            invoker.invokeExactWithFrame(target, stackFrame.esf);
        }
    }

    public static void invokeExactWithFrame(
            MethodHandle target, EmulatedStackFrame stackFrame) throws Throwable {
        if (!target.type().equals(stackFrame.type())) {
            throw newWrongMethodTypeException(stackFrame.type(), target.type());
        }
        invokeExactWithFrameNoChecks(target, stackFrame);
    }

    public static void invokeWithFrame(
            MethodHandle target, EmulatedStackFrame stackFrame) throws Throwable {
        MethodHandle adaptedTarget = target.asType(stackFrame.type());
        invokeExactWithFrameNoChecks(adaptedTarget, stackFrame);
    }
}
