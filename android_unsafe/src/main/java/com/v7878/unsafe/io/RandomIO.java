package com.v7878.unsafe.io;

import static com.v7878.unsafe.Utils.roundUpL;

import com.v7878.unsafe.Utils;

public interface RandomIO extends RandomInput, RandomOutput {

    default long alignPosition(long alignment) {
        long new_position = roundUpL(position(), alignment);
        position(new_position);
        return new_position;
    }

    default void requireAlignment(int alignment) {
        long pos = position();
        if (!Utils.isAlignedL(pos, alignment)) {
            throw new IllegalStateException("position " + pos + " not aligned by " + alignment);
        }
    }

    @Override
    RandomIO duplicate();

    @Override
    default RandomIO duplicate(long offset) {
        RandomIO out = duplicate();
        out.skipBytes(offset);
        return out;
    }

    @Override
    default void close() {
    }
}
