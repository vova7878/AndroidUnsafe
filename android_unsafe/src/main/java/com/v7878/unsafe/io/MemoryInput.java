package com.v7878.unsafe.io;

import com.v7878.unsafe.Checks;
import com.v7878.unsafe.memory.MemorySegment;
import com.v7878.unsafe.memory.ValueLayout;

import java.util.Objects;

public class MemoryInput implements RandomInput {

    private final MemorySegment data;
    private long offset;

    public MemoryInput(MemorySegment data) {
        Objects.requireNonNull(data);
        this.data = data;
        this.offset = 0;
    }

    private long grow(long n) {
        long tmp = Checks.checkFromIndexSize(offset, n, data.size());
        offset += n;
        return tmp;
    }

    @Override
    public void skipBytes(long n) {
        grow(n);
    }

    @Override
    public byte readByte() {
        return data.get(ValueLayout.JAVA_BYTE, grow(1));
    }

    @Override
    public short readShort() {
        return data.get(ValueLayout.JAVA_SHORT, grow(2));
    }

    @Override
    public int readInt() {
        return data.get(ValueLayout.JAVA_INT, grow(4));
    }

    @Override
    public long readLong() {
        return data.get(ValueLayout.JAVA_LONG, grow(8));
    }

    @Override
    public long size() {
        return data.size();
    }

    @Override
    public long position() {
        return offset;
    }

    @Override
    public void position(long new_position) {
        offset = Checks.checkIndex(new_position, data.size());
    }

    @Override
    public RandomInput duplicate() {
        return new MemoryInput(data);
    }
}
