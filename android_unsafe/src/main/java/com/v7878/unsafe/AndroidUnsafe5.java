package com.v7878.unsafe;

import static com.v7878.misc.Version.CORRECT_SDK_INT;
import static com.v7878.unsafe.AndroidUnsafe5.AccessModifier.PRIVATE;
import static com.v7878.unsafe.AndroidUnsafe5.AccessModifier.PROTECTED;
import static com.v7878.unsafe.AndroidUnsafe5.AccessModifier.PUBLIC;
import static com.v7878.unsafe.Utils.assert_;
import static com.v7878.unsafe.Utils.nothrows_run;
import static com.v7878.unsafe.Utils.runOnce;
import static com.v7878.unsafe.memory.LayoutPath.PathElement.groupElement;
import static com.v7878.unsafe.memory.ValueLayout.ADDRESS;
import static com.v7878.unsafe.memory.ValueLayout.JAVA_BOOLEAN;
import static com.v7878.unsafe.memory.ValueLayout.JAVA_BYTE;
import static com.v7878.unsafe.memory.ValueLayout.JAVA_INT;
import static com.v7878.unsafe.memory.ValueLayout.JAVA_OBJECT;
import static com.v7878.unsafe.memory.ValueLayout.JAVA_SHORT;
import static com.v7878.unsafe.memory.ValueLayout.WORD;
import static com.v7878.unsafe.memory.ValueLayout.structLayout;
import static com.v7878.unsafe.memory.ValueLayout.unionLayout;

import com.v7878.unsafe.memory.Addressable;
import com.v7878.unsafe.memory.GroupLayout;
import com.v7878.unsafe.memory.MemorySegment;
import com.v7878.unsafe.memory.Pointer;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import dalvik.system.DexFile;

@SuppressWarnings("deprecation")
@DangerLevel(5)
public class AndroidUnsafe5 extends AndroidUnsafe4 {

    public static class std {

        public static final GroupLayout string = structLayout(
                WORD.withName("mLength"),
                WORD.withName("mCapacity"),
                ADDRESS.withName("mData")
        );
        public static final GroupLayout alternative_string = structLayout(
                ADDRESS.withName("mData"),
                WORD.withName("mCapacity"),
                WORD.withName("mLength")
        );
        public static final GroupLayout shared_ptr = structLayout(
                ADDRESS.withName("__ptr_"),
                ADDRESS.withName("__cntrl_")
        );
        public static final GroupLayout unique_ptr = structLayout(
                ADDRESS.withName("__ptr_")
        );
    }

