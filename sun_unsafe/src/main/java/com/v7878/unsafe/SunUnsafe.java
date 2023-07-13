package com.v7878.unsafe;

import java.lang.reflect.Field;

import sun.misc.Unsafe;

//sun.misc.Unsafe "as is", but static
@SuppressWarnings("DiscouragedPrivateApi")
public class SunUnsafe {

    private static final Unsafe unsafe;

    static {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (sun.misc.Unsafe) field.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static Unsafe getUnsafe() {
        return unsafe;
    }

    public static Class<?> getUnsafeClass() {
        return Unsafe.class;
    }

    public static long objectFieldOffset(Field field) {
        return unsafe.objectFieldOffset(field);
    }

    public static int arrayBaseOffset(Class<?> clazz) {
        return unsafe.arrayBaseOffset(clazz);
    }

    public static int arrayIndexScale(Class<?> clazz) {
        return unsafe.arrayIndexScale(clazz);
    }

    public static boolean compareAndSwapInt(Object obj, long offset,
                                            int expectedValue, int value) {
        return unsafe.compareAndSwapInt(obj, offset, expectedValue, value);
    }

    public static boolean compareAndSwapLong(Object obj, long offset,
                                             long expectedValue, long value) {
        return unsafe.compareAndSwapLong(obj, offset, expectedValue, value);
    }

    public static boolean compareAndSwapObject(Object obj, long offset,
                                               Object expectedValue, Object value) {
        return unsafe.compareAndSwapObject(obj, offset, expectedValue, value);
    }

    public static int getIntVolatile(Object obj, long offset) {
        return unsafe.getIntVolatile(obj, offset);
    }

    public static void putIntVolatile(Object obj, long offset, int value) {
        unsafe.putIntVolatile(obj, offset, value);
    }

    public static long getLongVolatile(Object obj, long offset) {
        return unsafe.getLongVolatile(obj, offset);
    }

    public static void putLongVolatile(Object obj, long offset, long value) {
        unsafe.putLongVolatile(obj, offset, value);
    }

    public static Object getObjectVolatile(Object obj, long offset) {
        return unsafe.getObjectVolatile(obj, offset);
    }

    public static void putObjectVolatile(Object obj, long offset, Object value) {
        unsafe.putObjectVolatile(obj, offset, value);
    }

    public static int getInt(Object obj, long offset) {
        return unsafe.getInt(obj, offset);
    }

    public static void putInt(Object obj, long offset, int value) {
        unsafe.putInt(obj, offset, value);
    }

    public static void putOrderedInt(Object obj, long offset, int value) {
        unsafe.putOrderedInt(obj, offset, value);
    }

    public static long getLong(Object obj, long offset) {
        return unsafe.getLong(obj, offset);
    }

    public static void putLong(Object obj, long offset, long value) {
        unsafe.putLong(obj, offset, value);
    }

    public static void putOrderedLong(Object obj, long offset, long value) {
        unsafe.putOrderedLong(obj, offset, value);
    }

    public static Object getObject(Object obj, long offset) {
        return unsafe.getObject(obj, offset);
    }

    public static void putObject(Object obj, long offset, Object value) {
        unsafe.putObject(obj, offset, value);
    }

    public static void putOrderedObject(Object obj, long offset, Object value) {
        unsafe.putOrderedObject(obj, offset, value);
    }

    public static void park(boolean absolute, long time) {
        unsafe.park(absolute, time);
    }

    public static void unpark(Object obj) {
        unsafe.unpark(obj);
    }

    public static Object allocateInstance(Class<?> clazz) throws InstantiationException {
        return unsafe.allocateInstance(clazz);
    }

    public static int addressSize() {
        return unsafe.addressSize();
    }

    public static int pageSize() {
        return unsafe.pageSize();
    }

    public static long allocateMemory(long bytes) {
        return unsafe.allocateMemory(bytes);
    }

    public static void freeMemory(long address) {
        unsafe.freeMemory(address);
    }

    public static void setMemory(long address, long bytes, byte value) {
        unsafe.setMemory(address, bytes, value);
    }

    public static boolean getBoolean(Object obj, long offset) {
        return unsafe.getByte(obj, offset) != 0;
    }

    public static void putBoolean(Object obj, long offset, boolean value) {
        unsafe.putByte(obj, offset, (byte) (value ? 1 : 0));
    }

    public static byte getByte(Object obj, long offset) {
        return unsafe.getByte(obj, offset);
    }

    public static void putByte(Object obj, long offset, byte value) {
        unsafe.putByte(obj, offset, value);
    }

    public static char getChar(Object obj, long offset) {
        return unsafe.getChar(obj, offset);
    }

    public static void putChar(Object obj, long offset, char value) {
        unsafe.putChar(obj, offset, value);
    }

    public static short getShort(Object obj, long offset) {
        return unsafe.getShort(obj, offset);
    }

    public static void putShort(Object obj, long offset, short value) {
        unsafe.putShort(obj, offset, value);
    }

    public static float getFloat(Object obj, long offset) {
        return unsafe.getFloat(obj, offset);
    }

    public static void putFloat(Object obj, long offset, float value) {
        unsafe.putFloat(obj, offset, value);
    }

    public static double getDouble(Object obj, long offset) {
        return unsafe.getDouble(obj, offset);
    }

    public static void putDouble(Object obj, long offset, double value) {
        unsafe.putDouble(obj, offset, value);
    }

    public static boolean getBoolean(long address) {
        return unsafe.getByte(address) != 0;
    }

    public static void putBoolean(long address, boolean value) {
        unsafe.putByte(address, (byte) (value ? 1 : 0));
    }

    public static byte getByte(long address) {
        return unsafe.getByte(address);
    }

    public static void putByte(long address, byte value) {
        unsafe.putByte(address, value);
    }

    public static short getShort(long address) {
        return unsafe.getShort(address);
    }

    public static void putShort(long address, short value) {
        unsafe.putShort(address, value);
    }

    public static char getChar(long address) {
        return unsafe.getChar(address);
    }

    public static void putChar(long address, char value) {
        unsafe.putChar(address, value);
    }

    public static int getInt(long address) {
        return unsafe.getInt(address);
    }

    public static void putInt(long address, int value) {
        unsafe.putInt(address, value);
    }

    public static long getLong(long address) {
        return unsafe.getLong(address);
    }

    public static void putLong(long address, long value) {
        unsafe.putLong(address, value);
    }

    public static float getFloat(long address) {
        return unsafe.getFloat(address);
    }

    public static void putFloat(long address, float value) {
        unsafe.putFloat(address, value);
    }

    public static double getDouble(long address) {
        return unsafe.getDouble(address);
    }

    public static void putDouble(long address, double value) {
        unsafe.putDouble(address, value);
    }

    public static void copyMemory(long srcAddr, long dstAddr, long bytes) {
        unsafe.copyMemory(srcAddr, dstAddr, bytes);
    }

    public static int getAndAddInt(Object obj, long offset, int delta) {
        int v;
        do {
            v = getIntVolatile(obj, offset);
        } while (!compareAndSwapInt(obj, offset, v, v + delta));
        return v;
    }

    public static long getAndAddLong(Object obj, long offset, long delta) {
        long v;
        do {
            v = getLongVolatile(obj, offset);
        } while (!compareAndSwapLong(obj, offset, v, v + delta));
        return v;
    }

    public static int getAndSetInt(Object obj, long offset, int newValue) {
        int v;
        do {
            v = getIntVolatile(obj, offset);
        } while (!compareAndSwapInt(obj, offset, v, newValue));
        return v;
    }

    public static long getAndSetLong(Object obj, long offset, long newValue) {
        long v;
        do {
            v = getLongVolatile(obj, offset);
        } while (!compareAndSwapLong(obj, offset, v, newValue));
        return v;
    }

    public static Object getAndSetObject(Object obj, long offset, Object newValue) {
        Object v;
        do {
            v = getObjectVolatile(obj, offset);
        } while (!compareAndSwapObject(obj, offset, v, newValue));
        return v;
    }

    public static void loadFence() {
        unsafe.loadFence();
    }

    public static void storeFence() {
        unsafe.storeFence();
    }

    public static void fullFence() {
        unsafe.fullFence();
    }
}
