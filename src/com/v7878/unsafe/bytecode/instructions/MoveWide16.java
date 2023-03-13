package com.v7878.unsafe.bytecode.instructions;

import com.v7878.unsafe.bytecode.instructions.InstructionReader.*;

public class MoveWide16 extends Instruction {

    public static final int OPCODE = 0x06;

    static void init() {
        InstructionReader.register(OPCODE, new Reader_32x((A, B) -> {
            return new MoveWide16(A, B);
        }));
    }

    public final int destination_register_pair, source_register_pair;

    public MoveWide16(int A, int B) {
        destination_register_pair = A;
        source_register_pair = B;
    }

    @Override
    public String toString() {
        return "move-wide/16 " + destination_register_pair + " " + source_register_pair;
    }
}