    private static final GroupLayout array_ref_layout = structLayout(
            ADDRESS.withName("array_"),
            WORD.withName("size_")
    );
    private static final GroupLayout dex_file_14_layout = structLayout(
            ADDRESS.withName("__cpp_virtual_data__"),
            ADDRESS.withName("begin_"),
            WORD.withName("size_"),
            array_ref_layout.withName("data_"),
            std.string.withName("location_"),
            JAVA_INT.withName("location_checksum_"),
            ADDRESS.withName("header_"),
            ADDRESS.withName("string_ids_"),
            ADDRESS.withName("type_ids_"),
            ADDRESS.withName("field_ids_"),
            ADDRESS.withName("method_ids_"),
            ADDRESS.withName("proto_ids_"),
            ADDRESS.withName("class_defs_"),
            ADDRESS.withName("method_handles_"),
            WORD.withName("num_method_handles_"),
            ADDRESS.withName("call_site_ids_"),
            WORD.withName("num_call_site_ids_"),
            ADDRESS.withName("hiddenapi_class_data_"),
            ADDRESS.withName("oat_dex_file_"),
            std.shared_ptr.withName("container_"),
            JAVA_BOOLEAN.withName("is_compact_dex_"),
            JAVA_BYTE.withName("hiddenapi_domain_")
    );
    private static final GroupLayout dex_file_13_10_layout = structLayout(
            ADDRESS.withName("__cpp_virtual_data__"),
            ADDRESS.withName("begin_"),
            WORD.withName("size_"),
            ADDRESS.withName("data_begin_"),
            WORD.withName("data_size_"),
            std.string.withName("location_"),
            JAVA_INT.withName("location_checksum_"),
            ADDRESS.withName("header_"),
            ADDRESS.withName("string_ids_"),
            ADDRESS.withName("type_ids_"),
            ADDRESS.withName("field_ids_"),
            ADDRESS.withName("method_ids_"),
            ADDRESS.withName("proto_ids_"),
            ADDRESS.withName("class_defs_"),
            ADDRESS.withName("method_handles_"),
            WORD.withName("num_method_handles_"),
            ADDRESS.withName("call_site_ids_"),
            WORD.withName("num_call_site_ids_"),
            ADDRESS.withName("hiddenapi_class_data_"),
            ADDRESS.withName("oat_dex_file_"),
            std.unique_ptr.withName("container_"),
            JAVA_BOOLEAN.withName("is_compact_dex_"),
            JAVA_BYTE.withName("hiddenapi_domain_")
    );
    private static final GroupLayout dex_file_9_layout = structLayout(
            ADDRESS.withName("__cpp_virtual_data__"),
            ADDRESS.withName("begin_"),
            WORD.withName("size_"),
            ADDRESS.withName("data_begin_"),
            WORD.withName("data_size_"),
            std.string.withName("location_"),
            JAVA_INT.withName("location_checksum_"),
            ADDRESS.withName("header_"),
            ADDRESS.withName("string_ids_"),
            ADDRESS.withName("type_ids_"),
            ADDRESS.withName("field_ids_"),
            ADDRESS.withName("method_ids_"),
            ADDRESS.withName("proto_ids_"),
            ADDRESS.withName("class_defs_"),
            ADDRESS.withName("method_handles_"),
            WORD.withName("num_method_handles_"),
            ADDRESS.withName("call_site_ids_"),
            WORD.withName("num_call_site_ids_"),
            ADDRESS.withName("oat_dex_file_"),
            std.unique_ptr.withName("container_"),
            JAVA_BOOLEAN.withName("is_compact_dex_"),
            JAVA_BOOLEAN.withName("is_platform_dex_")
    );
    private static final GroupLayout dex_file_8xx_layout = structLayout(
            ADDRESS.withName("__cpp_virtual_data__"),
            ADDRESS.withName("begin_"),
            WORD.withName("size_"),
            std.string.withName("location_"),
            JAVA_INT.withName("location_checksum_"),
            std.unique_ptr.withName("mem_map_"),
            ADDRESS.withName("header_"),
            ADDRESS.withName("string_ids_"),
            ADDRESS.withName("type_ids_"),
            ADDRESS.withName("field_ids_"),
            ADDRESS.withName("method_ids_"),
            ADDRESS.withName("proto_ids_"),
            ADDRESS.withName("class_defs_"),
            ADDRESS.withName("method_handles_"),
            WORD.withName("num_method_handles_"),
            ADDRESS.withName("call_site_ids_"),
            WORD.withName("num_call_site_ids_"),
            ADDRESS.withName("oat_dex_file_")
    );

    private static final GroupLayout art_method_14_12_layout = structLayout(
            JAVA_OBJECT.withName("declaring_class_"),
            JAVA_INT.withName("access_flags_"),
            JAVA_INT.withName("dex_method_index_"),
            JAVA_SHORT.withName("method_index_"),
            unionLayout(
                    JAVA_SHORT.withName("hotness_count_"),
                    JAVA_SHORT.withName("imt_index_")
            ),
            structLayout(
                    ADDRESS.withName("data_"),
                    ADDRESS.withName("entry_point_from_quick_compiled_code_")
            ).withName("ptr_sized_fields_")
    );

    private static final GroupLayout art_method_11_10_layout = structLayout(
            JAVA_OBJECT.withName("declaring_class_"),
            JAVA_INT.withName("access_flags_"),
            JAVA_INT.withName("dex_code_item_offset_"),
            JAVA_INT.withName("dex_method_index_"),
            JAVA_SHORT.withName("method_index_"),
            unionLayout(
                    JAVA_SHORT.withName("hotness_count_"),
                    JAVA_SHORT.withName("imt_index_")
            ),
            structLayout(
                    ADDRESS.withName("data_"),
                    ADDRESS.withName("entry_point_from_quick_compiled_code_")
            ).withName("ptr_sized_fields_")
    );

    private static final GroupLayout art_method_9_layout = structLayout(
            JAVA_OBJECT.withName("declaring_class_"),
            JAVA_INT.withName("access_flags_"),
            JAVA_INT.withName("dex_code_item_offset_"),
            JAVA_INT.withName("dex_method_index_"),
            JAVA_SHORT.withName("method_index_"),
            JAVA_SHORT.withName("hotness_count_"),
            structLayout(
                    ADDRESS.withName("data_"),
                    ADDRESS.withName("entry_point_from_quick_compiled_code_")
            ).withName("ptr_sized_fields_")
    );

