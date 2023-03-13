package com.v7878.unsafe.bytecode.instructions;

import com.v7878.unsafe.bytecode.instructions.InstructionReader.*;

public class Throw extends Instruction {

    public static final int OPCODE = 0x27;

    static void init() {
        InstructionReader.register(OPCODE, new Reader_11x_10t((A) -> {
            return new Throw(A);
        }));
    }

    public final int exception_bearing_register;

    public Throw(int A) {
        exception_bearing_register = A;
    }

    @Override
    public String toString() {
        return "throw " + exception_bearing_register;
    }
}
