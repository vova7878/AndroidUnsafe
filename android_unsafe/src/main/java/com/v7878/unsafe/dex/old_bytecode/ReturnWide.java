package com.v7878.unsafe.dex.old_bytecode;

import com.v7878.unsafe.dex.WriteContext;
import com.v7878.unsafe.dex.old_bytecode.InstructionReader.Reader_11x;
import com.v7878.unsafe.io.RandomOutput;

public class ReturnWide extends Instruction {

    public static final int OPCODE = 0x10;

    static void init() {
        InstructionReader.register(OPCODE, new Reader_11x(ReturnWide::new));
    }

    public final int return_value_register_pair;

    public ReturnWide(int A) {
        return_value_register_pair = A;
    }

    @Override
    public void write(WriteContext context, RandomOutput out) {
        InstructionWriter.write_11x(out, OPCODE, return_value_register_pair);
    }

    @Override
    public int opcode_old() {
        return OPCODE;
    }

    @Override
    public String name() {
        return "return-wide";
    }

    @Override
    public String toString() {
        return name() + " " + return_value_register_pair;
    }

    @Override
    public ReturnWide clone() {
        return new ReturnWide(return_value_register_pair);
    }
}
