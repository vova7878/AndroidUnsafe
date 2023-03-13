package com.v7878.unsafe.bytecode.instructions;

import com.v7878.unsafe.bytecode.instructions.InstructionReader.*;

public class Const extends Instruction {

    public static final int OPCODE = 0x14;

    static void init() {
        InstructionReader.register(OPCODE, new Reader_31i_31t((A, B) -> {
            return new Const(A, B);
        }));
    }

    public final int destination_register, value;

    public Const(int A, int B) {
        destination_register = A;
        value = B;
    }

    @Override
    public String toString() {
        return "const " + destination_register + " " + value;
    }
}
