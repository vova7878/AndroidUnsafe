package com.v7878.unsafe.bytecode.instructions;

import com.v7878.unsafe.bytecode.instructions.InstructionReader.*;

public class Goto16 extends Instruction {

    public static final int OPCODE = 0x29;

    static void init() {
        InstructionReader.register(OPCODE, new Reader_20t((A) -> {
            return new Goto16((A << 16) >> 16);
        }));
    }

    public final int signed_branch_offset;

    public Goto16(int A) {
        signed_branch_offset = A;
    }

    @Override
    public String toString() {
        return "goto/16 " + signed_branch_offset;
    }
}
