package com.v7878.unsafe.io;

import static com.v7878.unsafe.AndroidUnsafe2.*;
import static com.v7878.unsafe.Utils.*;
import java.util.Arrays;

class ModifiableArray {

    private byte[] data;
    private int data_size;

    ModifiableArray(int size) {
        this.data = new byte[size];
        this.data_size = 0;
    }

    int size() {
        return data_size;
    }

    byte[] data() {
        return data;
    }

    byte[] copyData() {
        return Arrays.copyOf(data, data_size);
    }

    static final int GROW_FACTOR = 4096;

    void ensureSize(int new_size) {
        assert_(new_size >= 0, IllegalArgumentException::new, "negative size");
        if (new_size > data.length) {
            data = Arrays.copyOf(data,
                    roundUp(new_size, GROW_FACTOR) + GROW_FACTOR);
        }
        if (new_size > data_size) {
            data_size = new_size;
        }
    }
}

public class ByteArrayIO implements RandomIO {

    private final ModifiableArray arr;
    private int offset;

    private ByteArrayIO(ModifiableArray arr) {
        this.arr = arr;
        this.offset = 0;
    }

    public ByteArrayIO(int size) {
        this(new ModifiableArray(size));
    }

    public ByteArrayIO() {
        this(0);
    }

    private long grow(long n) {
        long new_offset = offset + n;
        assert_(new_offset >= offset && new_offset <= Integer.MAX_VALUE,
                IllegalArgumentException::new);
        arr.ensureSize((int) new_offset);
        offset = (int) new_offset;
        return new_offset - n;
    }

    @Override
    public void skipBytes(long n) {
        grow(n);
    }

    @Override
    public void writeByte(int value) {
        int arr_offset = ARRAY_BYTE_BASE_OFFSET + (int) grow(1);
        putByteO(arr.data(), arr_offset, (byte) value);
    }

    @Override
    public void writeShort(int value) {
        int arr_offset = ARRAY_BYTE_BASE_OFFSET + (int) grow(2);
        putShortUnaligned(arr.data(), arr_offset, (short) value);
    }

    @Override
    public void writeInt(int value) {
        int arr_offset = ARRAY_BYTE_BASE_OFFSET + (int) grow(4);
        putIntUnaligned(arr.data(), arr_offset, value);
    }

    @Override
    public void writeLong(long value) {
        int arr_offset = ARRAY_BYTE_BASE_OFFSET + (int) grow(8);
        putLongUnaligned(arr.data(), arr_offset, value);
    }

    @Override
    public byte readByte() {
        int arr_offset = ARRAY_BYTE_BASE_OFFSET + (int) grow(1);
        return getByteO(arr.data(), arr_offset);
    }

    @Override
    public short readShort() {
        int arr_offset = ARRAY_BYTE_BASE_OFFSET + (int) grow(2);
        return getShortUnaligned(arr.data(), arr_offset);
    }

    @Override
    public int readInt() {
        int arr_offset = ARRAY_BYTE_BASE_OFFSET + (int) grow(4);
        return getIntUnaligned(arr.data(), arr_offset);
    }

    @Override
    public long readLong() {
        int arr_offset = ARRAY_BYTE_BASE_OFFSET + (int) grow(8);
        return getLongUnaligned(arr.data(), arr_offset);
    }

    public byte[] toByteArray() {
        return arr.copyData();
    }

    @Override
    public long size() {
        return arr.size();
    }

    @Override
    public long position() {
        return offset;
    }

    @Override
    public void position(long new_position) {
        assert_(new_position >= 0 && new_position <= Integer.MAX_VALUE,
                IllegalArgumentException::new);
        arr.ensureSize((int) new_position);
        offset = (int) new_position;
    }

    @Override
    public ByteArrayIO duplicate() {
        return new ByteArrayIO(arr);
    }
}
