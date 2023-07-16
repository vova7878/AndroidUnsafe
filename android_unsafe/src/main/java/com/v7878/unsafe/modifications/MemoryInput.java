package com.v7878.unsafe.modifications;

import com.v7878.dex.io.RandomInput;
import com.v7878.misc.Checks;
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

    @Override
    public byte readByte() {
        return data.get(ValueLayout.JAVA_BYTE, addPosition(1));
    }

    @Override
    public short readShort() {
        return data.get(ValueLayout.JAVA_SHORT, addPosition(2));
    }

    @Override
    public int readInt() {
        return data.get(ValueLayout.JAVA_INT, addPosition(4));
    }

    @Override
    public long readLong() {
        return data.get(ValueLayout.JAVA_LONG, addPosition(8));
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
    public long position(long new_position) {
        long tmp = offset;
        offset = Checks.checkPosition(new_position, data.size());
        return tmp;
    }

    @Override
    public RandomInput duplicate() {
        return new MemoryInput(data);
    }
}
