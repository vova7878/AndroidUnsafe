package com.v7878.unsafe;

import static com.v7878.unsafe.AndroidUnsafe2.ADDRESS_SIZE;
import static com.v7878.unsafe.AndroidUnsafe2.IS64BIT;
import static com.v7878.unsafe.Utils.is32Bit;
import static com.v7878.unsafe.Utils.isSigned32Bit;

public class Checks {

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

    public static long checkFromIndexSize(long fromIndex, long size, long length) {
        if ((length | fromIndex | size) < 0 || size > length - fromIndex) {
            throw new IndexOutOfBoundsException(
                    String.format("Range [%s, %<s + %s) out of bounds for length %s",
                            fromIndex, size, length));
        }
        return fromIndex;
    }

    public static int checkFromIndexSize(int fromIndex, int size, int length) {
        if ((length | fromIndex | size) < 0 || size > length - fromIndex) {
            throw new IndexOutOfBoundsException(
                    String.format("Range [%s, %<s + %s) out of bounds for length %s",
                            fromIndex, size, length));
        }
        return fromIndex;
    }

    public static long checkFromToIndex(long fromIndex, long toIndex, long length) {
        if (fromIndex < 0 || fromIndex > toIndex || toIndex > length) {
            throw new IndexOutOfBoundsException(
                    String.format("Range [%s, %<s + %s) out of bounds for length %s",
                            fromIndex, toIndex, length));
        }
        return fromIndex;
    }

    public static int checkFromToIndex(int fromIndex, int toIndex, int length) {
        if (fromIndex < 0 || fromIndex > toIndex || toIndex > length) {
            throw new IndexOutOfBoundsException(
                    String.format("Range [%s, %<s + %s) out of bounds for length %s",
                            fromIndex, toIndex, length));
        }
        return fromIndex;
    }

    public static long checkIndex(long index, long length) {
        if (index < 0 || index >= length) {
            throw new IndexOutOfBoundsException(
                    String.format("Index %s out of bounds for length %s",
                            index, length));
        }
        return index;
    }

    public static int checkIndex(int index, int length) {
        if (index < 0 || index >= length) {
            throw new IndexOutOfBoundsException(
                    String.format("Index %s out of bounds for length %s",
                            index, length));
        }
        return index;
    }
}
