package com.v7878.unsafe.memory;

import static com.v7878.unsafe.AndroidUnsafe.*;
import static com.v7878.unsafe.Checks.*;
import com.v7878.unsafe.Utils;

public final class Word extends Number {

    private final long value;

    public Word() {
        this.value = 0;
    }

    public Word(long value) {
        if (!checkNativeAddress(value)) {
            throw new IllegalArgumentException(
                    "value is too big to represent as a word: " + value);
        }
        this.value = value;
    }

    public Word(int value) {
        this.value = value;
    }

    public boolean is32BitOnly() {
        return Utils.isSigned32Bit(value);
    }

    @Override
    public int intValue() {
        return (int) value;
    }

    @Override
    public long longValue() {
        return IS64BIT ? value : ((value << 32) >> 32);
    }

    public long ulongValue() {
        return IS64BIT ? value : (value & 0xffffffffL);
    }

    @Override
    public float floatValue() {
        return longValue();
    }

    @Override
    public double doubleValue() {
        return longValue();
    }

    @Override
    public String toString() {
        return Long.toString(longValue());
    }
}
