package com.v7878.unsafe;

import static com.v7878.unsafe.Utils.assert_;
import static com.v7878.unsafe.Utils.nothrows_run;

import android.annotation.TargetApi;
import android.os.Build;

import com.v7878.Thrower;

import java.lang.reflect.Field;
import java.nio.ByteOrder;
import java.util.Objects;

@DangerLevel(1)
@TargetApi(Build.VERSION_CODES.O)
public class AndroidUnsafe {

    public static final int ADDRESS_SIZE = addressSize();

    static {
        assert_((ADDRESS_SIZE == 4) || (ADDRESS_SIZE == 8), RuntimeException::new,
                "ADDRESS_SIZE must be equal to 4 or 8");
    }

    public static final boolean IS64BIT = ADDRESS_SIZE == 8;

    public static boolean UNALIGNED_ACCESS = false;

    static {
        String arch = System.getProperty("os.arch");
        if (arch != null) {
            UNALIGNED_ACCESS = arch.equals("i386") || arch.equals("x86")
                    || arch.equals("amd64") || arch.equals("x86_64");
        }
    }

    public static final int ARRAY_BOOLEAN_BASE_OFFSET = arrayBaseOffset(boolean[].class);
    public static final int ARRAY_BYTE_BASE_OFFSET = arrayBaseOffset(byte[].class);
    public static final int ARRAY_SHORT_BASE_OFFSET = arrayBaseOffset(short[].class);
    public static final int ARRAY_CHAR_BASE_OFFSET = arrayBaseOffset(char[].class);
    public static final int ARRAY_INT_BASE_OFFSET = arrayBaseOffset(int[].class);
    public static final int ARRAY_LONG_BASE_OFFSET = arrayBaseOffset(long[].class);
    public static final int ARRAY_FLOAT_BASE_OFFSET = arrayBaseOffset(float[].class);
    public static final int ARRAY_DOUBLE_BASE_OFFSET = arrayBaseOffset(double[].class);
    public static final int ARRAY_OBJECT_BASE_OFFSET = arrayBaseOffset(Object[].class);

    public static final int ARRAY_BOOLEAN_INDEX_SCALE = arrayIndexScale(boolean[].class);
    public static final int ARRAY_BYTE_INDEX_SCALE = arrayIndexScale(byte[].class);
    public static final int ARRAY_SHORT_INDEX_SCALE = arrayIndexScale(short[].class);
    public static final int ARRAY_CHAR_INDEX_SCALE = arrayIndexScale(char[].class);
    public static final int ARRAY_INT_INDEX_SCALE = arrayIndexScale(int[].class);
    public static final int ARRAY_LONG_INDEX_SCALE = arrayIndexScale(long[].class);
    public static final int ARRAY_FLOAT_INDEX_SCALE = arrayIndexScale(float[].class);
    public static final int ARRAY_DOUBLE_INDEX_SCALE = arrayIndexScale(double[].class);
    public static final int ARRAY_OBJECT_INDEX_SCALE = arrayIndexScale(Object[].class);

    public static boolean unalignedAccess() {
        return UNALIGNED_ACCESS;
    }

    public static final boolean isBigEndian() {
        return ByteOrder.nativeOrder().equals(ByteOrder.BIG_ENDIAN);
    }

    public static void throwException(Throwable th) {
        Thrower.throwException(th);
    }

    public static void park(boolean absolute, long time) {
        SunUnsafe.park(absolute, time);
    }

    public static void unpark(Object obj) {
        SunUnsafe.unpark(obj);
    }

    public static void loadFence() {
        SunUnsafe.loadFence();
    }

    public static void storeFence() {
        SunUnsafe.storeFence();
    }

    public static void fullFence() {
        SunUnsafe.fullFence();
    }

    public static int addressSize() {
        return SunUnsafe.addressSize();
    }

    public static int pageSize() {
        return SunUnsafe.pageSize();
    }

    public static long allocateMemory(long bytes) {
        return SunUnsafe.allocateMemory(bytes);
    }

    public static void freeMemory(long address) {
        SunUnsafe.freeMemory(address);
    }

    public static void setMemory(long address, long bytes, byte value) {
        SunUnsafe.setMemory(address, bytes, value);
    }

