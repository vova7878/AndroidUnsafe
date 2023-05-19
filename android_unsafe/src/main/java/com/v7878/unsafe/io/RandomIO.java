package com.v7878.unsafe.io;

public interface RandomIO extends RandomInput, RandomOutput {

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
