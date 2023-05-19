package com.v7878.unsafe;

import static com.v7878.unsafe.Utils.toUnsignedInt;
import static com.v7878.unsafe.Utils.toUnsignedLong;

import android.annotation.TargetApi;
import android.os.Build;

import java.nio.ByteOrder;

@DangerLevel(2)
@TargetApi(Build.VERSION_CODES.O)
public class AndroidUnsafe2 extends AndroidUnsafe {

    private static short convEndian(short value, ByteOrder order) {
        return ByteOrder.nativeOrder().equals(order) ? value : Short.reverseBytes(value);
    }

    private static int convEndian(int value, ByteOrder order) {
        return ByteOrder.nativeOrder().equals(order) ? value : Integer.reverseBytes(value);
    }

    private static long convEndian(long value, ByteOrder order) {
        return ByteOrder.nativeOrder().equals(order) ? value : Long.reverseBytes(value);
    }

    private static int pickPos(int top, int pos) {
        return isBigEndian() ? top - pos : pos;
    }

    private static long makeLong(int i0, int i1) {
        return (toUnsignedLong(i0) << pickPos(32, 0))
                | (toUnsignedLong(i1) << pickPos(32, 32));
    }

    private static long makeLong(short i0, short i1, short i2, short i3) {
        return ((toUnsignedLong(i0) << pickPos(48, 0))
                | (toUnsignedLong(i1) << pickPos(48, 16))
                | (toUnsignedLong(i2) << pickPos(48, 32))
                | (toUnsignedLong(i3) << pickPos(48, 48)));
    }

    private static long makeLong(byte i0, byte i1, byte i2, byte i3, byte i4, byte i5, byte i6, byte i7) {
        return ((toUnsignedLong(i0) << pickPos(56, 0))
                | (toUnsignedLong(i1) << pickPos(56, 8))
                | (toUnsignedLong(i2) << pickPos(56, 16))
                | (toUnsignedLong(i3) << pickPos(56, 24))
                | (toUnsignedLong(i4) << pickPos(56, 32))
                | (toUnsignedLong(i5) << pickPos(56, 40))
                | (toUnsignedLong(i6) << pickPos(56, 48))
                | (toUnsignedLong(i7) << pickPos(56, 56)));
    }

    private static int makeInt(short i0, short i1) {
        return (toUnsignedInt(i0) << pickPos(16, 0))
                | (toUnsignedInt(i1) << pickPos(16, 16));
    }

    private static int makeInt(byte i0, byte i1, byte i2, byte i3) {
        return ((toUnsignedInt(i0) << pickPos(24, 0))
                | (toUnsignedInt(i1) << pickPos(24, 8))
                | (toUnsignedInt(i2) << pickPos(24, 16))
                | (toUnsignedInt(i3) << pickPos(24, 24)));
    }

    private static short makeShort(byte i0, byte i1) {
        return (short) ((toUnsignedInt(i0) << pickPos(8, 0))
                | (toUnsignedInt(i1) << pickPos(8, 8)));
    }

    public static long getLongUnaligned(Object obj, long offset) {
        if (UNALIGNED_ACCESS || ((offset & 7) == 0)) {
            return getLong(obj, offset);
        } else if ((offset & 3) == 0) {
            return makeLong(getInt(obj, offset),
                    getInt(obj, offset + 4));
        } else if ((offset & 1) == 0) {
            return makeLong(getShort(obj, offset),
                    getShort(obj, offset + 2),
                    getShort(obj, offset + 4),
                    getShort(obj, offset + 6));
        } else {
            return makeLong(getByte(obj, offset),
                    getByte(obj, offset + 1),
                    getByte(obj, offset + 2),
                    getByte(obj, offset + 3),
                    getByte(obj, offset + 4),
                    getByte(obj, offset + 5),
                    getByte(obj, offset + 6),
                    getByte(obj, offset + 7));
        }
    }

    public static long getLongUnaligned(Object obj, long offset, ByteOrder order) {
        return convEndian(getLongUnaligned(obj, offset), order);
    }

    public static double getDoubleUnaligned(Object obj, long offset) {
        return Double.longBitsToDouble(getLongUnaligned(obj, offset));
    }

    public static double getDoubleUnaligned(Object obj, long offset, ByteOrder order) {
        return Double.longBitsToDouble(getLongUnaligned(obj, offset, order));
    }

    public static int getIntUnaligned(Object obj, long offset) {
        if (UNALIGNED_ACCESS || ((offset & 3) == 0)) {
            return getInt(obj, offset);
        } else if ((offset & 1) == 0) {
            return makeInt(getShort(obj, offset),
                    getShort(obj, offset + 2));
        } else {
            return makeInt(getByte(obj, offset),
                    getByte(obj, offset + 1),
                    getByte(obj, offset + 2),
                    getByte(obj, offset + 3));
        }
    }

