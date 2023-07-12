package com.v7878.unsafe.io;

import static com.v7878.unsafe.AndroidUnsafe.IS64BIT;
import static com.v7878.unsafe.Utils.roundUpL;

import com.v7878.unsafe.Checks;
import com.v7878.unsafe.Utils;
import com.v7878.unsafe.memory.Word;

import java.util.Objects;

public interface RandomOutput extends AutoCloseable {

    default void writeByteArray(byte[] arr) {
        writeByteArray(arr, 0, arr.length);
    }

    default void writeByteArray(byte[] arr, int off, int len) {
        Objects.requireNonNull(arr);
        Checks.checkFromIndexSize(off, len, arr.length);
        if (len == 0) {
            return;
        }
        for (int i = 0; i < len; i++) {
            writeByte(arr[i + off]);
        }
    }

    default void writeShortArray(short[] shorts) {
        for (short value : shorts) {
            writeShort(value);
        }
    }

    default void writeIntArray(int[] ints) {
        for (int value : ints) {
            writeInt(value);
        }
    }

    default void writeBoolean(boolean value) {
        writeByte(value ? 1 : 0);
    }

    void writeByte(int value);

    void writeShort(int value);

    default void writeChar(int value) {
        writeShort(value);
    }

    void writeInt(int value);

    void writeLong(long value);

    default void writeFloat(float value) {
        writeInt(Float.floatToRawIntBits(value));
    }

    default void writeDouble(double value) {
        writeLong(Double.doubleToRawLongBits(value));
    }

    default void writeWord(Word value) {
        if (IS64BIT) {
            writeLong(value.longValue());
        } else {
            writeInt(value.intValue());
        }
    }

    default void writeULeb128(int value) {
        Leb128.writeUnsignedLeb128(this, value);
    }

    default void writeSLeb128(int value) {
        Leb128.writeSignedLeb128(this, value);
    }

    default void writeMUtf8(String value) {
        MUTF8.writeMUTF8(this, value);
    }

    long size();

    long position();

    long position(long new_position);

    default long addPosition(long delta) {
        return position(position() + delta);
    }

    default long alignPosition(long alignment) {
        return position(roundUpL(position(), alignment));
    }

    default long alignPositionAndFillZeros(long alignment) {
        long old_position = position();
        long new_position = roundUpL(old_position, alignment);
        for (long i = 0; i < new_position - old_position; i++) {
            writeByte(0);
        }
        return old_position;
    }

    default void requireAlignment(int alignment) {
        long pos = position();
        if (!Utils.isAlignedL(pos, alignment)) {
            throw new IllegalStateException("position " + pos + " not aligned by " + alignment);
        }
    }

    RandomOutput duplicate();

    default RandomOutput duplicate(long offset) {
        RandomOutput out = duplicate();
        out.addPosition(offset);
        return out;
    }

    @Override
    default void close() {
    }
}
