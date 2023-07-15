package com.v7878.misc;

public class Checks {

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

    public static long checkPosition(long position, long length) {
        if (position < 0 || position > length) {
            throw new IndexOutOfBoundsException(
                    String.format("Position %s out of bounds for length %s",
                            position, length));
        }
        return position;
    }

    public static int checkPosition(int position, int length) {
        if (position < 0 || position > length) {
            throw new IndexOutOfBoundsException(
                    String.format("Position %s out of bounds for length %s",
                            position, length));
        }
        return position;
    }

    public static int checkRange(int value, int start, int length) {
        if (length < 0 || value < start || value >= start + length) {
            throw new IndexOutOfBoundsException(
                    String.format("value %s out of range [%s, %<s + %s)",
                            value, start, length));
        }
        return value;
    }

    public static long checkRange(long value, long start, long length) {
        if (length < 0 || value < start || value >= start + length) {
            throw new IndexOutOfBoundsException(
                    String.format("value %s out of range [%s, %<s + %s)",
                            value, start, length));
        }
        return value;
    }
}
