package com.v7878;

import android.annotation.TargetApi;
import android.os.Build;
import static com.v7878.Utils.*;
import java.nio.ByteOrder;

@TargetApi(Build.VERSION_CODES.N)
public class AndroidUnsafe2 extends AndroidUnsafe {

    public static final int ADDRESS_SIZE = addressSize();
    public static final boolean IS64BIT = ADDRESS_SIZE == 8;
    public static final boolean IS_BIG_ENDIAN = ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN;

    //TODO
    public static final boolean UNALIGNED_ACCESS = false;

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

    static {
        assert_(ARRAY_OBJECT_INDEX_SCALE == 4, RuntimeException::new,
                "ARRAY_OBJECT_INDEX_SCALE must be equal to 4");
        assert_((ADDRESS_SIZE == 4) || (ADDRESS_SIZE == 8), RuntimeException::new,
                "ADDRESS_SIZE must be equal to 4 or 8");
    }

    public static boolean unalignedAccess() {
        return UNALIGNED_ACCESS;
    }

    public final boolean isBigEndian() {
        return IS_BIG_ENDIAN;
    }

    public static long getWord(Object obj, long offset, boolean is_64_bit) {
        return is_64_bit ? getLong(obj, offset) : getInt(obj, offset) & 0xffffffffL;
    }

    public static long getWord(Object obj, long offset) {
        return getWord(obj, offset, IS64BIT);
    }

    public static long getWord(long address, boolean is_64_bit) {
        return is_64_bit ? getLong(address) : getInt(address) & 0xffffffffL;
    }

    public static long getWord(long address) {
        return getWord(address, IS64BIT);
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

    public static void putWord(long address, long value, boolean is_64_bit) {
        if (is_64_bit) {
            putLong(address, value);
        } else {
            putInt(address, (int) value);
        }
    }

    public static void putWord(long address, long value) {
        putWord(address, value, IS64BIT);
    }

    public static void putWordChecked(Object obj, long offset, long value, boolean is_64_bit) {
        assert_(checkPointer(obj, offset), IllegalArgumentException::new);
        assert_(is_64_bit || is32BitClean(value), IllegalArgumentException::new);
        putWord(obj, offset, value, is_64_bit);
    }

    public static void putWordChecked(Object obj, long offset, long value) {
        putWordChecked(obj, offset, value, IS64BIT);
    }

    public static void putWordChecked(long address, long value, boolean is_64_bit) {
        assert_(checkNativeAddress(address), IllegalArgumentException::new);
        assert_(is_64_bit || is32BitClean(value), IllegalArgumentException::new);
        putWord(address, value, is_64_bit);
    }

    public static void putWordChecked(long address, long value) {
        putWordChecked(address, value, IS64BIT);
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

    public static long getLongUnaligned(Object obj, long offset, boolean bigEndian) {
        return convEndian(getLongUnaligned(obj, offset), bigEndian);
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

    public static int getIntUnaligned(Object obj, long offset, boolean bigEndian) {
        return convEndian(getIntUnaligned(obj, offset), bigEndian);
    }

    public static short getShortUnaligned(Object obj, long offset) {
        if (UNALIGNED_ACCESS || ((offset & 1) == 0)) {
            return getShort(obj, offset);
        } else {
            return makeShort(getByte(obj, offset),
                    getByte(obj, offset + 1));
        }
    }

    public static short getShortUnaligned(Object obj, long offset, boolean bigEndian) {
        return convEndian(getShortUnaligned(obj, offset), bigEndian);
    }

    public static char getCharUnaligned(Object obj, long offset) {
        return (char) getShortUnaligned(obj, offset);
    }

    public static char getCharUnaligned(Object obj, long offset, boolean bigEndian) {
        return (char) getShortUnaligned(obj, offset, bigEndian);
    }

    private static byte pick(byte le, byte be) {
        return IS_BIG_ENDIAN ? be : le;
    }

    private static short pick(short le, short be) {
        return IS_BIG_ENDIAN ? be : le;
    }

    private static int pick(int le, int be) {
        return IS_BIG_ENDIAN ? be : le;
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

    public static void putLongUnaligned(Object o, long offset, long x) {
        if (UNALIGNED_ACCESS || ((offset & 7) == 0)) {
            putLong(o, offset, x);
        } else if ((offset & 3) == 0) {
            putLongParts(o, offset,
                    (int) (x >>> 0),
                    (int) (x >>> 32));
        } else if ((offset & 1) == 0) {
            putLongParts(o, offset,
                    (short) (x >>> 0),
                    (short) (x >>> 16),
                    (short) (x >>> 32),
                    (short) (x >>> 48));
        } else {
            putLongParts(o, offset,
                    (byte) (x >>> 0),
                    (byte) (x >>> 8),
                    (byte) (x >>> 16),
                    (byte) (x >>> 24),
                    (byte) (x >>> 32),
                    (byte) (x >>> 40),
                    (byte) (x >>> 48),
                    (byte) (x >>> 56));
        }
    }

    public static void putLongUnaligned(Object o, long offset, long x, boolean bigEndian) {
        putLongUnaligned(o, offset, convEndian(x, bigEndian));
    }

    public static void putIntUnaligned(Object o, long offset, int x) {
        if (UNALIGNED_ACCESS || ((offset & 3) == 0)) {
            putInt(o, offset, x);
        } else if ((offset & 1) == 0) {
            putIntParts(o, offset,
                    (short) (x >> 0),
                    (short) (x >>> 16));
        } else {
            putIntParts(o, offset,
                    (byte) (x >>> 0),
                    (byte) (x >>> 8),
                    (byte) (x >>> 16),
                    (byte) (x >>> 24));
        }
    }

    public static void putIntUnaligned(Object o, long offset, int x, boolean bigEndian) {
        putIntUnaligned(o, offset, convEndian(x, bigEndian));
    }

    public static void putShortUnaligned(Object o, long offset, short x) {
        if (UNALIGNED_ACCESS || ((offset & 1) == 0)) {
            putShort(o, offset, x);
        } else {
            putShortParts(o, offset,
                    (byte) (x >>> 0),
                    (byte) (x >>> 8));
        }
    }

    public static void putShortUnaligned(Object o, long offset, short x, boolean bigEndian) {
        putShortUnaligned(o, offset, convEndian(x, bigEndian));
    }

    public static void putCharUnaligned(Object o, long offset, char x) {
        putShortUnaligned(o, offset, (short) x);
    }

    public static void putCharUnaligned(Object o, long offset, char x, boolean bigEndian) {
        putShortUnaligned(o, offset, (short) x, bigEndian);
    }

    public static void copyMemory(Object srcBase, long srcOffset, Object destBase, long destOffset, long bytes) {
        if (bytes == 0) {
            return;
        }
        if (srcBase == null && destBase == null) {
            copyMemory(srcOffset, destOffset, bytes);
            return;
        }

        //maybe it can be done better
        for (long i = 0; i < bytes; i++) {
            putByte(destBase, destOffset + i, getByte(srcBase, srcOffset + i));
        }
    }

    public static void copyMemoryChecked(Object srcBase, long srcOffset, Object destBase, long destOffset, long bytes) {
        assert_(checkOffset(bytes), IllegalArgumentException::new);
        assert_(checkPointer(srcBase, srcOffset), IllegalArgumentException::new);
        assert_(checkPointer(destBase, destOffset), IllegalArgumentException::new);
        copyMemory(srcBase, srcOffset, destBase, destOffset, bytes);
    }
}
