package com.v7878.misc;

import java.nio.ByteOrder;

public class Math {

    public static short convEndian(short value, ByteOrder order) {
        return ByteOrder.nativeOrder().equals(order) ? value : Short.reverseBytes(value);
    }

    public static int convEndian(int value, ByteOrder order) {
        return ByteOrder.nativeOrder().equals(order) ? value : Integer.reverseBytes(value);
    }

    public static long convEndian(long value, ByteOrder order) {
        return ByteOrder.nativeOrder().equals(order) ? value : Long.reverseBytes(value);
    }

    public static long maxUL(long a, long b) {
        return Long.compareUnsigned(a, b) > 0 ? a : b;
    }

    public static long minUL(long a, long b) {
        return Long.compareUnsigned(a, b) < 0 ? a : b;
    }

    public static int maxU(int a, int b) {
        return Integer.compareUnsigned(a, b) > 0 ? a : b;
    }

    public static int minU(int a, int b) {
        return Integer.compareUnsigned(a, b) < 0 ? a : b;
    }

    public static long addExactUL(long x, long y) {
        long tmp = x + y;
        if (Long.compareUnsigned(tmp, x) < 0) {
            throw new ArithmeticException("unsigned long overflow");
        }
        return tmp;
    }

    public static int addExactU(int x, int y) {
        int tmp = x + y;
        if (Integer.compareUnsigned(tmp, x) < 0) {
            throw new ArithmeticException("unsigned int overflow");
        }
        return tmp;
    }

    public static boolean is32Bit(long value) {
        return value >>> 32 == 0;
    }

    public static boolean isSigned32Bit(long value) {
        return (((value >> 32) + 1) & ~1) == 0;
    }

    public static boolean isPowerOfTwoUL(long x) {
        return (x != 0) && (x & (x - 1)) == 0;
    }

    public static boolean isPowerOfTwoL(long x) {
        return (x > 0) && isPowerOfTwoUL(x);
    }

    public static boolean isPowerOfTwoU(int x) {
        return (x != 0) && (x & (x - 1)) == 0;
    }

    public static boolean isPowerOfTwo(int x) {
        return (x > 0) && isPowerOfTwoU(x);
    }

    public static boolean isAlignedL(long x, long alignment) {
        if (!isPowerOfTwoUL(alignment)) {
            throw new IllegalArgumentException("alignment(" +
                    Long.toUnsignedString(alignment) + ") must be power of two");
        }
        return (x & (alignment - 1)) == 0;
    }

    public static boolean isAligned(int x, int alignment) {
        if (!isPowerOfTwoU(alignment)) {
            throw new IllegalArgumentException("alignment(" +
                    Integer.toUnsignedString(alignment) + ") must be power of two");
        }
        return (x & (alignment - 1)) == 0;
    }

    public static long roundDownUL(long x, long alignment) {
        if (!isPowerOfTwoUL(alignment)) {
            throw new IllegalArgumentException("alignment(" +
                    Long.toUnsignedString(alignment) + ") must be power of two");
        }
        return x & -alignment;
    }

    public static long roundDownL(long x, long alignment) {
        if (x < 0 || alignment < 0) {
            throw new IllegalArgumentException(
                    "x(" + x + ") or alignment(" + alignment + ") is negative");
        }
        return roundDownUL(x, alignment);
    }

    public static long roundUpUL(long x, long alignment) {
        return roundDownUL(addExactUL(x, alignment - 1), alignment);
    }

    public static long roundUpL(long x, long alignment) {
        return roundDownL(java.lang.Math.addExact(x, alignment - 1), alignment);
    }

    public static int roundDownU(int x, int alignment) {
        if (!isPowerOfTwoU(alignment)) {
            throw new IllegalArgumentException("alignment(" +
                    Integer.toUnsignedString(alignment) + ") must be power of two");
        }
        return x & -alignment;
    }

    public static int roundDown(int x, int alignment) {
        if (x < 0 || alignment < 0) {
            throw new IllegalArgumentException(
                    "x(" + x + ") or alignment(" + alignment + ") is negative");
        }
        return roundDownU(x, alignment);
    }

    public static int roundUpU(int x, int alignment) {
        return roundDownU(addExactU(x, alignment - 1), alignment);
    }

    public static int roundUp(int x, int alignment) {
        return roundDown(java.lang.Math.addExact(x, alignment - 1), alignment);
    }

    public static int log2(int value) {
        return 31 - Integer.numberOfLeadingZeros(value);
    }

    public static int log2(long value) {
        return 63 - Long.numberOfLeadingZeros(value);
    }

    public static int toUnsignedInt(byte n) {
        return n & 0xff;
    }

    public static int toUnsignedInt(short n) {
        return n & 0xffff;
    }

    public static long toUnsignedLong(byte n) {
        return n & 0xffL;
    }

    public static long toUnsignedLong(short n) {
        return n & 0xffffL;
    }

    public static long toUnsignedLong(int n) {
        return n & 0xffffffffL;
    }
}
