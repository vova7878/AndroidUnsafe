package com.v7878.unsafe;

import static com.v7878.unsafe.Utils.assert_;
import static com.v7878.unsafe.Utils.getSdkInt;
import static com.v7878.unsafe.Utils.runOnce;
import static com.v7878.unsafe.Utils.searchMethod;

import androidx.annotation.Keep;

import com.v7878.unsafe.dex.AnnotationItem;
import com.v7878.unsafe.dex.AnnotationSet;
import com.v7878.unsafe.dex.ClassDef;
import com.v7878.unsafe.dex.Dex;
import com.v7878.unsafe.dex.EncodedMethod;
import com.v7878.unsafe.dex.MethodId;
import com.v7878.unsafe.dex.ProtoId;
import com.v7878.unsafe.dex.TypeId;
import com.v7878.unsafe.function.LibArt;
import com.v7878.unsafe.function.SymbolLookup;
import com.v7878.unsafe.memory.Pointer;
import com.v7878.unsafe.memory.Word;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.function.Supplier;

import dalvik.system.DexFile;

@DangerLevel(7)
public class AndroidUnsafe7 extends AndroidUnsafe6 {
    public enum ClassStatus {
        NotReady,  // Zero-initialized Class object starts in this state.
        Retired,  // Retired, should not be used. Use the newly cloned one instead.
        ErrorResolved,
        ErrorUnresolved,
        Idx,  // Loaded, DEX idx in super_class_type_idx_ and interfaces_type_idx_.
        Loaded,  // DEX idx values resolved.
        Resolving,  // Just cloned from temporary class object.
        Resolved,  // Part of linking.
        Verifying,  // In the process of being verified.
        RetryVerificationAtRuntime,  // Compile time verification failed, retry at runtime.
        Verified,  // Logically part of linking; done pre-init.
        Initializing,  // Class init in progress.
        Initialized;  // Ready to go.

        static {
            switch (getSdkInt()) {
                case 34: // android 14
                case 33: // android 13
                case 32: // android 12L
                case 31: // android 12
                case 30: // android 11
                case 29: // android 10
                case 28: // android 9
                    NotReady.value = 0;
                    Retired.value = 1;
                    ErrorResolved.value = 2;
                    ErrorUnresolved.value = 3;
                    Idx.value = 4;
                    Loaded.value = 5;
                    Resolving.value = 6;
                    Resolved.value = 7;
                    Verifying.value = 8;
                    RetryVerificationAtRuntime.value = 9;
                    Verified.value = 11;
                    Initializing.value = 13;
                    Initialized.value = 14;
                    break;
                case 27: // android 8.1
                    NotReady.value = 0;
                    Retired.value = -3;
                    ErrorResolved.value = -2;
                    ErrorUnresolved.value = -1;
                    Idx.value = 1;
                    Loaded.value = 2;
                    Resolving.value = 3;
                    Resolved.value = 4;
                    Verifying.value = 5;
                    RetryVerificationAtRuntime.value = 6;
                    Verified.value = 8;
                    Initializing.value = 10;
                    Initialized.value = 11;
                    break;
                case 26: // android 8
                    NotReady.value = 0;
                    Retired.value = -3;
                    ErrorResolved.value = -2;
                    ErrorUnresolved.value = -1;
                    Idx.value = 1;
                    Loaded.value = 2;
                    Resolving.value = 3;
                    Resolved.value = 4;
                    Verifying.value = 5;
                    RetryVerificationAtRuntime.value = 6;
                    Verified.value = 8;
                    Initializing.value = 9;
                    Initialized.value = 10;
                    break;
                default:
                    throw new IllegalStateException("unsupported sdk: " + getSdkInt());
            }
        }

        private int value;

        public int getValue() {
            return value;
        }
    }

