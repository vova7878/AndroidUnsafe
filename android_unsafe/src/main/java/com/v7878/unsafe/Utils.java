package com.v7878.unsafe;

import com.v7878.Thrower;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Utils {

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
        assert_(!thw, NoSuchFieldException::new, name);
        return null;
    }

    public static Field searchField(Field[] fields, String name) {
        return searchField(fields, name, true);
    }

    private static String methodToString(String name, Class<?>[] argTypes) {
        return name + ((argTypes == null || argTypes.length == 0)
                ? "()" : Arrays.stream(argTypes)
                .map(c -> c == null ? "null" : c.getName())
                .collect(Collectors.joining(",", "(", ")")));
    }

    public static Method searchMethod(Method[] methods, String name,
                                      boolean thw, Class<?>... parameterTypes) {
        for (Method m : methods) {
            if (m.getName().equals(name) && arrayContentsEq(
                    parameterTypes, m.getParameterTypes())) {
                return m;
            }
        }
        assert_(!thw, NoSuchMethodException::new, methodToString(name, parameterTypes));
        return null;
    }

    public static Method searchMethod(Method[] methods, String name, Class<?>... parameterTypes) {
        return searchMethod(methods, name, true, parameterTypes);
    }

    public static <T> Constructor<T> searchConstructor(
            Constructor<T>[] constructors, boolean thw, Class<?>... parameterTypes) {
        for (Constructor<T> c : constructors) {
            if (arrayContentsEq(parameterTypes, c.getParameterTypes())) {
                return c;
            }
        }
        assert_(!thw, NoSuchMethodException::new, methodToString("<init>", parameterTypes));
        return null;
    }

    public static <T> Constructor<T> searchConstructor(
            Constructor<T>[] constructors, Class<?>... parameterTypes) {
        return searchConstructor(constructors, true, parameterTypes);
    }

    public static <T extends Throwable> void assert_(
            boolean value, Supplier<T> th) {
        if (!value) {
            Throwable tmp = th.get();
            Thrower.throwException(tmp);
            throw new RuntimeException(tmp);
        }
    }

    public static <T extends Throwable, E> void assert_(
            boolean value, Function<E, T> th, Supplier<E> msg) {
        if (!value) {
            Throwable tmp = th.apply(msg.get());
            Thrower.throwException(tmp);
            throw new RuntimeException(tmp);
        }
    }

    public static <T extends Throwable> void assert_(
            boolean value, Function<String, T> th, String msg) {
        if (!value) {
            Throwable tmp = th.apply(msg);
            Thrower.throwException(tmp);
            throw new RuntimeException(tmp);
        }
    }

    @FunctionalInterface
    public interface TRun<T> {

        T run() throws Throwable;
    }

    @FunctionalInterface
    public interface VTRun {

        void run() throws Throwable;
    }

    public static <T> T nothrows_run(TRun<T> r) {
        try {
            return r.run();
        } catch (Throwable th) {
            Thrower.throwException(th);
            throw new RuntimeException(th);
        }
    }

    public static void nothrows_run(VTRun r) {
        try {
            r.run();
        } catch (Throwable th) {
            Thrower.throwException(th);
            throw new RuntimeException(th);
        }
    }

    public static <T> Supplier<T> runOnce(Supplier<T> task) {
        return new Supplier<T>() {
            volatile T value;

            @Override
            public T get() {
                if (value == null) {
                    synchronized (this) {
                        if (value == null) {
                            value = task.get();
                        }
                    }
                }
                return value;
            }
        };
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

    @SafeVarargs
    public static <T> List<T> asList(T... array) {
        for (T tmp : array) {
            Objects.requireNonNull(tmp);
        }
        return Arrays.asList(array);
    }
}
