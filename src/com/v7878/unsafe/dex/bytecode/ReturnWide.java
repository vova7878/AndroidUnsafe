package com.v7878.unsafe.dex.bytecode;

import com.v7878.unsafe.dex.bytecode.InstructionReader.*;

public class ReturnWide extends Instruction {

    public static final int OPCODE = 0x10;

    static void init() {
        InstructionReader.register(OPCODE, new Reader_11x_10t((A) -> {
            return new ReturnWide(A);
        }));
    }

    public final int return_value_register_pair;

    public ReturnWide(int A) {
        return_value_register_pair = A;
    }

    @Override
    public String toString() {
        return "return-wide " + return_value_register_pair;
    }
}
