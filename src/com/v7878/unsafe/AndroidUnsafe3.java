package com.v7878.unsafe;

import android.annotation.TargetApi;
import android.os.Build;
import static com.v7878.unsafe.Utils.*;
import java.lang.invoke.*;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.*;
import java.util.*;

@DangerLevel(3)
@TargetApi(Build.VERSION_CODES.O)
public class AndroidUnsafe3 extends AndroidUnsafe2 {

    public static class ClassBase {

        public ClassLoader classLoader;
        public Class<?> componentType;
        public Object dexCache;
        public Object extData;
        public Object[] ifTable;
        public String name;
        public Class<?> superClass;
        public Object vtable;
        public long iFields;
        public long methods;
        public long sFields;
        public int accessFlags;
        public int classFlags;
        public int classSize;
        public int clinitThreadId;
        public int dexClassDefIndex;
        public volatile int dexTypeIndex;
        public int numReferenceInstanceFields;
        public int numReferenceStaticFields;
        public int objectSize;
        public int objectSizeAllocFastPath;
        public int primitiveType;
        public int referenceInstanceOffsets;
        public int status;
        public short copiedMethodsOffset;
        public short virtualMethodsOffset;
    }

    public static class AccessibleObjectBase {

        public boolean override;
    }

    public static class FieldBase extends AccessibleObjectBase {

        public int accessFlags;
        public Class<?> declaringClass;
        public int artFieldIndex;
        public int offset;
        public Class<?> type;
    }

    public static class ExecutableBase extends AccessibleObjectBase {

        public volatile boolean hasRealParameterData;
        public volatile Parameter[] parameters;
        public int accessFlags;
        public long artMethod;
        public Class<?> declaringClass;
        public Class<?> declaringClassOfOverriddenMethod;
        public int dexMethodIndex;
    }

    public static class MethodHandleBase {

        public MethodType type;
        public Object different;
        public MethodHandleImplBase cachedSpreadInvoker;
        public int handleKind;
        public long artFieldOrMethod;
    }

    public static final class MethodHandleImplBase extends MethodHandleBase {

        public HandleInfo info;
    }

    public static final class HandleInfo {

        public Member member;
        public MethodHandleImplBase handle;
    }

    private static class Test {

        public static final Lookup lp = MethodHandles.lookup();

        public static final Method am = nothrow_run(() -> Test.class.getDeclaredMethod("a"));
        public static final Method bm = nothrow_run(() -> Test.class.getDeclaredMethod("b"));

        public static final Field af = nothrow_run(() -> Test.class.getDeclaredField("sa"));
        public static final Field bf = nothrow_run(() -> Test.class.getDeclaredField("sb"));

        public static int sa, sb;

        public static void a() {
            System.out.println("inside a");
        }

        public static void b() {
            System.out.println("inside b");
        }
    }

    private static final Class<MethodHandle> MethodHandleImplClass = nothrow_run(() -> {
        return (Class<MethodHandle>) Class.forName("java.lang.invoke.MethodHandleImpl");
    });

    public static final int artMethodSize;
    public static final int artMethodPadding;
    public static final int artFieldSize;
    public static final int artFieldPadding;

    private static final Method mGetArtField;

    static {
        ClassBase[] th = arrayCast(ClassBase.class, Test.class);

        long am = getArtMethod(Test.am);
        long bm = getArtMethod(Test.bm);
        artMethodSize = (int) (bm - am);
        artMethodPadding = (int) (am - th[0].methods) % artMethodSize;

        mGetArtField = nothrow_run(() -> {
            return getDeclaredMethod(Field.class, "getArtField");
        });

        long af = getArtField(Test.af);
        long bf = getArtField(Test.bf);
        artFieldSize = (int) (bf - af);
        artFieldPadding = (int) (af - th[0].sFields) % artFieldSize;
    }

    @DangerLevel(DangerLevel.VERY_CAREFUL)
    public static <T> T[] arrayCast(Class<T> clazz, Object... data) {
        assert_(!clazz.isPrimitive(), IllegalArgumentException::new);
        T[] out = (T[]) Array.newInstance(clazz, data.length);
        for (int i = 0; i < data.length; i++) {
            putObject(out, ARRAY_OBJECT_BASE_OFFSET + i * ARRAY_OBJECT_INDEX_SCALE, data[i]);
        }
        return out;
    }

    public static void setAccessible(AccessibleObject ao, boolean value) {
        AccessibleObjectBase[] aob = arrayCast(AccessibleObjectBase.class, ao);
        aob[0].override = value;
    }

    public static int fieldOffset(Field f) {
        FieldBase[] fh = arrayCast(FieldBase.class, f);
        return fh[0].offset;
    }

    public static long instanceFieldOffset(Field f) {
        assert_(!Modifier.isStatic(f.getModifiers()), IllegalArgumentException::new);
        return fieldOffset(f);
    }

    public static long staticFieldOffset(Field f) {
        assert_(Modifier.isStatic(f.getModifiers()), IllegalArgumentException::new);
        return fieldOffset(f);
    }

    public static Object staticFieldBase(Field f) {
        assert_(Modifier.isStatic(f.getModifiers()), IllegalArgumentException::new);
        return f.getDeclaringClass();
    }

    public static long getArtMethod(Executable ex) {
        ExecutableBase[] eh = arrayCast(ExecutableBase.class, ex);
        return eh[0].artMethod;
    }

    public static long getArtField(Field f) {
        return (long) nothrow_run(() -> mGetArtField.invoke(f), true);
    }

