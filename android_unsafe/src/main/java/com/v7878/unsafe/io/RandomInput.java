package com.v7878.unsafe.io;

import static com.v7878.unsafe.AndroidUnsafe.IS64BIT;

import com.v7878.unsafe.Checks;
import com.v7878.unsafe.memory.Word;

import java.util.Objects;

public interface RandomInput extends AutoCloseable {

    default void readFully(byte[] arr) {
        readFully(arr, 0, arr.length);
    }

    default void readFully(byte[] arr, int off, int len) {
        Objects.requireNonNull(arr);
        Checks.checkFromIndexSize(off, len, arr.length);
        if (len == 0) {
            return;
        }
        for (int i = 0; i < len; i++) {
            arr[i + off] = readByte();
        }
    }

    default byte[] readByteArray(int length) {
        byte[] result = new byte[length];
        readFully(result);
        return result;
    }

    default short[] readShortArray(int length) {
        short[] result = new short[length];
        for (int i = 0; i < length; i++) {
            result[i] = readShort();
        }
        return result;
    }

    void skipBytes(long n);

    default boolean readBoolean() {
        return readByte() != 0;
    }

    byte readByte();

    default int readUnsignedByte() {
        return readByte() & 0xff;
    }

    short readShort();

    default int readUnsignedShort() {
        return readShort() & 0xffff;
    }

    default char readChar() {
        return (char) readShort();
    }

    int readInt();

    long readLong();

    default float readFloat() {
        return Float.intBitsToFloat(readInt());
    }

    default double readDouble() {
        return Double.longBitsToDouble(readLong());
    }

    default Word readWord() {
        return new Word(IS64BIT ? readLong() : readInt());
    }

    default int readULeb128() {
        return Leb128.readUnsignedLeb128(this);
    }

    default int readSLeb128() {
        return Leb128.readSignedLeb128(this);
    }

    default String readMUTF8() {
        return MUTF8.readMUTF8(this);
    }

    long size();

    long position();

    void position(long new_position);

    RandomInput duplicate();

    default RandomInput duplicate(long offset) {
        RandomInput out = duplicate();
        out.skipBytes(offset);
        return out;
    }

    @Override
    default void close() {
    }
}