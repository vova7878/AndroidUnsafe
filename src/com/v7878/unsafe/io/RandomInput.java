package com.v7878.unsafe.io;

import java.util.Objects;

public interface RandomInput extends AutoCloseable {

    public default void readFully(byte[] b) {
        readFully(b, 0, b.length);
    }

    public default void readFully(byte[] arr, int off, int len) {
        Objects.requireNonNull(arr);
        if (off < 0 || len < 0 || len > arr.length - off) {
            throw new IllegalArgumentException();
        } else if (len == 0) {
            return;
        }
        for (int i = 0; i < len; i++) {
            arr[i + off] = readByte();
        }
    }

    public default byte[] readByteArray(int length) {
        byte[] result = new byte[length];
        readFully(result);
        return result;
    }

    public default short[] readShortArray(int length) {
        short[] result = new short[length];
        for (int i = 0; i < length; i++) {
            result[i] = readShort();
        }
        return result;
    }

    public void skipBytes(long n);

    public default boolean readBoolean() {
        return readByte() != 0;
    }

    public byte readByte();

    public default int readUnsignedByte() {
        return readByte() & 0xff;
    }

    public short readShort();

    public default int readUnsignedShort() {
        return readShort() & 0xffff;
    }

    public default char readChar() {
        return (char) readShort();
    }

    public int readInt();

    public long readLong();

    public default float readFloat() {
        return Float.intBitsToFloat(readInt());
    }

    public default double readDouble() {
        return Double.longBitsToDouble(readLong());
    }

    public default int readULeb128() {
        return Leb128.readUnsignedLeb128(this);
    }

    public default int readSLeb128() {
        return Leb128.readSignedLeb128(this);
    }

    public default String readMUTF8() {
        return MUTF8.readMUTF8(this);
    }

    public long size();

    public long position();

    public void position(long new_position);

    public RandomInput duplicate();

    public default RandomInput duplicate(long offset) {
        RandomInput out = duplicate();
        out.skipBytes(offset);
        return out;
    }

    @Override
    public default void close() {
    }
}
