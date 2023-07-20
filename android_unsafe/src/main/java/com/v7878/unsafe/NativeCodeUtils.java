package com.v7878.unsafe;

import static android.system.Os.mmap;
import static android.system.Os.munmap;
import static com.v7878.misc.Math.roundUpL;
import static com.v7878.unsafe.AndroidUnsafe.ARRAY_BYTE_BASE_OFFSET;
import static com.v7878.unsafe.AndroidUnsafe2.copyMemory;
import static com.v7878.unsafe.Utils.nothrows_run;

import android.system.OsConstants;

import com.v7878.unsafe.memory.Pointer;

class NativeCodeUtils {
    private static final int CODE_ALIGNMENT = 16;
    private static final int CODE_PROT = OsConstants.PROT_READ | OsConstants.PROT_WRITE | OsConstants.PROT_EXEC;
    private static final int MAP_ANONYMOUS = 0x20;
    private static final int CODE_FLAGS = OsConstants.MAP_PRIVATE | MAP_ANONYMOUS;

    //TODO: clone arrays?
    //TODO: cleanup code
    public static Pointer[] makeCode(Object lifetime, byte[]... code) {
        int count = code.length;
        long size = 0;
        long[] offsets = new long[count];
        for (int i = 0; i < count; i++) {
            size = roundUpL(size, CODE_ALIGNMENT);
            offsets[i] = size;
            size += code[i].length;
        }
        long finalSize = size;
        Pointer data = new Pointer((long) nothrows_run(() -> mmap(0, finalSize, CODE_PROT, CODE_FLAGS, null, 0)));
        if (lifetime != null) {
            sun.misc.Cleaner.create(lifetime, () -> nothrows_run(() -> munmap(data.getRawAddress(), finalSize)));
        }
        Pointer[] out = new Pointer[count];
        for (int i = 0; i < count; i++) {
            Pointer tmp = data.addOffset(offsets[i]);
            copyMemory(code[i], ARRAY_BYTE_BASE_OFFSET, tmp.getBase(), tmp.getOffset(), code[i].length);
            System.out.println(tmp);
            out[i] = tmp;
        }
        return out;
    }
}
