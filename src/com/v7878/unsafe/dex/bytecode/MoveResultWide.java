package com.v7878.unsafe.dex.bytecode;

import com.v7878.unsafe.dex.bytecode.InstructionReader.*;

public class MoveResultWide extends Instruction {

    public static final int OPCODE = 0x0b;

    static void init() {
        InstructionReader.register(OPCODE, new Reader_11x_10t((A) -> {
            return new MoveResultWide(A);
        }));
    }

    public final int destination_register_pair;

    public MoveResultWide(int A) {
        destination_register_pair = A;
    }

    @Override
    public String toString() {
        return "move-result-wide " + destination_register_pair;
    }
}
