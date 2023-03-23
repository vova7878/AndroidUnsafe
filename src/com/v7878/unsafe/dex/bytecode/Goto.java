package com.v7878.unsafe.dex.bytecode;

import com.v7878.unsafe.dex.bytecode.InstructionReader.*;

public class Goto extends Instruction {

    public static final int OPCODE = 0x28;

    static void init() {
        InstructionReader.register(OPCODE, new Reader_11x_10t((A) -> {
            return new Goto((A << 24) >> 24);
        }));
    }

    public final int signed_branch_offset;

    public Goto(int A) {
        signed_branch_offset = A;
    }

    @Override
    public String toString() {
        return "goto " + signed_branch_offset;
    }
}
