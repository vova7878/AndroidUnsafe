package com.v7878.unsafe.dex.modifications;

import static com.v7878.unsafe.AndroidUnsafe5.DEXFILE_LAYOUT;
import static com.v7878.unsafe.AndroidUnsafe5.arrayCast;
import static com.v7878.unsafe.AndroidUnsafe5.getDexFile;
import static com.v7878.unsafe.AndroidUnsafe5.loadClass;
import static com.v7878.unsafe.AndroidUnsafe5.openDexFile;
import static com.v7878.unsafe.AndroidUnsafe5.setTrusted;
import static com.v7878.unsafe.memory.LayoutPath.PathElement.groupElement;
import static com.v7878.unsafe.memory.ValueLayout.ADDRESS;
import static com.v7878.unsafe.memory.ValueLayout.WORD;

import com.v7878.unsafe.AndroidUnsafe3.ClassMirror;
import com.v7878.unsafe.dex.DataCollector;
import com.v7878.unsafe.dex.Dex;
import com.v7878.unsafe.io.MemoryInput;
import com.v7878.unsafe.memory.Layout;
import com.v7878.unsafe.memory.MemorySegment;
import com.v7878.unsafe.memory.Pointer;

import dalvik.system.DexFile;

public class Modifications {

    public static Dex readDex(Class<?>... classes) {
        ClassMirror[] cm = arrayCast(ClassMirror.class, (Object[]) classes);
        long dex_file = 0;
        int[] dex_ids = new int[classes.length];
        for (int i = 0; i < classes.length; i++) {
            int dex_id = cm[i].dexClassDefIndex;
            if (dex_id >= 0xffff) {
                throw new IllegalArgumentException("illegal dexClassDefIndex: " + dex_id);
            }
            dex_ids[i] = dex_id;
            if (dex_file == 0) {
                dex_file = getDexFile(classes[i]);
            } else if (dex_file != getDexFile(classes[i])) {
                throw new IllegalArgumentException("classes must be from the same dex file");
            }
        }
        MemorySegment dex_segment = DEXFILE_LAYOUT.bind(new Pointer(dex_file));
        Pointer data = dex_segment.select(groupElement("begin_"))
                .get(ADDRESS, 0);
        long size = dex_segment.select(groupElement("size_"))
                .get(WORD, 0).longValue();

        return Dex.read(new MemoryInput(
                Layout.rawLayout(size).bind(data)), dex_ids);
    }

    public static class EmptyClassLoader extends ClassLoader {

        public EmptyClassLoader(ClassLoader parent) {
            super(parent);
        }
    }

    public static Class<?> reload(Class<?> clazz,
                                  DataCollector data, ClassLoader loader) {
        Dex dex = readDex(clazz);
        dex.collectData(data);
        byte[] bytes = dex.compile();
        //noinspection deprecation
        DexFile df = openDexFile(bytes);
        setTrusted(df);
        return loadClass(df, clazz.getName(), loader);
    }

    public static Class<?> reload(Class<?> clazz, DataCollector data) {
        return reload(clazz, data, new EmptyClassLoader(clazz.getClassLoader()));
    }
}