    public static void copyMemory(long srcAddr, long dstAddr, long bytes) {
        SunUnsafe.copyMemory(srcAddr, dstAddr, bytes);
    }

    public static <T> T allocateInstance(Class<T> clazz) {
        return (T) nothrows_run(() -> SunUnsafe.allocateInstance(clazz));
    }

    public static long objectFieldOffset(Field field) {
        return SunUnsafe.objectFieldOffset(field);
    }

    public static int arrayBaseOffset(Class<?> clazz) {
        if (clazz.getComponentType() == void.class) {
            clazz = byte[].class;
        }
        int out = SunUnsafe.arrayBaseOffset(clazz);
        if (out == 0) {
            throw new IllegalArgumentException();
        }
        return out;
    }

    public static int arrayIndexScale(Class<?> clazz) {
        if (clazz.getComponentType() == void.class) {
            clazz = byte[].class;
        }
        int out = SunUnsafe.arrayIndexScale(clazz);
        if (out == 0) {
            throw new IllegalArgumentException();
        }
        return out;
    }

    public static boolean getBooleanO(Object obj, long offset) {
        return SunUnsafe.getBoolean(obj, offset);
    }

    public static void putBooleanO(Object obj, long offset, boolean value) {
        SunUnsafe.putBoolean(obj, offset, value);
    }

    public static byte getByteO(Object obj, long offset) {
        return SunUnsafe.getByte(obj, offset);
    }

    public static void putByteO(Object obj, long offset, byte value) {
        SunUnsafe.putByte(obj, offset, value);
    }

    public static char getCharO(Object obj, long offset) {
        return SunUnsafe.getChar(obj, offset);
    }

    public static void putCharO(Object obj, long offset, char value) {
        SunUnsafe.putChar(obj, offset, value);
    }

    public static short getShortO(Object obj, long offset) {
        return SunUnsafe.getShort(obj, offset);
    }

    public static void putShortO(Object obj, long offset, short value) {
        SunUnsafe.putShort(obj, offset, value);
    }

    public static int getIntO(Object obj, long offset) {
        return SunUnsafe.getInt(obj, offset);
    }

    public static void putIntO(Object obj, long offset, int value) {
        SunUnsafe.putInt(obj, offset, value);
    }

    public static float getFloatO(Object obj, long offset) {
        return SunUnsafe.getFloat(obj, offset);
    }

    public static void putFloatO(Object obj, long offset, float value) {
        SunUnsafe.putFloat(obj, offset, value);
    }

    public static long getLongO(Object obj, long offset) {
        return SunUnsafe.getLong(obj, offset);
    }

    public static void putLongO(Object obj, long offset, long value) {
        SunUnsafe.putLong(obj, offset, value);
    }

    public static double getDoubleO(Object obj, long offset) {
        return SunUnsafe.getDouble(obj, offset);
    }

    public static void putDoubleO(Object obj, long offset, double value) {
        SunUnsafe.putDouble(obj, offset, value);
    }

    public static boolean getBooleanN(long address) {
        return SunUnsafe.getBoolean(address);
    }

    public static void putBooleanN(long address, boolean value) {
        SunUnsafe.putBoolean(address, value);
    }

    public static byte getByteN(long address) {
        return SunUnsafe.getByte(address);
    }

    public static void putByteN(long address, byte value) {
        SunUnsafe.putByte(address, value);
    }

    public static char getCharN(long address) {
        return SunUnsafe.getChar(address);
    }

    public static void putCharN(long address, char value) {
        SunUnsafe.putChar(address, value);
    }

    public static short getShortN(long address) {
        return SunUnsafe.getShort(address);
    }

    public static void putShortN(long address, short value) {
        SunUnsafe.putShort(address, value);
    }

    public static int getIntN(long address) {
        return SunUnsafe.getInt(address);
    }

    public static void putIntN(long address, int value) {
        SunUnsafe.putInt(address, value);
    }

    public static float getFloatN(long address) {
        return SunUnsafe.getFloat(address);
    }

    public static void putFloatN(long address, float value) {
        SunUnsafe.putFloat(address, value);
    }

