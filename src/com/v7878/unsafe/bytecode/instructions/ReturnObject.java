package com.v7878.unsafe.bytecode.instructions;

import com.v7878.unsafe.bytecode.instructions.InstructionReader.*;

public class ReturnObject extends Instruction {

    public static final int OPCODE = 0x11;

    static void init() {
        InstructionReader.register(OPCODE, new Reader_11x_10t((A) -> {
            return new ReturnObject(A);
        }));
    }

    public final int return_value_register;

    public ReturnObject(int A) {
        return_value_register = A;
    }

    @Override
    public String toString() {
        return "return-object " + return_value_register;
    }
}
