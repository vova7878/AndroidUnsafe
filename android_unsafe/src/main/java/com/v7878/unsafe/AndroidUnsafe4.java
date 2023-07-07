package com.v7878.unsafe;

import static com.v7878.unsafe.Utils.assert_;
import static com.v7878.unsafe.Utils.isSigned32Bit;
import static com.v7878.unsafe.Utils.nothrows_run;
import static com.v7878.unsafe.Utils.roundUp;
import static com.v7878.unsafe.Utils.runOnce;

import androidx.annotation.Keep;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Objects;
import java.util.function.Supplier;

@DangerLevel(4)
public class AndroidUnsafe4 extends AndroidUnsafe3 {

    @Keep
    public static class ArrayMirror {

        public int length;
    }

    @Keep
    public static class StringMirror {

        public static final boolean COMPACT_STRINGS = nothrows_run(() -> {
            StringMirror[] test = arrayCast(StringMirror.class, "\uffff");
            if (test[0].count == 3) {
                return true;
            }
            if (test[0].count == 1) {
                return false;
            }
            throw new IllegalStateException("" + test[0].count);
        });

        public int count;
        public int hash;
    }

    public static final int OBJECT_ALIGNMENT_SHIFT = 3;
    public static final int OBJECT_ALIGNMENT = 1 << OBJECT_ALIGNMENT_SHIFT;
    public static final int OBJECT_INSTANCE_SIZE = objectSizeField(Object.class);

    public static final int OBJECT_FIELD_SIZE_SHIFT = 2;
    public static final int OBJECT_FIELD_SIZE = 1 << OBJECT_FIELD_SIZE_SHIFT;

    private static final Supplier<Field> shadow$_klass_ =
            runOnce(() -> getDeclaredField(Object.class, "shadow$_klass_"));
    private static final Supplier<Field> shadow$_monitor_ =
            runOnce(() -> getDeclaredField(Object.class, "shadow$_monitor_"));
    private static final Supplier<Object> vmruntime = runOnce(() -> {
        Class<?> vmrc = nothrows_run(() -> Class.forName("dalvik.system.VMRuntime"));
        return allocateInstance(vmrc);
    });
    private static final Supplier<MethodHandle> newNonMovableArray =
            runOnce(() -> unreflectDirect(getDeclaredMethod(vmruntime.get().getClass(),
                    "newNonMovableArray", Class.class, int.class)));
    private static final Supplier<MethodHandle> addressOf =
            runOnce(() -> unreflectDirect(getDeclaredMethod(vmruntime.get().getClass(),
                    "addressOf", Object.class)));
    private static final Supplier<MethodHandle> vmLibrary =
            runOnce(() -> unreflectDirect(getDeclaredMethod(vmruntime.get().getClass(),
                    "vmLibrary")));

    private static final Supplier<MethodHandle> internalClone =
            runOnce(() -> unreflectDirect(getDeclaredMethod(Object.class, "internalClone")));

    static {
        assert_(ARRAY_OBJECT_INDEX_SCALE == OBJECT_FIELD_SIZE, RuntimeException::new,
                "ARRAY_OBJECT_INDEX_SCALE must be equal to OBJECT_FIELD_SIZE");
        assert_(ARRAY_INT_BASE_OFFSET == 12, RuntimeException::new,
                "ARRAY_INT_BASE_OFFSET must be equal to 12");
        assert_(OBJECT_INSTANCE_SIZE == 8, RuntimeException::new,
                "OBJECT_SIZE must be equal to 8");
    }

    public static String vmLibrary() {
        return (String) nothrows_run(() -> vmLibrary.get().invoke(vmruntime.get()));
    }

    public static Field getShadowKlassField() {
        return shadow$_klass_.get();
    }

    public static Field getShadowMonitorField() {
        return shadow$_monitor_.get();
    }

    @SuppressWarnings("unchecked")
    public static <T> T internalClone(T obj) {
        return (T) nothrows_run(() -> internalClone.get().invoke(obj));
    }

    @DangerLevel(DangerLevel.VERY_CAREFUL)
    @SuppressWarnings("unchecked")
    public static <T> T setObjectClass(Object obj, Class<T> clazz) {
        Field sk = getShadowKlassField();
        nothrows_run(() -> sk.set(obj, clazz));
        return (T) obj;
    }

    public static Object newNonMovableArrayVM(Class<?> componentType, int length) {
        return nothrows_run(() -> newNonMovableArray.get()
                .invoke(vmruntime.get(), componentType, length));
    }