    @SuppressWarnings("UseSpecificCatch")
    public static Executable[] getDeclaredExecutables0(Class<?> clazz) {
        Objects.requireNonNull(clazz);
        ClassBase[] clh = arrayCast(ClassBase.class, clazz);
        long methods = clh[0].methods;
        if (methods == 0) {
            return new Executable[0];
        }
        int col = getInt(methods);
        Executable[] out = new Executable[col];
        if (out.length == 0) {
            return out;
        }
        MethodHandle mh = allocateInstance(MethodHandleImplClass);
        MethodHandleImplBase[] mhh = arrayCast(MethodHandleImplBase.class, mh);
        for (int i = 0; i < col; i++) {
            mhh[0].artFieldOrMethod = methods + artMethodPadding + artMethodSize * i;
            mhh[0].info = null;
            out[i] = MethodHandles.reflectAs(Executable.class, mh);
        }
        return out;
    }

    @SuppressWarnings("UseSpecificCatch")
    public static Field[] getDeclaredFields0(Class<?> clazz, boolean s) {
        Objects.requireNonNull(clazz);
        ClassBase[] clh = arrayCast(ClassBase.class, clazz);
        long fields = s ? clh[0].sFields : clh[0].iFields;
        if (fields == 0) {
            return new Field[0];
        }
        int col = getInt(fields);
        Field[] out = new Field[col];
        if (out.length == 0) {
            return out;
        }
        MethodHandle mh = allocateInstance(MethodHandleImplClass);
        MethodHandleImplBase[] mhh = arrayCast(MethodHandleImplBase.class, mh);
        for (int i = 0; i < col; i++) {
            mhh[0].artFieldOrMethod = fields + artFieldPadding + artFieldSize * i;
            mhh[0].info = null;
            mhh[0].handleKind = Integer.MAX_VALUE;
            out[i] = MethodHandles.reflectAs(Field.class, mh);
        }
        return out;
    }

    public static Field[] getDeclaredFields(Class<?> clazz) {
        Field[] out1 = getDeclaredFields0(clazz, true);
        Field[] out2 = getDeclaredFields0(clazz, false);
        Field[] out = new Field[out1.length + out2.length];
        System.arraycopy(out1, 0, out, 0, out1.length);
        System.arraycopy(out2, 0, out, out1.length, out2.length);
        return out;
    }

    public static Method[] getDeclaredMethods(Class<?> clazz) {
        return Arrays.stream(getDeclaredExecutables0(clazz))
                .filter((exec) -> exec instanceof Method)
                .toArray(Method[]::new);
    }

    public static Constructor[] getDeclaredConstructors(Class<?> clazz) {
        return Arrays.stream(getDeclaredExecutables0(clazz))
                .filter((exec) -> exec instanceof Constructor
                && !Modifier.isStatic(exec.getModifiers()))
                .toArray(Constructor[]::new);
    }

    public static Method convertConstructorToMethod(Constructor<?> ct) {
        Method out = allocateInstance(Method.class);
        ExecutableBase[] eb = arrayCast(ExecutableBase.class, ct, out);

        eb[1].override = eb[0].override;
        eb[1].accessFlags = eb[0].accessFlags;
        eb[1].artMethod = eb[0].artMethod;
        eb[1].hasRealParameterData = eb[0].hasRealParameterData;
        eb[1].declaringClass = eb[0].declaringClass;
        eb[1].dexMethodIndex = eb[0].dexMethodIndex;
        eb[1].hasRealParameterData = eb[0].hasRealParameterData;
        eb[1].parameters = eb[0].parameters;

        return out;
    }

    public static Method getDeclaredStaticConstructor(Class<?> clazz) {
        Constructor[] out = Arrays.stream(getDeclaredExecutables0(clazz))
                .filter((exec) -> exec instanceof Constructor
                && Modifier.isStatic(exec.getModifiers()))
                .toArray(Constructor[]::new);
        if (out.length == 0) {
            return null;
        }
        assert_(out.length == 1, IllegalStateException::new);
        return convertConstructorToMethod(out[0]);
    }

    public static Field getDeclaredField(Class<?> clazz, String name) {
        return searchField(getDeclaredFields(clazz), name, true);
    }

    public static Method getDeclaredMethod(Class<?> clazz, String name, Class<?>... params) {
        return searchMethod(getDeclaredMethods(clazz), name, true, params);
    }

    public static <T> Constructor<T> getDeclaredConstructor(Class<T> clazz, Class<?>... params) {
        return searchConstructor(getDeclaredConstructors(clazz), true, params);
    }

    public static Field[] getFields(Class<?> c) {
        ArrayList<Field> out = new ArrayList<>();
        while (c != null) {
            Field[] af = getDeclaredFields(c);
            out.addAll(Arrays.asList(af));
            c = c.getSuperclass();
        }
        return out.stream().toArray(Field[]::new);
    }

    public static Field[] getInstanceFields(Class<?> c) {
        ArrayList<Field> out = new ArrayList<>();
        while (c != null) {
            Field[] af = getDeclaredFields0(c, false);
            out.addAll(Arrays.asList(af));
            c = c.getSuperclass();
        }
        return out.stream().toArray(Field[]::new);
    }

    public static Method[] getMethods(Class<?> c) {
        ArrayList<Method> out = new ArrayList<>();
        while (c != null) {
            Method[] af = getDeclaredMethods(c);
            out.addAll(Arrays.asList(af));
            c = c.getSuperclass();
        }
        return out.stream().toArray(Method[]::new);
    }
}