    public static long getLongN(long address) {
        return SunUnsafe.getLong(address);
    }

    public static void putLongN(long address, long value) {
        SunUnsafe.putLong(address, value);
    }

    public static double getDoubleN(long address) {
        return SunUnsafe.getDouble(address);
    }

    public static void putDoubleN(long address, double value) {
        SunUnsafe.putDouble(address, value);
    }

    public static boolean getBoolean(Object obj, long offset) {
        if (obj == null) {
            return SunUnsafe.getBoolean(offset);
        } else {
            return SunUnsafe.getBoolean(obj, offset);
        }
    }

    public static void putBoolean(Object obj, long offset, boolean value) {
        if (obj == null) {
            SunUnsafe.putBoolean(offset, value);
        } else {
            SunUnsafe.putBoolean(obj, offset, value);
        }
    }

    public static byte getByte(Object obj, long offset) {
        if (obj == null) {
            return SunUnsafe.getByte(offset);
        } else {
            return SunUnsafe.getByte(obj, offset);
        }
    }

    public static void putByte(Object obj, long offset, byte value) {
        if (obj == null) {
            SunUnsafe.putByte(offset, value);
        } else {
            SunUnsafe.putByte(obj, offset, value);
        }
    }

    public static char getChar(Object obj, long offset) {
        if (obj == null) {
            return SunUnsafe.getChar(offset);
        } else {
            return SunUnsafe.getChar(obj, offset);
        }
    }

    public static void putChar(Object obj, long offset, char value) {
        if (obj == null) {
            SunUnsafe.putChar(offset, value);
        } else {
            SunUnsafe.putChar(obj, offset, value);
        }
    }

    public static short getShort(Object obj, long offset) {
        if (obj == null) {
            return SunUnsafe.getShort(offset);
        } else {
            return SunUnsafe.getShort(obj, offset);
        }
    }

    public static void putShort(Object obj, long offset, short value) {
        if (obj == null) {
            SunUnsafe.putShort(offset, value);
        } else {
            SunUnsafe.putShort(obj, offset, value);
        }
    }

    public static int getInt(Object obj, long offset) {
        if (obj == null) {
            return SunUnsafe.getInt(offset);
        } else {
            return SunUnsafe.getInt(obj, offset);
        }
    }

    public static void putInt(Object obj, long offset, int value) {
        if (obj == null) {
            SunUnsafe.putInt(offset, value);
        } else {
            SunUnsafe.putInt(obj, offset, value);
        }
    }

    public static float getFloat(Object obj, long offset) {
        if (obj == null) {
            return SunUnsafe.getFloat(offset);
        } else {
            return SunUnsafe.getFloat(obj, offset);
        }
    }

    public static void putFloat(Object obj, long offset, float value) {
        if (obj == null) {
            SunUnsafe.putFloat(offset, value);
        } else {
            SunUnsafe.putFloat(obj, offset, value);
        }
    }

    public static long getLong(Object obj, long offset) {
        if (obj == null) {
            return SunUnsafe.getLong(offset);
        } else {
            return SunUnsafe.getLong(obj, offset);
        }
    }

    public static void putLong(Object obj, long offset, long value) {
        if (obj == null) {
            SunUnsafe.putLong(offset, value);
        } else {
            SunUnsafe.putLong(obj, offset, value);
        }
    }

    public static double getDouble(Object obj, long offset) {
        if (obj == null) {
            return SunUnsafe.getDouble(offset);
        } else {
            return SunUnsafe.getDouble(obj, offset);
        }
    }

    public static void putDouble(Object obj, long offset, double value) {
        if (obj == null) {
            SunUnsafe.putDouble(offset, value);
        } else {
            SunUnsafe.putDouble(obj, offset, value);
        }
    }

    public static long getWordO(Object obj, long offset, boolean is_64_bit) {
        return is_64_bit ? getLongO(obj, offset) : getIntO(obj, offset) & 0xffffffffL;
    }

    public static long getWordO(Object obj, long offset) {
        return getWordO(obj, offset, IS64BIT);
    }

    public static void putWordO(Object obj, long offset, long value, boolean is_64_bit) {
        if (is_64_bit) {
            putLongO(obj, offset, value);
        } else {
            putIntO(obj, offset, (int) value);
        }
    }