    private static final GroupLayout art_method_8xx_layout = structLayout(
            JAVA_OBJECT.withName("declaring_class_"),
            JAVA_INT.withName("access_flags_"),
            JAVA_INT.withName("dex_code_item_offset_"),
            JAVA_INT.withName("dex_method_index_"),
            JAVA_SHORT.withName("method_index_"),
            JAVA_SHORT.withName("hotness_count_"),
            structLayout(
                    // ArtMethod** for oreo
                    // mirror::MethodDexCacheType for oreo mr 1
                    ADDRESS.withName("dex_cache_resolved_methods_"),
                    ADDRESS.withName("data_"),
                    ADDRESS.withName("entry_point_from_quick_compiled_code_")
            ).withName("ptr_sized_fields_")
    );

    public static final GroupLayout DEXFILE_LAYOUT = nothrows_run(() -> {
        switch (CORRECT_SDK_INT) {
            case 34: // android 14
                return dex_file_14_layout;
            case 33: // android 13
            case 32: // android 12L
            case 31: // android 12
            case 30: // android 11
            case 29: // android 10
                return dex_file_13_10_layout;
            case 28: // android 9
                return dex_file_9_layout;
            case 27: // android 8.1
            case 26: // android 8
                return dex_file_8xx_layout;
            default:
                throw new IllegalStateException("unsupported sdk: " + CORRECT_SDK_INT);
        }
    });

    public static final GroupLayout ARTMETHOD_LAYOUT = nothrows_run(() -> {
        switch (CORRECT_SDK_INT) {
            case 34: // android 14
            case 33: // android 13
            case 32: // android 12L
            case 31: // android 12
                return art_method_14_12_layout;
            case 30: // android 11
            case 29: // android 10
                return art_method_11_10_layout;
            case 28: // android 9
                return art_method_9_layout;
            case 27: // android 8.1
            case 26: // android 8
                return art_method_8xx_layout;
            default:
                throw new IllegalStateException("unsupported sdk: " + CORRECT_SDK_INT);
        }
    });

    private static final Class<?> dexCacheClass
            = nothrows_run(() -> Class.forName("java.lang.DexCache"));
    private static final Field dexFile = nothrows_run(
            () -> getDeclaredField(dexCacheClass, "dexFile"));

    public static Object getDexCache(Class<?> clazz) {
        ClassMirror[] m = arrayCast(ClassMirror.class, clazz);
        return m[0].dexCache;
    }

    public static long getDexFile(Class<?> clazz) {
        Object dexCache = getDexCache(clazz);
        long address = nothrows_run(() -> dexFile.getLong(dexCache));
        assert_(address != 0, IllegalStateException::new, "dexFile == 0");
        return address;
    }

    public static Pointer getDexFilePointer(Class<?> clazz) {
        return new Pointer(getDexFile(clazz));
    }

    public static MemorySegment getDexFileSegment(Class<?> clazz) {
        return DEXFILE_LAYOUT.bind(getDexFilePointer(clazz));
    }

    public static Pointer getArtMethodPointer(Executable ex) {
        return new Pointer(getArtMethod(ex));
    }

    public static MemorySegment getArtMethodSegment(Executable ex) {
        return ARTMETHOD_LAYOUT.bind(getArtMethodPointer(ex));
    }

    private static final Supplier<Constructor<DexFile>> dex_constructor = runOnce(() -> {
        if (CORRECT_SDK_INT >= 26 && CORRECT_SDK_INT <= 28) {
            return getDeclaredConstructor(DexFile.class, ByteBuffer.class);
        }
        if (CORRECT_SDK_INT >= 29 && CORRECT_SDK_INT <= 34) {
            Class<?> dex_path_list_elements = nothrows_run(
                    () -> Class.forName("[Ldalvik.system.DexPathList$Element;"));
            return getDeclaredConstructor(DexFile.class, ByteBuffer[].class,
                    ClassLoader.class, dex_path_list_elements);
        }
        throw new IllegalStateException("unsupported sdk: " + CORRECT_SDK_INT);
    });

    public static DexFile openDexFile(ByteBuffer data) {
        if (CORRECT_SDK_INT >= 26 && CORRECT_SDK_INT <= 28) {
            return nothrows_run(() -> dex_constructor.get().newInstance(data));
        } else if (CORRECT_SDK_INT >= 29 && CORRECT_SDK_INT <= 34) {
            return nothrows_run(() -> dex_constructor.get().newInstance(
                    new ByteBuffer[]{data}, null, null));
        } else {
            throw new IllegalStateException("unsupported sdk: " + CORRECT_SDK_INT);
        }
    }

    public static DexFile openDexFile(byte[] data) {
        return openDexFile(ByteBuffer.wrap(data));
    }

    private static final Field cookie = nothrows_run(
            () -> getDeclaredField(DexFile.class, "mCookie"));

