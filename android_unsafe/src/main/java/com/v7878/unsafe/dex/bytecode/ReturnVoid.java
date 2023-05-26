package com.v7878.unsafe.dex.bytecode;

import com.v7878.unsafe.dex.WriteContext;
import com.v7878.unsafe.dex.bytecode.InstructionReader.Reader_10x;
import com.v7878.unsafe.io.RandomOutput;

public class ReturnVoid extends Instruction {

    public static final int OPCODE = 0x0e;

    static void init() {
        InstructionReader.register(OPCODE, new Reader_10x(ReturnVoid::new));
    }

    @Override
    public void write(WriteContext context, RandomOutput out) {
        InstructionWriter.write_10x(out, OPCODE);
    }

    @Override
    public int opcode() {
        return OPCODE;
    }

    @Override
    public String name() {
        return "return-void";
    }

    @Override
    public String toString() {
        return name();
    }

    @Override
    public ReturnVoid clone() {
        return new ReturnVoid();
    }
}