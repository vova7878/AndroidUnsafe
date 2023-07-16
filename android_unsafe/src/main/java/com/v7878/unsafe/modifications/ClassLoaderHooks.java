package com.v7878.unsafe.modifications;

import static com.v7878.unsafe.AndroidUnsafe.fullFence;
import static com.v7878.unsafe.AndroidUnsafe3.arrayCast;
import static com.v7878.unsafe.AndroidUnsafe3.getDeclaredField;
import static com.v7878.unsafe.AndroidUnsafe3.getMethods;
import static com.v7878.unsafe.AndroidUnsafe4.objectSizeField;
import static com.v7878.unsafe.AndroidUnsafe4.setObjectClass;
import static com.v7878.unsafe.AndroidUnsafe5.getExecutableAccessFlags;
import static com.v7878.unsafe.AndroidUnsafe5.loadClass;
import static com.v7878.unsafe.AndroidUnsafe5.replaceExecutableAccessModifier;
import static com.v7878.unsafe.AndroidUnsafe5.setExecutableAccessFlags;
import static com.v7878.unsafe.AndroidUnsafe5.setTrusted;
import static com.v7878.unsafe.Utils.assert_;
import static com.v7878.unsafe.Utils.nothrows_run;
import static com.v7878.unsafe.Utils.searchMethod;

import com.v7878.dex.ClassDef;
import com.v7878.dex.Dex;
import com.v7878.dex.EncodedField;
import com.v7878.dex.EncodedMethod;
import com.v7878.dex.FieldId;
import com.v7878.dex.MethodId;
import com.v7878.dex.ProtoId;
import com.v7878.dex.TypeId;
import com.v7878.dex.bytecode.CodeBuilder;
import com.v7878.unsafe.AndroidUnsafe3.ClassMirror;
import com.v7878.unsafe.AndroidUnsafe5;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Objects;
import java.util.function.BiFunction;

import dalvik.system.DexFile;

@SuppressWarnings("deprecation")
public class ClassLoaderHooks {

    private static final Object LOCK = new Object();

    @FunctionalInterface
    public interface FindClassI {
        Class<?> findClass(ClassLoader thiz, String name) throws ClassNotFoundException;
    }

    private static BiFunction<ClassLoader, String, Class<?>> itf(FindClassI impl) {
        return (thiz, name) -> nothrows_run(() -> impl.findClass(thiz, name));
    }

    static {
        //resolve all required classes to prevent recursion
        BiFunction<ClassLoader, String, Class<?>> dummy = itf((thiz, name) -> null);
        dummy.apply(null, null);
    }

    public static void hookFindClass(ClassLoader loader, FindClassI impl) {
        Objects.requireNonNull(loader);
        Objects.requireNonNull(impl);
        synchronized (LOCK) {
            Class<?> lc = loader.getClass();
            {
                //TODO: safer way
                ClassMirror[] cm = arrayCast(ClassMirror.class, lc);
                cm[0].accessFlags &= ~Modifier.FINAL;
                cm[0].accessFlags &= ~(Modifier.PUBLIC | Modifier.PROTECTED | Modifier.PRIVATE);
                cm[0].accessFlags |= Modifier.PUBLIC;
                fullFence();
            }
            Method fc = searchMethod(getMethods(lc), "findClass", String.class);
            if (Modifier.isFinal(fc.getModifiers())) {
                //TODO: safer way
                int real_flags = getExecutableAccessFlags(fc);
                real_flags &= ~Modifier.FINAL;
                setExecutableAccessFlags(fc, real_flags);
                replaceExecutableAccessModifier(fc, AndroidUnsafe5.AccessModifier.PUBLIC);
            }

            String hook_name = lc.getName() + "$$$SyntheticHook";
            TypeId hook_id = TypeId.of(hook_name);
            ClassDef hook_clazz = new ClassDef(hook_id);
            hook_clazz.setSuperClass(TypeId.of(lc));
            FieldId impl_f_id = new FieldId(hook_id, TypeId.of(BiFunction.class), "impl");
            hook_clazz.getClassData().getStaticFields().add(new EncodedField(
                    impl_f_id, Modifier.STATIC, null
            ));
            hook_clazz.getClassData().getVirtualMethods().add(new EncodedMethod(
                    new MethodId(hook_id, new ProtoId(TypeId.of(Class.class),
                            TypeId.of(String.class)), "findClass"),
                    Modifier.PUBLIC).withCode(1, b -> b
                    .sop(CodeBuilder.Op.GET_OBJECT, b.l(0), impl_f_id)
                    .invoke(CodeBuilder.InvokeKind.INTERFACE, new MethodId(TypeId.of(BiFunction.class), new ProtoId(
                                    TypeId.of(Object.class), TypeId.of(Object.class),
                                    TypeId.of(Object.class)), "apply"),
                            b.l(0), b.this_(), b.p(0))
                    .move_result_object(b.l(0))
                    .check_cast(b.l(0), TypeId.of(Class.class))
                    .if_testz(CodeBuilder.Test.EQ, b.l(0), ":null")
                    .return_object(b.l(0))
                    .label(":null")
                    .invoke(CodeBuilder.InvokeKind.SUPER, MethodId.of(fc), b.this_(), b.p(0))
                    .move_result_object(b.l(0))
                    .return_object(b.l(0))
            ));

            DexFile dex = AndroidUnsafe5.openDexFile(new Dex(hook_clazz).compile());
            setTrusted(dex);
            Class<?> hook = loadClass(dex, hook_name, lc.getClassLoader());
            Field impl_f = getDeclaredField(hook, "impl");
            nothrows_run(() -> impl_f.set(null, itf(impl)));

            assert_(objectSizeField(lc) == objectSizeField(hook), AssertionError::new);

            setObjectClass(loader, hook);
        }
    }
}
