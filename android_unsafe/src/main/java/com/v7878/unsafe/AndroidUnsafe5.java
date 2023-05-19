package com.v7878.unsafe;

import static com.v7878.unsafe.Utils.assert_;
import static com.v7878.unsafe.Utils.getSdkInt;
import static com.v7878.unsafe.Utils.nothrows_run;
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

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

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

    private static final GroupLayout currentDexFileLayout = nothrows_run(() -> {
        switch (getSdkInt()) {
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
                throw new IllegalStateException("unsupported sdk: " + getSdkInt());
        }
    });

    private static final GroupLayout currentArtMethodLayout = nothrows_run(() -> {
        switch (getSdkInt()) {
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
                throw new IllegalStateException("unsupported sdk: " + getSdkInt());
        }
    });

    private static final Class<?> dexCacheClass
            = nothrows_run(() -> Class.forName("java.lang.DexCache"));
    private static final Field dexFile = nothrows_run(() -> {
        Field tmp = getDeclaredField(dexCacheClass, "dexFile");
        setAccessible(tmp, true);
        return tmp;
    });

    public static Object getDexCache(Class<?> clazz) {
        ClassMirror[] m = arrayCast(ClassMirror.class, clazz);
        return m[0].dexCache;
    }

    public static GroupLayout getDexFileLayout() {
        return currentDexFileLayout;
    }

    public static GroupLayout getArtMethodLayout() {
        return currentArtMethodLayout;
    }

    public static long getDexFile(Class<?> clazz) {
        Object dexCache = getDexCache(clazz);
        long address = nothrows_run(() -> dexFile.getLong(dexCache));
        assert_(address != 0, IllegalStateException::new, "dexFile == 0");
        return address;
    }

    public static MemorySegment getDexFileSegment(Class<?> clazz) {
        return getDexFileLayout().bind(new Pointer(getDexFile(clazz)));
    }

    public static MemorySegment getArtMethodSegment(Executable ex) {
        return getArtMethodLayout()
                .bind(new Pointer(getArtMethod(ex)));
    }

    private static Constructor<DexFile> dex_constructor;

    private synchronized static void initDex() {
        if (dex_constructor == null) {
            if (getSdkInt() >= 26 && getSdkInt() <= 28) {
                dex_constructor = nothrows_run(() -> getDeclaredConstructor(
                        DexFile.class, ByteBuffer.class));
            } else if (getSdkInt() >= 29 && getSdkInt() <= 34) {
                Class<?> dex_path_list_elements = nothrows_run(()
                        -> Class.forName("[Ldalvik.system.DexPathList$Element;"));
                dex_constructor = nothrows_run(() -> getDeclaredConstructor(
                        DexFile.class, ByteBuffer[].class,
                        ClassLoader.class, dex_path_list_elements));
            } else {
                throw new IllegalStateException("unsupported sdk: " + getSdkInt());
            }
            setAccessible(dex_constructor, true);
        }
    }

    public static DexFile openDexFile(ByteBuffer data) {
        initDex();
        if (getSdkInt() >= 26 && getSdkInt() <= 28) {
            return nothrows_run(() -> dex_constructor.newInstance(data), true);
        } else if (getSdkInt() >= 29 && getSdkInt() <= 34) {
            return nothrows_run(() -> dex_constructor.newInstance(
                    new ByteBuffer[]{data}, null, null), true);
        } else {
            throw new IllegalStateException("unsupported sdk: " + getSdkInt());
        }
    }

    public static DexFile openDexFile(byte[] data) {
        return openDexFile(ByteBuffer.wrap(data));
    }

    private static Method set_dex_trusted;

    private synchronized static void initSetTrusted() {
        if (getSdkInt() >= 26 && getSdkInt() <= 27) {
            return;
        }
        if (set_dex_trusted == null) {
            if (getSdkInt() >= 28 && getSdkInt() <= 34) {
                set_dex_trusted = nothrows_run(() -> getDeclaredMethod(
                        DexFile.class, "setTrusted"));
            } else {
                throw new IllegalStateException("unsupported sdk: " + getSdkInt());
            }
            setAccessible(set_dex_trusted, true);
        }
    }

    public static void setTrusted(DexFile dex) {
        if (getSdkInt() >= 26 && getSdkInt() <= 27) {
            return;
        }
        initSetTrusted();
        nothrows_run(() -> set_dex_trusted.invoke(dex), true);
    }

    private static Method loadClassBinaryName;

    private synchronized static void initLoad() {
        if (loadClassBinaryName == null) {
            loadClassBinaryName = getDeclaredMethod(DexFile.class,
                    "loadClassBinaryName", String.class,
                    ClassLoader.class, List.class);
            setAccessible(loadClassBinaryName, true);
        }
    }

    public static Class<?> loadClass(DexFile dex, String name, ClassLoader loader) {
        initLoad();
        List<Throwable> suppressed = new ArrayList<>();
        Class<?> out = (Class<?>) nothrows_run(() -> loadClassBinaryName
                .invoke(dex, name.replace('.', '/'),
                        loader, suppressed), true);
        if (!suppressed.isEmpty()) {
            RuntimeException err = new RuntimeException();
            for (Throwable tmp : suppressed) {
                err.addSuppressed(tmp);
            }
            throw err;
        }
        return out;
    }

    private static final long DATA_OFFSET = getArtMethodLayout()
            .selectPath(groupElement("ptr_sized_fields_"),
                    groupElement("data_")).offset();

    public static Pointer getExecutableData(Executable ex) {
        long art_method = getArtMethod(ex);
        return new Pointer(getWordN(art_method + DATA_OFFSET));
    }

    public static void setExecutableData(Executable ex, Addressable data) {
        long art_method = getArtMethod(ex);
        putWordN(art_method + DATA_OFFSET, data.pointer().getRawAddress());
    }

    private static final long ACCESS_FLAGS_OFFSET = getArtMethodLayout()
            .selectPath(groupElement("access_flags_")).offset();

    public static int getExecutableAccessFlags(Executable ex) {
        long art_method = getArtMethod(ex);
        return getIntN(art_method + ACCESS_FLAGS_OFFSET);
    }

    public static void setExecutableAccessFlags(Executable ex, int flags) {
        long art_method = getArtMethod(ex);
        putIntN(art_method + ACCESS_FLAGS_OFFSET, flags);
    }

    private static final long ENTRY_POINT_OFFSET = getArtMethodLayout()
            .selectPath(groupElement("ptr_sized_fields_"),
                    groupElement("entry_point_from_quick_compiled_code_")).offset();

    public static Pointer getExecutableEntryPoint(Executable ex) {
        long art_method = getArtMethod(ex);
        return new Pointer(getWordN(art_method + ENTRY_POINT_OFFSET));
    }
}