    public static int getIntUnaligned(Object obj, long offset, ByteOrder order) {
        return convEndian(getIntUnaligned(obj, offset), order);
    }

    public static float getFloatUnaligned(Object obj, long offset) {
        return Float.intBitsToFloat(getIntUnaligned(obj, offset));
    }

    public static float getFloatUnaligned(Object obj, long offset, ByteOrder order) {
        return Float.intBitsToFloat(getIntUnaligned(obj, offset, order));
    }

    public static short getShortUnaligned(Object obj, long offset) {
        if (UNALIGNED_ACCESS || ((offset & 1) == 0)) {
            return getShort(obj, offset);
        } else {
            return makeShort(getByte(obj, offset),
                    getByte(obj, offset + 1));
        }
    }

    public static short getShortUnaligned(Object obj, long offset, ByteOrder order) {
        return convEndian(getShortUnaligned(obj, offset), order);
    }

    public static char getCharUnaligned(Object obj, long offset) {
        return (char) getShortUnaligned(obj, offset);
    }

    public static char getCharUnaligned(Object obj, long offset, ByteOrder order) {
        return (char) getShortUnaligned(obj, offset, order);
    }

    public static long getWordUnaligned(Object obj, long offset, ByteOrder order, boolean is_64_bit) {
        return is_64_bit ? getLongUnaligned(obj, offset, order)
                : getIntUnaligned(obj, offset, order) & 0xffffffffL;
    }

    public static long getWordUnaligned(Object obj, long offset, ByteOrder order) {
        return getWordUnaligned(obj, offset, order, IS64BIT);
    }

    public static long getWordUnaligned(Object obj, long offset, boolean is_64_bit) {
        return is_64_bit ? getLongUnaligned(obj, offset)
                : getIntUnaligned(obj, offset) & 0xffffffffL;
    }

    public static long getWordUnaligned(Object obj, long offset) {
        return getWordUnaligned(obj, offset, IS64BIT);
    }

    private static byte pick(byte le, byte be) {
        return isBigEndian() ? be : le;
    }

    private static short pick(short le, short be) {
        return isBigEndian() ? be : le;
    }

    private static int pick(int le, int be) {
        return isBigEndian() ? be : le;
    }

    private static void putLongParts(Object o, long offset, byte i0, byte i1, byte i2, byte i3, byte i4, byte i5, byte i6, byte i7) {
        putByte(o, offset + 0, pick(i0, i7));
        putByte(o, offset + 1, pick(i1, i6));
        putByte(o, offset + 2, pick(i2, i5));
        putByte(o, offset + 3, pick(i3, i4));
        putByte(o, offset + 4, pick(i4, i3));
        putByte(o, offset + 5, pick(i5, i2));
        putByte(o, offset + 6, pick(i6, i1));
        putByte(o, offset + 7, pick(i7, i0));
    }

    private static void putLongParts(Object o, long offset, short i0, short i1, short i2, short i3) {
        putShort(o, offset + 0, pick(i0, i3));
        putShort(o, offset + 2, pick(i1, i2));
        putShort(o, offset + 4, pick(i2, i1));
        putShort(o, offset + 6, pick(i3, i0));
    }

    private static void putLongParts(Object o, long offset, int i0, int i1) {
        putInt(o, offset + 0, pick(i0, i1));
        putInt(o, offset + 4, pick(i1, i0));
    }

    private static void putIntParts(Object o, long offset, short i0, short i1) {
        putShort(o, offset + 0, pick(i0, i1));
        putShort(o, offset + 2, pick(i1, i0));
    }

    private static void putIntParts(Object o, long offset, byte i0, byte i1, byte i2, byte i3) {
        putByte(o, offset + 0, pick(i0, i3));
        putByte(o, offset + 1, pick(i1, i2));
        putByte(o, offset + 2, pick(i2, i1));
        putByte(o, offset + 3, pick(i3, i0));
    }

    private static void putShortParts(Object o, long offset, byte i0, byte i1) {
        putByte(o, offset + 0, pick(i0, i1));
        putByte(o, offset + 1, pick(i1, i0));
    }

