package com.v7878.unsafe;

import static com.v7878.unsafe.AndroidUnsafe2.*;
import static com.v7878.unsafe.Utils.*;

public class Checks {

    public static boolean checkNativeAddress(long address) {
        if (ADDRESS_SIZE == 4) {
            return is32BitOnly(address);
        }
        return true;
    }

    public static boolean checkOffset(long offset) {
        if (ADDRESS_SIZE == 4) {
            // Note: this will also check for negative sizes
            return is32BitOnly(offset);
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
