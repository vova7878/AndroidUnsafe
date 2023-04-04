package com.v7878.unsafe.methodhandle;

import static com.v7878.unsafe.AndroidUnsafe5.*;
import static com.v7878.unsafe.Utils.nothrows_run;
import com.v7878.unsafe.dex.*;
import com.v7878.unsafe.dex.bytecode.*;
import dalvik.system.DexFile;
import java.lang.invoke.*;
import java.lang.reflect.*;

public class Transformers {

    private static final Class<MethodHandle> invoke_transformer = nothrows_run(() -> {
        return (Class<MethodHandle>) Class.forName("java.lang.invoke.Transformers$Transformer");
    });
    private static final Class<MethodHandle> transformer;
    private static final Constructor<MethodHandle> transformer_constructor;

    static {
        TypeId esf = TypeId.of("dalvik.system.EmulatedStackFrame");
        TypeId mesf = TypeId.of(EmulatedStackFrame.class);

        String transformer_name = Transformers.class.getName() + "$Transformer";
        TypeId transformer_id = TypeId.of(transformer_name);

        ClassDef transformer_def = new ClassDef(transformer_id);
        transformer_def.setSuperClass(TypeId.of(invoke_transformer));
        transformer_def.setAccessFlags(Modifier.PUBLIC);

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

        DexFile dex = openDexFile(new Dex(transformer_def).compile());
        setTrusted(dex);

        ClassLoader loader = Transformers.class.getClassLoader();
        transformer = (Class<MethodHandle>) loadClass(dex, transformer_name, loader);

        transformer_constructor = nothrows_run(() -> {
            return transformer.getDeclaredConstructor(
                    MethodType.class, TransformerI.class);
        });
    }

    public static MethodHandle makeTransformer(MethodType type, TransformerI impl) {
        return nothrows_run(() -> transformer_constructor.newInstance(type, impl));
    }

    @FunctionalInterface
    public interface TransformerI {

        public void transform(EmulatedStackFrame stackFrame) throws Throwable;
    }

    private static final Method invokeExactWithFrame = nothrows_run(() -> {
        return getDeclaredMethod(MethodHandle.class,
                "invokeExactWithFrame", EmulatedStackFrame.esf_class);
    });

    private static final Method transform = nothrows_run(() -> {
        return getDeclaredMethod(MethodHandle.class,
                "transform", EmulatedStackFrame.esf_class);
    });

    public static void invokeFromTransform(MethodHandle target,
            EmulatedStackFrame stackFrame) throws Throwable {
        if (invoke_transformer.isInstance(target)) {
            transform.invoke(target, stackFrame.esf);
        } else {
            final MethodHandle adaptedTarget = target.asType(stackFrame.getMethodType());
            invokeExactWithFrame.invoke(adaptedTarget, stackFrame.esf);
        }
    }

    public static void invokeExactFromTransform(MethodHandle target,
            EmulatedStackFrame stackFrame) throws Throwable {
        if (invoke_transformer.isInstance(target)) {
            transform.invoke(target, stackFrame.esf);
        } else {
            invokeExactWithFrame.invoke(target, stackFrame.esf);
        }
    }
}
