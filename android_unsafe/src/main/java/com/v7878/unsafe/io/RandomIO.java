package com.v7878.unsafe.io;

import static com.v7878.unsafe.Utils.roundUpL;

public interface RandomIO extends RandomInput, RandomOutput {

    default long alignPosition(long alignment) {
        long new_position = roundUpL(position(), alignment);
        position(new_position);
        return new_position;
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
