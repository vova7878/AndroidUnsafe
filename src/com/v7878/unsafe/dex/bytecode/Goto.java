package com.v7878.unsafe.dex.bytecode;

import com.v7878.unsafe.dex.WriteContext;
import com.v7878.unsafe.dex.bytecode.InstructionReader.Reader_10t;
import com.v7878.unsafe.io.RandomOutput;

public class Goto extends Instruction {

    public static final int OPCODE = 0x28;

    static void init() {
        InstructionReader.register(OPCODE, new Reader_10t((A) -> {
            return new Goto(A);
        }));
    }

    public final int signed_branch_offset;

    public Goto(int A) {
        signed_branch_offset = A;
    }

    @Override
    public void write(WriteContext context, RandomOutput out) {
        InstructionWriter.write_10t(out, OPCODE, signed_branch_offset);
    }

    @Override
    public int opcode() {
        return OPCODE;
    }

    @Override
    public String name() {
        return "goto";
    }

    @Override
    public String toString() {
        return name() + " " + signed_branch_offset;
    }

    @Override
    public Goto clone() {
        return new Goto(signed_branch_offset);
    }
}
