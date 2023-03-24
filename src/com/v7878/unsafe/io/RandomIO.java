package com.v7878.unsafe.io;

public interface RandomIO extends RandomInput, RandomOutput {

    @Override
    public RandomIO duplicate();

    @Override
    public default RandomIO duplicate(long offset) {
        RandomIO out = duplicate();
        out.skipBytes(offset);
        return out;
    }

    @Override
    public default void close() {
    }
}
