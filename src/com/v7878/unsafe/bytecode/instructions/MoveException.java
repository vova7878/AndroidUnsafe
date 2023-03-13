package com.v7878.unsafe.bytecode.instructions;

import com.v7878.unsafe.bytecode.instructions.InstructionReader.*;

public class MoveException extends Instruction {

    public static final int OPCODE = 0x0d;

    static void init() {
        InstructionReader.register(OPCODE, new Reader_11x_10t((A) -> {
            return new MoveException(A);
        }));
    }

    public final int destination_register;

    public MoveException(int A) {
        destination_register = A;
    }

    @Override
    public String toString() {
        return "move-exception " + destination_register;
    }
}
