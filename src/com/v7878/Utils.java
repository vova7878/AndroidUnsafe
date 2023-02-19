package com.v7878;

import android.annotation.TargetApi;
import android.os.Build;
import static com.v7878.AndroidUnsafe2.ADDRESS_SIZE;
import static com.v7878.AndroidUnsafe2.IS_BIG_ENDIAN;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Function;
import java.util.function.Supplier;

@TargetApi(Build.VERSION_CODES.O)
public class Utils {

    public static int getSdkInt() {
        return Build.VERSION.SDK_INT + (Build.VERSION.PREVIEW_SDK_INT == 0 ? 0 : 1);
    }

    public static boolean arrayContentsEq(Object[] a1, Object[] a2) {
        if (a1 == null) {
            return a2 == null || a2.length == 0;
        }
        if (a2 == null) {
            return a1.length == 0;
        }
        if (a1.length != a2.length) {
            return false;
        }
        for (int i = 0; i < a1.length; i++) {
            if (a1[i] != a2[i]) {
                return false;
            }
        }
        return true;
    }

    public static Field searchField(Field[] fields, String name, boolean thw) {
        for (Field field : fields) {
            if (field.getName().equals(name)) {
                return field;
            }
        }
        assert_(!thw, NoSuchFieldException::new);
        return null;
    }

    public static Method searchMethod(Method[] methods, String name, boolean thw, Class<?>... parameterTypes) {
        for (Method m : methods) {
            if (m.getName().equals(name) && arrayContentsEq(parameterTypes, m.getParameterTypes())) {
                return m;
            }
        }
        assert_(!thw, NoSuchMethodException::new);
        return null;
    }

    public static <T> Constructor<T> searchConstructor(Constructor<T>[] constructors, boolean thw, Class<?>... parameterTypes) {
        for (Constructor<T> c : constructors) {
            if (arrayContentsEq(parameterTypes, c.getParameterTypes())) {
                return c;
            }
        }
        assert_(!thw, NoSuchMethodException::new);
        return null;
    }

    public static <T extends Throwable> void assert_(boolean value, Supplier<T> th) {
        if (!value) {
            Thrower.throwException(th.get());
        }
    }

    public static <T extends Throwable, E> void assert_(boolean value, Function<E, T> th, Supplier<E> msg) {
        if (!value) {
            Thrower.throwException(th.apply(msg.get()));
        }
    }

    public static <T extends Throwable, E> void assert_(boolean value, Function<E, T> th, E msg) {
        if (!value) {
            Thrower.throwException(th.apply(msg));
        }
    }

    @FunctionalInterface
    public interface TRun<T> {

        public T run() throws Throwable;
    }

    @FunctionalInterface
    public interface VTRun {

        public void run() throws Throwable;
    }

    public static <T> T nothrow_run(TRun<T> r, boolean tie) {
        try {
            return r.run();
        } catch (Throwable th) {
            if (tie && (th instanceof InvocationTargetException)) {
                th = th.getCause();
            }
            Thrower.throwException(th);
            throw new RuntimeException(th);
        }
    }

    public static <T> T nothrow_run(TRun<T> r) {
        return nothrow_run(r, false);
    }

    public static void nothrow_run(VTRun r, boolean tie) {
        try {
            r.run();
        } catch (Throwable th) {
            if (tie && (th instanceof InvocationTargetException)) {
                th = th.getCause();
            }
            Thrower.throwException(th);
            throw new RuntimeException(th);
        }
    }

    public static void nothrow_run(VTRun r) {
        nothrow_run(r, false);
    }