    public static void putWordO(Object obj, long offset, long value) {
        putWordO(obj, offset, value, IS64BIT);
    }

    public static long getWordN(long address, boolean is_64_bit) {
        return is_64_bit ? getLongN(address) : getIntN(address) & 0xffffffffL;
    }

    public static long getWordN(long address) {
        return getWordN(address, IS64BIT);
    }

    public static void putWordN(long address, long value, boolean is_64_bit) {
        if (is_64_bit) {
            putLongN(address, value);
        } else {
            putIntN(address, (int) value);
        }
    }

    public static void putWordN(long address, long value) {
        putWordN(address, value, IS64BIT);
    }

    public static long getWord(Object obj, long offset, boolean is_64_bit) {
        return is_64_bit ? getLong(obj, offset) : getInt(obj, offset) & 0xffffffffL;
    }

    public static long getWord(Object obj, long offset) {
        return getWord(obj, offset, IS64BIT);
    }

    public static void putWord(Object obj, long offset, long value, boolean is_64_bit) {
        if (is_64_bit) {
            putLong(obj, offset, value);
        } else {
            putInt(obj, offset, (int) value);
        }
    }

    public static void putWord(Object obj, long offset, long value) {
        putWord(obj, offset, value, IS64BIT);
    }

    public static Object getObject(Object obj, long offset) {
        Objects.requireNonNull(obj);
        return SunUnsafe.getObject(obj, offset);
    }

    public static void putObject(Object obj, long offset, Object value) {
        Objects.requireNonNull(obj);
        SunUnsafe.putObject(obj, offset, value);
    }

    /*public static boolean compareAndSwapInt(Object obj, long offset,
            int expectedValue, int value) {
        return Unsafe.compareAndSwapInt(obj, offset, expectedValue, value);
    }

    public static boolean compareAndSwapLong(Object obj, long offset,
            long expectedValue, long value) {
        return Unsafe.compareAndSwapLong(obj, offset, expectedValue, value);
    }

    public static boolean compareAndSwapObject(Object obj, long offset,
            Object expectedValue, Object value) {
        return Unsafe.compareAndSwapObject(obj, offset, expectedValue, value);
    }

    public static int getIntVolatile(Object obj, long offset) {
        return Unsafe.getIntVolatile(obj, offset);
    }

    public static void putIntVolatile(Object obj, long offset, int newValue) {
        Unsafe.putIntVolatile(obj, offset, newValue);
    }

    public static long getLongVolatile(Object obj, long offset) {
        return Unsafe.getLongVolatile(obj, offset);
    }

    public static void putLongVolatile(Object obj, long offset, long newValue) {
        Unsafe.putLongVolatile(obj, offset, newValue);
    }

    public static Object getObjectVolatile(Object obj, long offset) {
        return Unsafe.getObjectVolatile(obj, offset);
    }

    public static void putObjectVolatile(Object obj, long offset, Object newValue) {
        Unsafe.putObjectVolatile(obj, offset, newValue);
    }

    public static void putOrderedInt(Object obj, long offset, int newValue) {
        unsafe.putOrderedInt(obj, offset, newValue);
    }

    public static void putOrderedLong(Object obj, long offset, long newValue) {
        unsafe.putOrderedLong(obj, offset, newValue);
    }

    public static void putOrderedObject(Object obj, long offset, Object newValue) {
        unsafe.putOrderedObject(obj, offset, newValue);
    }

    public static final int getAndAddInt(Object o, long offset, int delta) {
        return unsafe.getAndAddInt(o, offset, delta);
    }

    public static final long getAndAddLong(Object o, long offset, long delta) {
        return unsafe.getAndAddLong(o, offset, delta);
    }

    public static final int getAndSetInt(Object o, long offset, int newValue) {
        return unsafe.getAndSetInt(o, offset, newValue);
    }

    public static final long getAndSetLong(Object o, long offset, long newValue) {
        return unsafe.getAndSetLong(o, offset, newValue);
    }

    public static final Object getAndSetObject(Object o, long offset, Object newValue) {
        return unsafe.getAndSetObject(o, offset, newValue);
    }*/
}
