package com.v7878.unsafe.bytecode.instructions;

import com.v7878.unsafe.bytecode.instructions.InstructionReader.*;

public class Return extends Instruction {

    public static final int OPCODE = 0x0f;

    static void init() {
        InstructionReader.register(OPCODE, new Reader_11x_10t((A) -> {
            return new Return(A);
        }));
    }

    public final int return_value_register;

    public Return(int A) {
        return_value_register = A;
    }

    @Override
    public String toString() {
        return "return " + return_value_register;
    }
}
