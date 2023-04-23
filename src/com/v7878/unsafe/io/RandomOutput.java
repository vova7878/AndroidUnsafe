package com.v7878.unsafe.io;

import static com.v7878.unsafe.AndroidUnsafe.IS64BIT;
import com.v7878.unsafe.Checks;
import com.v7878.unsafe.memory.Word;
import java.util.Objects;

public interface RandomOutput extends AutoCloseable {

    public default void writeByteArray(byte[] arr) {
        writeByteArray(arr, 0, arr.length);
    }

    public default void writeByteArray(byte[] arr, int off, int len) {
        Objects.requireNonNull(arr);
        Checks.checkFromIndexSize(off, len, arr.length);
        if (len == 0) {
            return;
        }
        for (int i = 0; i < len; i++) {
            writeByte(arr[i + off]);
        }
    }

    public default void writeShortArray(short[] shorts) {
        for (short value : shorts) {
            writeShort(value);
        }
    }

    public void skipBytes(long n);

    public default void writeBoolean(boolean value) {
        writeByte(value ? 1 : 0);
    }

    public void writeByte(int value);

    public void writeShort(int value);

    public default void writeChar(int value) {
        writeShort(value);
    }

    public void writeInt(int value);

    public void writeLong(long value);

    public default void writeFloat(float value) {
        writeInt(Float.floatToRawIntBits(value));
    }

    public default void writeDouble(double value) {
        writeLong(Double.doubleToRawLongBits(value));
    }

    public default void writeWord(Word value) {
        if (IS64BIT) {
            writeLong(value.longValue());
        } else {
            writeInt(value.intValue());
        }
    }

    public default void writeULeb128(int value) {
        Leb128.writeUnsignedLeb128(this, value);
    }

    public default void writeSLeb128(int value) {
        Leb128.writeSignedLeb128(this, value);
    }

    public default void writeMUtf8(String value) {
        MUTF8.writeMUTF8(this, value);
    }

    public long size();

    public long position();

    public void position(long new_position);

    public RandomOutput duplicate();

    public default RandomOutput duplicate(long offset) {
        RandomOutput out = duplicate();
        out.skipBytes(offset);
        return out;
    }

    @Override
    public default void close() {
    }
}
