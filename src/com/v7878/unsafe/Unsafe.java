package com.v7878.unsafe;

import android.annotation.TargetApi;
import android.os.Build;
import com.v7878.Thrower;
import static com.v7878.unsafe.Utils.*;
import java.lang.reflect.*;

//sun.misc.Unsafe "as is", but static
@DangerLevel(1)
public class Unsafe {

    public static void throwException(Throwable th) {
        Thrower.throwException(th);
    }

    private static final sun.misc.Unsafe unsafe = nothrows_run(() -> {
        Field field = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
        field.setAccessible(true);
        return (sun.misc.Unsafe) field.get(null);
    });

    public static sun.misc.Unsafe getUnsafe() {
        return unsafe;
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

    @TargetApi(Build.VERSION_CODES.N)
    public static int addressSize() {
        return unsafe.addressSize();
    }

    @TargetApi(Build.VERSION_CODES.N)
    public static int pageSize() {
        return unsafe.pageSize();
    }

    @TargetApi(Build.VERSION_CODES.N)
    public static long allocateMemory(long bytes) {
        return unsafe.allocateMemory(bytes);
    }

    @TargetApi(Build.VERSION_CODES.N)
    public static void freeMemory(long address) {
        unsafe.freeMemory(address);
    }

    @TargetApi(Build.VERSION_CODES.N)
    public static void setMemory(long address, long bytes, byte value) {
        unsafe.setMemory(address, bytes, value);
    }

    @TargetApi(Build.VERSION_CODES.N)
    public static boolean getBoolean(Object obj, long offset) {
        return unsafe.getByte(obj, offset) != 0;
    }

    @TargetApi(Build.VERSION_CODES.N)
    public static void putBoolean(Object obj, long offset, boolean value) {
        unsafe.putByte(obj, offset, (byte) (value ? 1 : 0));
    }

    @TargetApi(Build.VERSION_CODES.N)
    public static byte getByte(Object obj, long offset) {
        return unsafe.getByte(obj, offset);
    }

    @TargetApi(Build.VERSION_CODES.N)
    public static void putByte(Object obj, long offset, byte value) {
        unsafe.putByte(obj, offset, value);
    }

    @TargetApi(Build.VERSION_CODES.N)
    public static char getChar(Object obj, long offset) {
        return unsafe.getChar(obj, offset);
    }

    @TargetApi(Build.VERSION_CODES.N)
    public static void putChar(Object obj, long offset, char value) {
        unsafe.putChar(obj, offset, value);
    }

    @TargetApi(Build.VERSION_CODES.N)
    public static short getShort(Object obj, long offset) {
        return unsafe.getShort(obj, offset);
    }

    @TargetApi(Build.VERSION_CODES.N)
    public static void putShort(Object obj, long offset, short value) {
        unsafe.putShort(obj, offset, value);
    }

    @TargetApi(Build.VERSION_CODES.N)
    public static float getFloat(Object obj, long offset) {
        return unsafe.getFloat(obj, offset);
    }

    @TargetApi(Build.VERSION_CODES.N)
    public static void putFloat(Object obj, long offset, float value) {
        unsafe.putFloat(obj, offset, value);
    }

    @TargetApi(Build.VERSION_CODES.N)
    public static double getDouble(Object obj, long offset) {
        return unsafe.getDouble(obj, offset);
    }

    @TargetApi(Build.VERSION_CODES.N)
    public static void putDouble(Object obj, long offset, double value) {
        unsafe.putDouble(obj, offset, value);
    }

    @TargetApi(Build.VERSION_CODES.N)
    public static boolean getBoolean(long address) {
        return unsafe.getByte(address) != 0;
    }

    @TargetApi(Build.VERSION_CODES.N)
    public static void putBoolean(long address, boolean value) {
        unsafe.putByte(address, (byte) (value ? 1 : 0));
    }

    @TargetApi(Build.VERSION_CODES.N)
    public static byte getByte(long address) {
        return unsafe.getByte(address);
    }

    @TargetApi(Build.VERSION_CODES.N)
    public static void putByte(long address, byte value) {
        unsafe.putByte(address, value);
    }

    @TargetApi(Build.VERSION_CODES.N)
    public static short getShort(long address) {
        return unsafe.getShort(address);
    }

    @TargetApi(Build.VERSION_CODES.N)
    public static void putShort(long address, short value) {
        unsafe.putShort(address, value);
    }

    @TargetApi(Build.VERSION_CODES.N)
    public static char getChar(long address) {
        return unsafe.getChar(address);
    }

    @TargetApi(Build.VERSION_CODES.N)
    public static void putChar(long address, char value) {
        unsafe.putChar(address, value);
    }

    @TargetApi(Build.VERSION_CODES.N)
    public static int getInt(long address) {
        return unsafe.getInt(address);
    }

    @TargetApi(Build.VERSION_CODES.N)
    public static void putInt(long address, int value) {
        unsafe.putInt(address, value);
    }

    @TargetApi(Build.VERSION_CODES.N)
    public static long getLong(long address) {
        return unsafe.getLong(address);
    }

    @TargetApi(Build.VERSION_CODES.N)
    public static void putLong(long address, long value) {
        unsafe.putLong(address, value);
    }

    @TargetApi(Build.VERSION_CODES.N)
    public static float getFloat(long address) {
        return unsafe.getFloat(address);
    }

    @TargetApi(Build.VERSION_CODES.N)
    public static void putFloat(long address, float value) {
        unsafe.putFloat(address, value);
    }

    @TargetApi(Build.VERSION_CODES.N)
    public static double getDouble(long address) {
        return unsafe.getDouble(address);
    }

    @TargetApi(Build.VERSION_CODES.N)
    public static void putDouble(long address, double value) {
        unsafe.putDouble(address, value);
    }

    @TargetApi(Build.VERSION_CODES.N)
    public static void copyMemory(long srcAddr, long dstAddr, long bytes) {
        unsafe.copyMemory(srcAddr, dstAddr, bytes);
    }

    @TargetApi(Build.VERSION_CODES.N)
    public static void loadFence() {
        unsafe.loadFence();
    }

    @TargetApi(Build.VERSION_CODES.N)
    public static void storeFence() {
        unsafe.storeFence();
    }

    @TargetApi(Build.VERSION_CODES.N)
    public static void fullFence() {
        unsafe.fullFence();
    }

    /*@TargetApi(Build.VERSION_CODES.N)
    public static final int getAndAddInt(Object o, long offset, int delta) {
        return unsafe.getAndAddInt(o, offset, delta);
    }

    @TargetApi(Build.VERSION_CODES.N)
    public static final long getAndAddLong(Object o, long offset, long delta) {
        return unsafe.getAndAddLong(o, offset, delta);
    }

    @TargetApi(Build.VERSION_CODES.N)
    public static final int getAndSetInt(Object o, long offset, int newValue) {
        return unsafe.getAndSetInt(o, offset, newValue);
    }

    @TargetApi(Build.VERSION_CODES.N)
    public static final long getAndSetLong(Object o, long offset, long newValue) {
        return unsafe.getAndSetLong(o, offset, newValue);
    }

    @TargetApi(Build.VERSION_CODES.N)
    public static final Object getAndSetObject(Object o, long offset, Object newValue) {
        return unsafe.getAndSetObject(o, offset, newValue);
    }*/
}
