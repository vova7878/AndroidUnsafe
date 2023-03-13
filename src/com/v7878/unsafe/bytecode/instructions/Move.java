package com.v7878.unsafe.bytecode.instructions;

import com.v7878.unsafe.bytecode.instructions.InstructionReader.*;

public class Move extends Instruction {

    public static final int OPCODE = 0x01;

    static void init() {
        InstructionReader.register(OPCODE, new Reader_12x_11n((A, B) -> {
            return new Move(A, B);
        }));
    }

    public final int destination_register, source_register;

    public Move(int A, int B) {
        destination_register = A;
        source_register = B;
    }

    @Override
    public String toString() {
        return "move " + destination_register + " " + source_register;
    }
}