    public static int getRawClassStatus(Class<?> clazz) {
        ClassMirror[] mirror = arrayCast(ClassMirror.class, clazz);
        return getSdkInt() <= 27 ? mirror[0].status : (mirror[0].status >>> 32 - 4);
    }

    public static ClassStatus getClassStatus(Class<?> clazz) {
        int status = getRawClassStatus(clazz);
        for (ClassStatus tmp : ClassStatus.values()) {
            if (tmp.value == status) {
                return tmp;
            }
        }
        //TODO: maybe throw exception?
        return null;
    }

    @DangerLevel(DangerLevel.VERY_CAREFUL)
    public static void setRawClassStatus(Class<?> clazz, int status) {
        ClassMirror[] mirror = arrayCast(ClassMirror.class, clazz);
        if (getSdkInt() <= 27) {
            mirror[0].status = status;
        } else {
            mirror[0].status = (mirror[0].status & ~0 >>> 4) | (status << 32 - 4);
        }
    }

    @DangerLevel(DangerLevel.VERY_CAREFUL)
    public static void setClassStatus(Class<?> clazz, ClassStatus status) {
        setRawClassStatus(clazz, status.value);
    }

    private interface LocalRefUtils {
        default long NewLocalRef64(long env, Object obj) {
            throw new AssertionError();
        }

        default int NewLocalRef32(int env, Object obj) {
            throw new AssertionError();
        }

        default void DeleteLocalRef64(long env, long ref) {
            throw new AssertionError();
        }

        default void DeleteLocalRef32(int env, int ref) {
            throw new AssertionError();
        }

        default void PushLocalFrame64(long env, int capacity) {
            throw new AssertionError();
        }

        default void PushLocalFrame32(int env, int capacity) {
            throw new AssertionError();
        }

        default void PopLocalFrame64(long env) {
            throw new AssertionError();
        }

        default void PopLocalFrame32(int env) {
            throw new AssertionError();
        }

        default void putRef64(Object obj, long offset, long ref) {
            throw new AssertionError();
        }

        default void putRef32(Object obj, long offset, int ref) {
            throw new AssertionError();
        }
    }