    public static void putLongUnaligned(Object o, long offset, long value) {
        if (UNALIGNED_ACCESS || ((offset & 7) == 0)) {
            putLong(o, offset, value);
        } else if ((offset & 3) == 0) {
            putLongParts(o, offset,
                    (int) (value >>> 0),
                    (int) (value >>> 32));
        } else if ((offset & 1) == 0) {
            putLongParts(o, offset,
                    (short) (value >>> 0),
                    (short) (value >>> 16),
                    (short) (value >>> 32),
                    (short) (value >>> 48));
        } else {
            putLongParts(o, offset,
                    (byte) (value >>> 0),
                    (byte) (value >>> 8),
                    (byte) (value >>> 16),
                    (byte) (value >>> 24),
                    (byte) (value >>> 32),
                    (byte) (value >>> 40),
                    (byte) (value >>> 48),
                    (byte) (value >>> 56));
        }
    }

    public static void putLongUnaligned(Object o, long offset, long value, ByteOrder order) {
        putLongUnaligned(o, offset, convEndian(value, order));
    }

    public static void putDoubleUnaligned(Object o, long offset, double value) {
        putLongUnaligned(o, offset, Double.doubleToRawLongBits(value));
    }

    public static void putDoubleUnaligned(Object o, long offset, double value, ByteOrder order) {
        putLongUnaligned(o, offset, Double.doubleToRawLongBits(value), order);
    }

    public static void putIntUnaligned(Object o, long offset, int value) {
        if (UNALIGNED_ACCESS || ((offset & 3) == 0)) {
            putInt(o, offset, value);
        } else if ((offset & 1) == 0) {
            putIntParts(o, offset,
                    (short) (value >> 0),
                    (short) (value >>> 16));
        } else {
            putIntParts(o, offset,
                    (byte) (value >>> 0),
                    (byte) (value >>> 8),
                    (byte) (value >>> 16),
                    (byte) (value >>> 24));
        }
    }

    public static void putIntUnaligned(Object o, long offset, int value, ByteOrder order) {
        putIntUnaligned(o, offset, convEndian(value, order));
    }

    public static void putFloatUnaligned(Object o, long offset, float value) {
        putLongUnaligned(o, offset, Float.floatToRawIntBits(value));
    }

    public static void putFloatUnaligned(Object o, long offset, float value, ByteOrder order) {
        putLongUnaligned(o, offset, Float.floatToRawIntBits(value), order);
    }

    public static void putShortUnaligned(Object o, long offset, short value) {
        if (UNALIGNED_ACCESS || ((offset & 1) == 0)) {
            putShort(o, offset, value);
        } else {
            putShortParts(o, offset,
                    (byte) (value >>> 0),
                    (byte) (value >>> 8));
        }
    }

    public static void putShortUnaligned(Object o, long offset, short value, ByteOrder order) {
        putShortUnaligned(o, offset, convEndian(value, order));
    }

    public static void putCharUnaligned(Object o, long offset, char value) {
        putShortUnaligned(o, offset, (short) value);
    }

    public static void putCharUnaligned(Object o, long offset, char value, ByteOrder order) {
        putShortUnaligned(o, offset, (short) value, order);
    }

    public static void putWordUnaligned(Object obj, long offset, long value, ByteOrder order, boolean is_64_bit) {
        if (is_64_bit) {
            putLongUnaligned(obj, offset, value, order);
        } else {
            putIntUnaligned(obj, offset, (int) value, order);
        }
    }

    public static void putWordUnaligned(Object obj, long offset, long value, ByteOrder order) {
        putWordUnaligned(obj, offset, value, order, IS64BIT);
    }

    public static void putWordUnaligned(Object obj, long offset, long value, boolean is_64_bit) {
        if (is_64_bit) {
            putLongUnaligned(obj, offset, value);
        } else {
            putIntUnaligned(obj, offset, (int) value);
        }
    }

    public static void putWordUnaligned(Object obj, long offset, long value) {
        putWordUnaligned(obj, offset, value, IS64BIT);
    }

    public static void copyMemory(Object srcBase, long srcOffset, Object destBase, long destOffset, long bytes) {
        if (bytes == 0) {
            return;
        }
        //maybe it can be done better
        if (srcBase == null) {
            if (destBase == null) {
                copyMemory(srcOffset, destOffset, bytes);
                return;
            }
            for (long i = 0; i < bytes; i++) {
                putByteO(destBase, destOffset + i, getByteN(srcOffset + i));
            }
            return;
        }
        if (destBase == null) {
            for (long i = 0; i < bytes; i++) {
                putByteN(destOffset + i, getByteO(srcBase, srcOffset + i));
            }
            return;
        }
        for (long i = 0; i < bytes; i++) {
            //TODO: use builtin functions for primitive arrays
            putByteO(destBase, destOffset + i, getByteO(srcBase, srcOffset + i));
        }
    }
}
