package com.v7878.unsafe.dex.bytecode;

import com.v7878.unsafe.dex.bytecode.InstructionReader.*;

public class Goto32 extends Instruction {

    public static final int OPCODE = 0x2a;

    static void init() {
        InstructionReader.register(OPCODE, new Reader_30t((A) -> {
            return new Goto32(A);
        }));
    }

    public final int signed_branch_offset;

    public Goto32(int A) {
        signed_branch_offset = A;
    }

    @Override
    public String toString() {
        return "goto/32 " + signed_branch_offset;
    }
}