    @DangerLevel(DangerLevel.VERY_CAREFUL)
    public static long[] getCookie(DexFile dex) {
        return (long[]) nothrows_run(() -> cookie.get(dex));
    }

    public static void setTrusted(Addressable dexfile) {
        if (CORRECT_SDK_INT >= 26 && CORRECT_SDK_INT <= 27) {
            return;
        }
        if (CORRECT_SDK_INT == 28) {
            DEXFILE_LAYOUT.bind(dexfile).select(groupElement("is_platform_dex_"))
                    .put(JAVA_BOOLEAN, 0, true);
            return;
        }
        if (CORRECT_SDK_INT >= 29 && CORRECT_SDK_INT <= 34) {
            DEXFILE_LAYOUT.bind(dexfile).select(groupElement("hiddenapi_domain_"))
                    .put(JAVA_BYTE, 0, /*kCorePlatform*/ (byte) 0);
            return;
        }
        throw new IllegalStateException("unsupported sdk: " + CORRECT_SDK_INT);
    }

    public static void setTrusted(DexFile dex) {
        if (CORRECT_SDK_INT >= 26 && CORRECT_SDK_INT <= 27) {
            return;
        }
        if (CORRECT_SDK_INT >= 28 && CORRECT_SDK_INT <= 34) {
            long[] cookie = getCookie(dex);
            for (int i = 1; i < cookie.length; i++) {
                setTrusted(new Pointer(cookie[i]));
            }
            return;
        }
        throw new IllegalStateException("unsupported sdk: " + CORRECT_SDK_INT);
    }

    private static final Supplier<MethodHandle> loadClassBinaryName = runOnce(
            () -> unreflectDirect(getDeclaredMethod(DexFile.class, "loadClassBinaryName",
                    String.class, ClassLoader.class, List.class)));

    public static Class<?> loadClass(DexFile dex, String name, ClassLoader loader) {
        List<Throwable> suppressed = new ArrayList<>();
        Class<?> out = (Class<?>) nothrows_run(() -> loadClassBinaryName.get()
                .invoke(dex, name.replace('.', '/'), loader, suppressed));
        if (!suppressed.isEmpty()) {
            RuntimeException err = new RuntimeException();
            for (Throwable tmp : suppressed) {
                err.addSuppressed(tmp);
            }
            throw err;
        }
        return out;
    }

    private static final long DATA_OFFSET = ARTMETHOD_LAYOUT.selectPath(
            groupElement("ptr_sized_fields_"), groupElement("data_")).offset();

    public static Pointer getExecutableData(Executable ex) {
        return new Pointer(getWordN(getArtMethod(ex) + DATA_OFFSET));
    }

    public static Pointer getExecutableData(Addressable art_method) {
        return new Pointer(getWordN(art_method.pointer().getRawAddress() + DATA_OFFSET));
    }

    @DangerLevel(DangerLevel.VERY_CAREFUL)
    public static void setExecutableData(Executable ex, Addressable data) {
        putWordN(getArtMethod(ex) + DATA_OFFSET, data.pointer().getRawAddress());
    }

    @DangerLevel(DangerLevel.VERY_CAREFUL)
    public static void setExecutableData(Addressable art_method, Addressable data) {
        putWordN(art_method.pointer().getRawAddress() + DATA_OFFSET,
                data.pointer().getRawAddress());
    }

    private static final long ACCESS_FLAGS_OFFSET = ARTMETHOD_LAYOUT
            .selectPath(groupElement("access_flags_")).offset();

    public static int getExecutableAccessFlags(Executable ex) {
        return getIntN(getArtMethod(ex) + ACCESS_FLAGS_OFFSET);
    }

    @DangerLevel(DangerLevel.VERY_CAREFUL)
    public static void setExecutableAccessFlags(Executable ex, int flags) {
        putIntN(getArtMethod(ex) + ACCESS_FLAGS_OFFSET, flags);
    }

    public enum AccessModifier {
        PUBLIC(Modifier.PUBLIC),
        PROTECTED(Modifier.PROTECTED),
        NONE(0),
        PRIVATE(Modifier.PRIVATE);

        public final int value;

        AccessModifier(int value) {
            this.value = value;
        }
    }

    @DangerLevel(DangerLevel.VERY_CAREFUL)
    public static void replaceExecutableAccessModifier(Executable ex, AccessModifier modifier) {
        final int all = PUBLIC.value | PROTECTED.value | PRIVATE.value;
        int flags = getExecutableAccessFlags(ex) & ~all;
        setExecutableAccessFlags(ex, flags | modifier.value);
        fullFence();
    }
}