    private static final Supplier<LocalRefUtils> localRefUtils = runOnce(() -> {
        //TODO: what if kPoisonReferences == true?
        //make sure reinterpret_cast<int>(obj) == addressof(obj)
        assert_(!kPoisonReferences.get(), AssertionError::new);

        Class<?> word = IS64BIT ? long.class : int.class;
        TypeId word_id = TypeId.of(word);
        String name = AndroidUnsafe7.class.getName() + "LocalRefUtils";
        TypeId id = TypeId.of(name);
        ClassDef clazz = new ClassDef(id);
        clazz.setSuperClass(TypeId.of(Object.class));
        clazz.getInterfaces().add(TypeId.of(LocalRefUtils.class));
        clazz.setAccessFlags(Modifier.PUBLIC | Modifier.FINAL);

        MethodId nlr_id = new MethodId(id, new ProtoId(word_id, word_id, word_id), "NewLocalRef");
        clazz.getClassData().getDirectMethods().add(new EncodedMethod(
                nlr_id, Modifier.PUBLIC | Modifier.STATIC | Modifier.NATIVE,
                new AnnotationSet(
                        AnnotationItem.CriticalNative()
                ), null, null
        ));

        //it's broken: object is cast to pointer
        if (IS64BIT) {
            //pointer 64-bit, object 32-bit -> fill upper 32 bits with zeros (l0 register)
            clazz.getClassData().getVirtualMethods().add(new EncodedMethod(
                    new MethodId(id, new ProtoId(TypeId.J, TypeId.J,
                            TypeId.of(Object.class)), "NewLocalRef64"),
                    Modifier.PUBLIC).withCode(1, b -> b
                    //FIXME
                    //.const_4(0)
                    .invoke_static(nlr_id, b.p(0), b.p(1), b.p(2), b.l(0))
                    .move_result_wide(b.v(0))
                    .return_wide(b.v(0))
            ));
        } else {
            clazz.getClassData().getVirtualMethods().add(new EncodedMethod(
                    new MethodId(id, new ProtoId(TypeId.I, TypeId.I,
                            TypeId.of(Object.class)), "NewLocalRef32"),
                    Modifier.PUBLIC).withCode(0, b -> b
                    .invoke_static(nlr_id, b.p(0), b.p(1))
                    .move_result(b.v(0))
                    .return_(b.v(0))
            ));
        }

        MethodId dlr_id = new MethodId(id, new ProtoId(TypeId.V, word_id, word_id), "DeleteLocalRef");
        clazz.getClassData().getDirectMethods().add(new EncodedMethod(
                dlr_id, Modifier.PUBLIC | Modifier.STATIC | Modifier.NATIVE,
                new AnnotationSet(
                        AnnotationItem.CriticalNative()
                ), null, null
        ));

        clazz.getClassData().getVirtualMethods().add(new EncodedMethod(
                new MethodId(id, new ProtoId(TypeId.V, word_id, word_id),
                        IS64BIT ? "DeleteLocalRef64" : "DeleteLocalRef32"),
                Modifier.PUBLIC).withCode(0, b -> (IS64BIT ?
                b.invoke_static(dlr_id, b.p(0), b.p(1), b.p(2), b.p(3)) :
                b.invoke_static(dlr_id, b.p(0), b.p(1)))
                .return_void()
        ));

        MethodId push_id = new MethodId(id, new ProtoId(TypeId.V, word_id, TypeId.I), "PushLocalFrame");
        clazz.getClassData().getDirectMethods().add(new EncodedMethod(
                push_id, Modifier.PUBLIC | Modifier.STATIC | Modifier.NATIVE,
                new AnnotationSet(
                        AnnotationItem.CriticalNative()
                ), null, null
        ));

        clazz.getClassData().getVirtualMethods().add(new EncodedMethod(
                new MethodId(id, new ProtoId(TypeId.V, word_id, TypeId.I),
                        IS64BIT ? "PushLocalFrame64" : "PushLocalFrame32"),
                Modifier.PUBLIC).withCode(0, b -> (IS64BIT ?
                b.invoke_static(push_id, b.p(0), b.p(1), b.p(2)) :
                b.invoke_static(push_id, b.p(0), b.p(1)))
                .return_void()
        ));

        MethodId pop_id = new MethodId(id, new ProtoId(TypeId.V, word_id), "PopLocalFrame");
        clazz.getClassData().getDirectMethods().add(new EncodedMethod(
                pop_id, Modifier.PUBLIC | Modifier.STATIC | Modifier.NATIVE,
                new AnnotationSet(
                        AnnotationItem.CriticalNative()
                ), null, null
        ));

        clazz.getClassData().getVirtualMethods().add(new EncodedMethod(
                new MethodId(id, new ProtoId(TypeId.V, word_id),
                        IS64BIT ? "PopLocalFrame64" : "PopLocalFrame32"),
                Modifier.PUBLIC).withCode(0, b -> (IS64BIT ?
                b.invoke_static(pop_id, b.p(0), b.p(1)) :
                b.invoke_static(pop_id, b.p(0)))
                .return_void()
        ));

        clazz.getClassData().getVirtualMethods().add(new EncodedMethod(
                new MethodId(id, new ProtoId(TypeId.V, TypeId.of(Object.class),
                        TypeId.J, word_id), IS64BIT ? "putRef64" : "putRef32"),
                Modifier.PUBLIC | Modifier.FINAL | Modifier.NATIVE,
                new AnnotationSet(AnnotationItem.FastNative()), null, null
        ));

        //noinspection deprecation
        DexFile dex = openDexFile(new Dex(clazz).compile());
        Class<?> utils = loadClass(dex, name, AndroidUnsafe7.class.getClassLoader());
        setClassStatus(utils, ClassStatus.Verified);

        Method[] methods = getDeclaredMethods(utils);

        try (SymbolLookup art = LibArt.open()) {
            Pointer nlr = art.lookup("_ZN3art9JNIEnvExt11NewLocalRefEPNS_6mirror6ObjectE");
            setExecutableData(searchMethod(methods, "NewLocalRef", word, word), nlr);

            Pointer dlr = art.lookup("_ZN3art9JNIEnvExt14DeleteLocalRefEP8_jobject");
            setExecutableData(searchMethod(methods, "DeleteLocalRef", word, word), dlr);

            Pointer push = art.lookup("_ZN3art9JNIEnvExt9PushFrameEi");
            setExecutableData(searchMethod(methods, "PushLocalFrame", word, int.class), push);

            Pointer pop = art.lookup("_ZN3art9JNIEnvExt8PopFrameEv");
            setExecutableData(searchMethod(methods, "PopLocalFrame", word), pop);
        }

        Method put = getDeclaredMethod(SunUnsafe.getUnsafeClass(), "putObject",
                Object.class, long.class, Object.class);
        assert_(Modifier.isNative(put.getModifiers()), AssertionError::new);
        setExecutableData(searchMethod(methods, IS64BIT ? "putRef64" : "putRef32",
                Object.class, long.class, word), getExecutableData(put));

        return (LocalRefUtils) allocateInstance(utils);
    });