    public static long addressOfNonMovableArrayData(Object array) {
        return (long) nothrows_run(() -> addressOf.get().invoke(vmruntime.get(), array));
    }

    public static long addressOfNonMovableArray(Object array) {
        return addressOfNonMovableArrayData(array) - arrayBaseOffset(array.getClass());
    }

    public static int getArrayLength(Object arr) {
        assert_(arr.getClass().isArray(), IllegalArgumentException::new);
        ArrayMirror[] clh = arrayCast(ArrayMirror.class, arr);
        return clh[0].length;
    }

    @DangerLevel(DangerLevel.VERY_CAREFUL)
    public static void setArrayLength(Object arr, int length) {
        assert_(arr.getClass().isArray(), IllegalArgumentException::new);
        assert_(length >= 0, IllegalArgumentException::new);
        ArrayMirror[] clh = arrayCast(ArrayMirror.class, arr);
        clh[0].length = length;
    }

    public static Object allocateObject(int size) {
        size = roundUp(size, OBJECT_ALIGNMENT);
        if (size < ARRAY_INT_BASE_OFFSET) {
            if (size <= OBJECT_INSTANCE_SIZE) {
                return new Object();
            } else {
                throw new IllegalStateException();
            }
        }
        return new int[(size - ARRAY_INT_BASE_OFFSET) / 4];
    }

    public static Object allocateNonMovableObject(int size) {
        size = roundUp(size, OBJECT_ALIGNMENT);
        if (size < ARRAY_INT_BASE_OFFSET) {
            size = roundUp(ARRAY_INT_BASE_OFFSET, OBJECT_ALIGNMENT);
        }
        return newNonMovableArrayVM(int.class, (size - ARRAY_INT_BASE_OFFSET) / 4);
    }

    public static Object allocateObject(int size, boolean nonmovable) {
        return nonmovable ? allocateNonMovableObject(size) : allocateObject(size);
    }

    public static <T> T newArrayInstance(Class<T> cls, int length, boolean nonmovable) {
        assert_(cls.isArray(), IllegalArgumentException::new);
        assert_(length >= 0, IllegalArgumentException::new);
        int size = Math.multiplyExact(arrayIndexScale(cls), length);
        size = Math.addExact(size, arrayBaseOffset(cls));
        T out = setObjectClass(allocateObject(size, nonmovable), cls);
        setArrayLength(out, length);
        return out;
    }

    public static <T> T newArrayInstance(Class<T> cls, int length) {
        return newArrayInstance(cls, length, false);
    }

    public static <T> T newNonMovableArrayInstance(Class<T> cls, int length) {
        return newArrayInstance(cls, length, true);
    }

    public static int objectSizeField(Class<?> clazz) {
        ClassMirror[] clh = arrayCast(ClassMirror.class, clazz);
        int out = clh[0].objectSize;
        assert_(out != 0, IllegalArgumentException::new, "objectSize == 0");
        return out;
    }

    public static int classSizeField(Class<?> clazz) {
        ClassMirror[] clh = arrayCast(ClassMirror.class, clazz);
        return clh[0].classSize;
    }

    public static int emptyClassSize() {
        return classSizeField(void.class);
    }

    public static boolean shouldHaveEmbeddedVTableAndImt(Class<?> clazz) {
        return clazz.isArray()
                || !(clazz.isInterface()
                || clazz.isPrimitive()
                || Modifier.isAbstract(clazz.getModifiers()));
    }

    public static int getEmbeddedVTableLength(Class<?> clazz) {
        assert_(shouldHaveEmbeddedVTableAndImt(clazz), IllegalArgumentException::new);
        return getIntO(clazz, emptyClassSize());
    }

    public static boolean isCompressedString(String s) {
        StringMirror[] sm = arrayCast(StringMirror.class, s);
        return StringMirror.COMPACT_STRINGS && ((sm[0].count & 1) == 0);
    }

    public static int sizeOf(Object obj) {
        Objects.requireNonNull(obj);
        if (obj instanceof String) {
            String sobj = (String) obj;
            int data_size = sobj.length() * (isCompressedString(sobj) ? 1 : 2);
            return roundUp(objectSizeField(StringMirror.class) + data_size, OBJECT_ALIGNMENT);
        }
        if (obj instanceof Class) {
            return classSizeField((Class<?>) obj);
        }
        Class<?> clazz = obj.getClass();
        if (clazz.isArray()) {
            return arrayBaseOffset(clazz) + arrayIndexScale(clazz) * getArrayLength(obj);
        }
        return objectSizeField(clazz);
    }

