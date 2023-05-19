package com.v7878.unsafe.methodhandle;

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
import com.v7878.unsafe.dex.bytecode.Instruction;
import com.v7878.unsafe.dex.bytecode.InvokeKind;
import com.v7878.unsafe.dex.bytecode.InvokePolymorphic;
import com.v7878.unsafe.dex.bytecode.MoveResultObject;
import com.v7878.unsafe.dex.bytecode.ReturnVoid;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import dalvik.system.DexFile;

public class Transformers {

    @SuppressWarnings("unchecked")
    private static final Class<MethodHandle> invoke_transformer = nothrows_run(
            () -> (Class<MethodHandle>) Class.forName("java.lang.invoke.Transformers$Transformer"));
    private static final Class<MethodHandle> transformer;
    private static final Constructor<MethodHandle> transformer_constructor;
    private static final InvokerI invoker;

    static {
        TypeId esf = TypeId.of("dalvik.system.EmulatedStackFrame");
        TypeId mesf = TypeId.of(EmulatedStackFrame.class);

        String transformer_name = Transformers.class.getName() + "$Transformer";
        TypeId transformer_id = TypeId.of(transformer_name);

        ClassDef transformer_def = new ClassDef(transformer_id);
        transformer_def.setSuperClass(TypeId.of(invoke_transformer));
        transformer_def.setAccessFlags(Modifier.PUBLIC | Modifier.FINAL);

        FieldId callback_field = new FieldId(transformer_id,
                TypeId.of(TransformerI.class), "callback");

        transformer_def.getClassData().getInstanceFields().add(
                new EncodedField(callback_field,
                        Modifier.PUBLIC, null)
        );

        PCList<Instruction> code = PCList.empty();
        code.add(new InvokeKind.InvokeDirect(2,
                MethodId.constructor(TypeId.of(invoke_transformer),
                        TypeId.of(MethodType.class)),
                0, 1, 0, 0, 0));
        code.add(new IInstanceOp.IPutObject(2, 0, callback_field));
        code.add(new ReturnVoid());

        transformer_def.getClassData().getDirectMethods().add(
                new EncodedMethod(
                        MethodId.constructor(transformer_id,
                                TypeId.of(MethodType.class),
                                TypeId.of(TransformerI.class)),
                        Modifier.PUBLIC | 0x10000,
                        null, null,
                        new CodeItem(
                                3, 3, 2,
                                code, null
                        )
                )
        );

        code.clear();
        code.add(new IInstanceOp.IGetObject(0, 2, callback_field));
        code.add(new InvokeKind.InvokeStatic(1,
                new MethodId(mesf, new ProtoId(mesf,
                        TypeId.of(Object.class)), "wrap"),
                3, 0, 0, 0, 0));
        code.add(new MoveResultObject(1));
        code.add(new InvokeKind.InvokeInterface(2,
                new MethodId(TypeId.of(TransformerI.class),
                        new ProtoId(TypeId.V, mesf),
                        "transform"),
                0, 1, 0, 0, 0));
        code.add(new ReturnVoid());

        transformer_def.getClassData().getVirtualMethods().add(
                new EncodedMethod(
                        new MethodId(transformer_id,
                                new ProtoId(TypeId.V, esf),
                                "transform"),
                        Modifier.PUBLIC,
                        null, null,
                        new CodeItem(
                                4, 2, 2,
                                code, null
                        )
                )
        );

        String invoker_name = Transformers.class.getName() + "$Invoker";
        TypeId invoker_id = TypeId.of(invoker_name);

        ClassDef invoker_def = new ClassDef(invoker_id);
        invoker_def.setSuperClass(TypeId.of(Object.class));
        invoker_def.getInterfaces().add(TypeId.of(InvokerI.class));
        invoker_def.setAccessFlags(Modifier.PUBLIC | Modifier.FINAL);

        if (getSdkInt() < 33) {
            code.clear();
            code.add(new CheckCast(2, esf));
            code.add(new InvokePolymorphic(2,
                    new MethodId(TypeId.of(MethodHandle.class),
                            new ProtoId(TypeId.of(Object.class),
                                    TypeId.of(Object[].class)),
                            "invoke"),
                    1, 2, 0, 0, 0,
                    new ProtoId(TypeId.V, esf)));
            code.add(new ReturnVoid());
        } else {
            Method tmp = getDeclaredMethod(MethodHandle.class,
                    "invokeExactWithFrame", EmulatedStackFrame.esf_class);
            int flags = getExecutableAccessFlags(tmp);
            setExecutableAccessFlags(tmp, flags | Modifier.PUBLIC);
            fullFence();

            code.clear();
            code.add(new CheckCast(2, esf));
            code.add(new InvokeKind.InvokeVirtual(2,
                    MethodId.of(tmp),
                    1, 2, 0, 0, 0));
            code.add(new ReturnVoid());
        }

        invoker_def.getClassData().getVirtualMethods().add(
                new EncodedMethod(
                        new MethodId(invoker_id,
                                new ProtoId(TypeId.V,
                                        TypeId.of(MethodHandle.class),
                                        TypeId.of(Object.class)),
                                "invokeExactWithFrame"),
                        Modifier.PUBLIC,
                        null, null,
                        new CodeItem(
                                3, 3, 2,
                                code, null
                        )
                )
        );

        Method tmp = getDeclaredMethod(MethodHandle.class,
                "transform", EmulatedStackFrame.esf_class);
        int flags = getExecutableAccessFlags(tmp);
        setExecutableAccessFlags(tmp, flags | Modifier.PUBLIC);
        fullFence();

        code.clear();
        code.add(new CheckCast(2, esf));
        code.add(new InvokeKind.InvokeVirtual(2,
                MethodId.of(tmp),
                1, 2, 0, 0, 0));
        code.add(new ReturnVoid());

        invoker_def.getClassData().getVirtualMethods().add(
                new EncodedMethod(
                        new MethodId(invoker_id,
                                new ProtoId(TypeId.V,
                                        TypeId.of(MethodHandle.class),
                                        TypeId.of(Object.class)),
                                "transform"),
                        Modifier.PUBLIC,
                        null, null,
                        new CodeItem(
                                3, 3, 2,
                                code, null
                        )
                )
        );

        //noinspection deprecation
        DexFile dex = openDexFile(new Dex(transformer_def,
                invoker_def).compile());
        setTrusted(dex);

        ClassLoader loader = Transformers.class.getClassLoader();
        //noinspection unchecked
        transformer = (Class<MethodHandle>) loadClass(dex, transformer_name, loader);
        Class<?> invoker_class = loadClass(dex, invoker_name, loader);
        invoker = (InvokerI) allocateInstance(invoker_class);

        transformer_constructor = nothrows_run(
                () -> transformer.getDeclaredConstructor(
                        MethodType.class, TransformerI.class));
    }

    public static MethodHandle makeTransformer(MethodType type, TransformerI impl) {
        return nothrows_run(() -> transformer_constructor.newInstance(type, impl));
    }

    private interface InvokerI {

        void transform(MethodHandle handle,
                       Object stackFrame) throws Throwable;

        void invokeExactWithFrame(MethodHandle handle,
                                  Object stackFrame) throws Throwable;
    }

    @FunctionalInterface
    public interface TransformerI {

        void transform(EmulatedStackFrame stackFrame) throws Throwable;
    }

    public static void invokeFromTransform(MethodHandle target,
                                           EmulatedStackFrame stackFrame) throws Throwable {
        if (invoke_transformer.isInstance(target)) {
            invoker.transform(target, stackFrame.esf);
        } else {
            final MethodHandle adaptedTarget = target.asType(stackFrame.type());
            invoker.invokeExactWithFrame(adaptedTarget, stackFrame.esf);
        }
    }

    public static void invokeExactFromTransform(MethodHandle target,
                                                EmulatedStackFrame stackFrame) throws Throwable {
        if (invoke_transformer.isInstance(target)) {
            invoker.transform(target, stackFrame.esf);
        } else {
            invoker.invokeExactWithFrame(target, stackFrame.esf);
        }
    }
}
