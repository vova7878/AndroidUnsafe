package com.v7878.unsafe.dex.bytecode;

import com.v7878.unsafe.dex.bytecode.InstructionReader.*;

public class MoveWide extends Instruction {

    public static final int OPCODE = 0x04;

    static void init() {
        InstructionReader.register(OPCODE, new Reader_12x_11n((A, B) -> {
            return new MoveWide(A, B);
        }));
    }

    public final int destination_register_pair, source_register_pair;

    public MoveWide(int A, int B) {
        destination_register_pair = A;
        source_register_pair = B;
    }

    @Override
    public String toString() {
        return "move-wide " + destination_register_pair + " " + source_register_pair;
    }
}
