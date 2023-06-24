package com.v7878.unsafe.dex.bytecode2;

import com.v7878.unsafe.dex.DataCollector;
import com.v7878.unsafe.dex.PublicCloneable;
import com.v7878.unsafe.dex.ReadContext;
import com.v7878.unsafe.dex.WriteContext;
import com.v7878.unsafe.io.RandomInput;
import com.v7878.unsafe.io.RandomOutput;

public abstract class Instruction implements PublicCloneable {

    public static Instruction read(RandomInput in, ReadContext context) {
        //TODO
        return null;
    }

    public abstract void collectData(DataCollector data);

    public abstract void write(WriteContext context, RandomOutput out);

    public abstract Opcode opcode();

    public abstract int units();

    @Override
    public abstract String toString();

    @Override
    public abstract boolean equals(Object obj);

    @Override
    public abstract int hashCode();

    @Override
    public abstract Instruction clone();
}
