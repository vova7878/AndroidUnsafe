package com.v7878.unsafe.bytecode.instructions;

import com.v7878.unsafe.bytecode.instructions.InstructionReader.*;

public class MoveObject16 extends Instruction {

    public static final int OPCODE = 0x09;

    static void init() {
        InstructionReader.register(OPCODE, new Reader_32x((A, B) -> {
            return new MoveObject16(A, B);
        }));
    }

    public final int destination_register, source_register;

    public MoveObject16(int A, int B) {
        destination_register = A;
        source_register = B;
    }

    @Override
    public String toString() {
        return "move-object/16 " + destination_register + " " + source_register;
    }
}