    public static int alignedSizeOf(Object obj) {
        return roundUp(sizeOf(obj), OBJECT_ALIGNMENT);
    }

    @DangerLevel(DangerLevel.BAD_GC_COLLISION)
    @SuppressWarnings("unchecked")
    public static <T> T cloneBySize(T obj, boolean nonmovable) {
        int size = sizeOf(obj);
        Object out = allocateObject(size, nonmovable);
        copyMemory(obj, 0, out, 0, size);
        return (T) out;
    }

    @DangerLevel(DangerLevel.BAD_GC_COLLISION)
    public static <T> T cloneBySize(T obj) {
        return cloneBySize(obj, false);
    }

    @DangerLevel(DangerLevel.BAD_GC_COLLISION)
    public static <T> T cloneNonMovable(T obj) {
        return cloneBySize(obj, true);
    }

    @DangerLevel(DangerLevel.POTENTIAL_GC_COLLISION)
    public static byte[] dumpObject(Object obj) {
        if (obj == null) {
            return null;
        }
        int size = alignedSizeOf(obj);
        byte[] out = new byte[size];
        copyMemory(obj, 0, out, ARRAY_BYTE_BASE_OFFSET, size);
        return out;
    }

    @DangerLevel(DangerLevel.BAD_GC_COLLISION)
    public static Object makeObject(byte[] dump, boolean nonmovable) {
        if (dump == null) {
            return null;
        }
        int size = dump.length;
        Object out = allocateObject(size, nonmovable);
        copyMemory(dump, ARRAY_BYTE_BASE_OFFSET, out, 0, size);
        return out;
    }

    @DangerLevel(DangerLevel.BAD_GC_COLLISION)
    public static Object makeObject(byte[] dump) {
        return makeObject(dump, false);
    }

    @DangerLevel(DangerLevel.BAD_GC_COLLISION)
    public static Object makeNonMovableObject(byte[] dump) {
        return makeObject(dump, true);
    }

    @DangerLevel(DangerLevel.POTENTIAL_GC_COLLISION)
    public static int rawObjectToInt(Object obj) {
        Object[] arr = new Object[1];
        arr[0] = obj;
        return getInt(arr, ARRAY_OBJECT_BASE_OFFSET);
    }

    @DangerLevel(DangerLevel.POTENTIAL_GC_COLLISION)
    public static void putObjectRaw(long address, Object value) {
        putIntN(address, rawObjectToInt(value));
    }

    @DangerLevel(DangerLevel.GC_COLLISION_MOVABLE_OBJECTS)
    public static Object rawIntToObject(int obj) {
        int[] arr = new int[1];
        arr[0] = obj;
        return getObject(arr, ARRAY_INT_BASE_OFFSET);
    }

    @DangerLevel(DangerLevel.GC_COLLISION_MOVABLE_OBJECTS)
    public static Object getObjectRaw(long address) {
        return rawIntToObject(getIntN(address));
    }

    protected static final Supplier<Boolean> kPoisonReferences = runOnce(() -> {
        Object test = allocateNonMovableObject(0);
        long address = addressOfNonMovableArray(test);
        assert_(isSigned32Bit(address), IllegalStateException::new);
        int real = (int) address;
        int raw = rawObjectToInt(test);
        if (real == raw) {
            return false;
        } else if (real == -raw) {
            return true;
        } else {
            throw new IllegalStateException(real + " " + raw);
        }
    });

    @DangerLevel(DangerLevel.POTENTIAL_GC_COLLISION)
    public static int objectToInt(Object obj) {
        int out = rawObjectToInt(obj);
        return kPoisonReferences.get() ? -out : out;
    }

    @DangerLevel(DangerLevel.GC_COLLISION_MOVABLE_OBJECTS)
    public static Object intToObject(int obj) {
        return rawIntToObject(kPoisonReferences.get() ? -obj : obj);
    }

    private static final Supplier<Class<?>> voidArrayClass = runOnce(() -> {
        Class<?> out = cloneBySize(int[].class);
        ClassMirror[] clh = arrayCast(ClassMirror.class, out);
        clh[0].componentType = void.class;
        clh[0].name = null;
        return out;
    });

    @DangerLevel(DangerLevel.MAX)
    public static Class<?> getVoidArrayClass() {
        return voidArrayClass.get();
    }
}