    public static String toHexString(byte[] arr) {
        if (arr == null) {
            return "null";
        }
        if (arr.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(Integer.toHexString(arr[0] & 0xff));
        for (int i = 1; i < arr.length; i++) {
            sb.append(" ");
            sb.append(Integer.toHexString(arr[i] & 0xff));
        }
        return sb.toString().toUpperCase();
    }

    public static boolean is32BitClean(long value) {
        return value >>> 32 == 0;
    }

    public static boolean isPowerOfTwoUnsignedL(long x) {
        return (x & (x - 1)) == 0;
    }

    public static boolean isPowerOfTwoL(long x) {
        return (x >= 0) && isPowerOfTwoUnsignedL(x);
    }

    public static long roundDownL(long x, long n) {
        assert_(isPowerOfTwoL(n), IllegalArgumentException::new);
        return x & -n;
    }

    public static long roundUpUnsignedL(long x, long n) {
        long out = roundDownL(x + n - 1, n);
        assert_(Long.compareUnsigned(out, x) >= 0, IllegalArgumentException::new);
        return out;
    }

    public static long roundUpL(long x, long n) {
        assert_(x >= 0, IllegalArgumentException::new);
        long out = roundDownL(x + n - 1, n);
        assert_(out >= x, IllegalArgumentException::new);
        return out;
    }

    public static boolean isPowerOfTwoUnsigned(int x) {
        return (x & (x - 1)) == 0;
    }

    public static boolean isPowerOfTwo(int x) {
        return (x >= 0) && isPowerOfTwoUnsigned(x);
    }

    public static int roundDown(int x, int n) {
        assert_(isPowerOfTwo(n), IllegalArgumentException::new);
        return x & -n;
    }

    public static int roundUpUnsigned(int x, int n) {
        int out = roundDown(x + n - 1, n);
        assert_(Integer.compareUnsigned(out, x) >= 0, IllegalArgumentException::new);
        return out;
    }

    public static int roundUp(int x, int n) {
        assert_(x >= 0, IllegalArgumentException::new);
        int out = roundDown(x + n - 1, n);
        assert_(out >= x, IllegalArgumentException::new);
        return out;
    }

    public static boolean isAligned(int x, int n) {
        assert_(isPowerOfTwoUnsigned(n), IllegalArgumentException::new);
        return (x & (n - 1)) == 0;
    }

    public static boolean isAlignedL(long x, long n) {
        assert_(isPowerOfTwoUnsignedL(n), IllegalArgumentException::new);
        return (x & (n - 1)) == 0;
    }

    public static boolean checkNativeAddress(long address) {
        if (ADDRESS_SIZE == 4) {
            return is32BitClean(address);
        }
        return true;
    }

    public static boolean checkOffset(long offset) {
        if (ADDRESS_SIZE == 4) {
            // Note: this will also check for negative sizes
            return is32BitClean(offset);
        }
        return offset >= 0;
    }

    public static boolean checkSize(long size) {
        return checkOffset(size);
    }

    public static boolean checkPointer(Object obj, long offset) {
        if (obj == null) {
            return checkNativeAddress(offset);
        }
        return checkOffset(offset);
    }

    public static int log2(int value) {
        return 31 - Integer.numberOfLeadingZeros(value);
    }

    public static int log2(long value) {
        return 63 - Long.numberOfLeadingZeros(value);
    }

    public static short convEndian(short n, boolean big) {
        return big == IS_BIG_ENDIAN ? n : Short.reverseBytes(n);
    }

    public static int convEndian(int n, boolean big) {
        return big == IS_BIG_ENDIAN ? n : Integer.reverseBytes(n);
    }

    public static long convEndian(long n, boolean big) {
        return big == IS_BIG_ENDIAN ? n : Long.reverseBytes(n);
    }

    public static int toUnsignedInt(byte n) {
        return n & 0xff;
    }

    public static int toUnsignedInt(short n) {
        return n & 0xffff;
    }

    public static long toUnsignedLong(byte n) {
        return n & 0xffl;
    }

    public static long toUnsignedLong(short n) {
        return n & 0xffffl;
    }

    public static long toUnsignedLong(int n) {
        return n & 0xffffffffl;
    }

    public static int pickPos(int top, int pos) {
        return IS_BIG_ENDIAN ? top - pos : pos;
    }

    public static long makeLong(int i0, int i1) {
        return (toUnsignedLong(i0) << pickPos(32, 0))
                | (toUnsignedLong(i1) << pickPos(32, 32));
    }

    public static long makeLong(short i0, short i1, short i2, short i3) {
        return ((toUnsignedLong(i0) << pickPos(48, 0))
                | (toUnsignedLong(i1) << pickPos(48, 16))
                | (toUnsignedLong(i2) << pickPos(48, 32))
                | (toUnsignedLong(i3) << pickPos(48, 48)));
    }

    public static long makeLong(byte i0, byte i1, byte i2, byte i3, byte i4, byte i5, byte i6, byte i7) {
        return ((toUnsignedLong(i0) << pickPos(56, 0))
                | (toUnsignedLong(i1) << pickPos(56, 8))
                | (toUnsignedLong(i2) << pickPos(56, 16))
                | (toUnsignedLong(i3) << pickPos(56, 24))
                | (toUnsignedLong(i4) << pickPos(56, 32))
                | (toUnsignedLong(i5) << pickPos(56, 40))
                | (toUnsignedLong(i6) << pickPos(56, 48))
                | (toUnsignedLong(i7) << pickPos(56, 56)));
    }

    public static int makeInt(short i0, short i1) {
        return (toUnsignedInt(i0) << pickPos(16, 0))
                | (toUnsignedInt(i1) << pickPos(16, 16));
    }

    public static int makeInt(byte i0, byte i1, byte i2, byte i3) {
        return ((toUnsignedInt(i0) << pickPos(24, 0))
                | (toUnsignedInt(i1) << pickPos(24, 8))
                | (toUnsignedInt(i2) << pickPos(24, 16))
                | (toUnsignedInt(i3) << pickPos(24, 24)));
    }

    public static short makeShort(byte i0, byte i1) {
        return (short) ((toUnsignedInt(i0) << pickPos(8, 0))
                | (toUnsignedInt(i1) << pickPos(8, 8)));
    }
}
