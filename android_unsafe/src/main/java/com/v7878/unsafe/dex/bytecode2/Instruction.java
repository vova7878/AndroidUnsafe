package com.v7878.unsafe.dex.bytecode2;

import com.v7878.unsafe.dex.DataCollector;
import com.v7878.unsafe.dex.DexOptions;
import com.v7878.unsafe.dex.PublicCloneable;
import com.v7878.unsafe.dex.WriteContext;
import com.v7878.unsafe.io.RandomOutput;

//temporary inheritance from bytecode.Instruction
public abstract class Instruction extends com.v7878.unsafe.dex.bytecode.Instruction implements PublicCloneable {

    /*public static Instruction read(RandomInput in, ReadContext context) {
        //TODO
        return null;
    }*/

    public void collectData(DataCollector data) {
    }

    public abstract void write(WriteContext context, RandomOutput out);

    //TODO: rename to opcode
    public abstract Opcode opcode2();

    public int units() {
        return opcode2().format().units();
    }

    @Override
    public abstract String toString();

    @Override
    public abstract boolean equals(Object obj);

    @Override
    public abstract int hashCode();

    @Override
    public abstract Instruction clone();


    //TODO: delete
    public String name() {
        return opcode2().opname();
    }

    //TODO: delete
    public int opcode() {
        return opcode2().opcodeValue(DexOptions.defaultOptions());
    }
}
