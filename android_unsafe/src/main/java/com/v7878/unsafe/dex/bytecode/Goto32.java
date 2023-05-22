package com.v7878.unsafe.dex.bytecode;

import com.v7878.unsafe.dex.WriteContext;
import com.v7878.unsafe.dex.bytecode.InstructionReader.Reader_30t;
import com.v7878.unsafe.io.RandomOutput;

public class Goto32 extends Instruction {

    public static final int OPCODE = 0x2a;

    static void init() {
        InstructionReader.register(OPCODE, new Reader_30t(Goto32::new));
    }

    public final int signed_branch_offset;

    public Goto32(int A) {
        signed_branch_offset = A;
    }

    @Override
    public void write(WriteContext context, RandomOutput out) {
        InstructionWriter.write_30t(out, OPCODE, signed_branch_offset);
    }

    @Override
    public int opcode() {
        return OPCODE;
    }

    @Override
    public String name() {
        return "goto/32";
    }

    @Override
    public String toString() {
        return name() + " " + signed_branch_offset;
    }

    @Override
    public Goto32 clone() {
        return new Goto32(signed_branch_offset);
    }
}
