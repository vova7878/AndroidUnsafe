package com.v7878.unsafe;

import static com.v7878.misc.Math.is32Bit;
import static com.v7878.misc.Math.isSigned32Bit;
import static com.v7878.unsafe.AndroidUnsafe2.ADDRESS_SIZE;
import static com.v7878.unsafe.AndroidUnsafe2.IS64BIT;

public class MemoryChecks {

    public static boolean checkNativeAddress(long address) {
        return IS64BIT || isSigned32Bit(address);
    }

    public static boolean checkOffset(long offset) {
        if (ADDRESS_SIZE == 4) {
            // Note: this will also check for negative sizes
            return is32Bit(offset);
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
}
