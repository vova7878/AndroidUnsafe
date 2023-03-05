package com.v7878.unsafe.memory;

import static com.v7878.unsafe.AndroidUnsafe.*;
import com.v7878.unsafe.Utils;
import static com.v7878.unsafe.Utils.*;

public final class Word extends Number {

    private final long value;

    public Word() {
        this.value = 0;
    }

    public Word(long value) {
        assert_(IS64BIT || Utils.is32BitOnly(value), IllegalArgumentException::new);
        this.value = value;
    }

    public Word(int value) {
        this.value = value & 0xffffffffL;
    }

    public boolean is32BitOnly() {
        return (!IS64BIT) || Utils.is32BitOnly(value);
    }

    @Override
    public int intValue() {
        return (int) value;
    }

    @Override
    public long longValue() {
        return value;
    }

    @Override
    public float floatValue() {
        return value;
    }

    @Override
    public double doubleValue() {
        return value;
    }

    @Override
    public String toString() {
        return Long.toString(value);
    }
}