    public static Word NewLocalRef(Object obj) {
        LocalRefUtils utils = localRefUtils.get();
        Pointer env = getCurrentEnvPtr();
        if (IS64BIT) {
            return new Word(utils.NewLocalRef64(env.getRawAddress(), obj));
        } else {
            return new Word(utils.NewLocalRef32((int) env.getRawAddress(), obj));
        }
    }

    public static void DeleteLocalRef(Word ref) {
        LocalRefUtils utils = localRefUtils.get();
        Pointer env = getCurrentEnvPtr();
        if (IS64BIT) {
            utils.DeleteLocalRef64(env.getRawAddress(), ref.longValue());
        } else {
            utils.DeleteLocalRef32((int) env.getRawAddress(), ref.intValue());
        }
    }

    public static void PushLocalFrame(int capacity) {
        //TODO: capacity checks
        LocalRefUtils utils = localRefUtils.get();
        Pointer env = getCurrentEnvPtr();
        if (IS64BIT) {
            utils.PushLocalFrame64(env.getRawAddress(), capacity);
        } else {
            utils.PushLocalFrame32((int) env.getRawAddress(), capacity);
        }
    }

    public static void PopLocalFrame() {
        LocalRefUtils utils = localRefUtils.get();
        Pointer env = getCurrentEnvPtr();
        if (IS64BIT) {
            utils.PopLocalFrame64(env.getRawAddress());
        } else {
            utils.PopLocalFrame32((int) env.getRawAddress());
        }
    }

    @Keep
    public static Object refToObject(Word ref) {
        Object[] arr = new Object[1];
        final long offset = ARRAY_OBJECT_BASE_OFFSET;
        if (IS64BIT) {
            localRefUtils.get().putRef64(arr, offset, ref.longValue());
        } else {
            localRefUtils.get().putRef32(arr, offset, ref.intValue());
        }
        return arr[0];
    }

    public static class ScopedLocalRef implements AutoCloseable {

        private final Word ref;

        public ScopedLocalRef(Object obj) {
            ref = NewLocalRef(obj);
        }

        public Word get() {
            return ref;
        }

        @Override
        public void close() {
            DeleteLocalRef(ref);
        }
    }

    public static class LocalFrame implements AutoCloseable {

        public LocalFrame(int capacity) {
            PushLocalFrame(capacity);
        }

        public Word newRef(Object obj) {
            return NewLocalRef(obj);
        }

        @Override
        public void close() {
            PopLocalFrame();
        }
    }
}
