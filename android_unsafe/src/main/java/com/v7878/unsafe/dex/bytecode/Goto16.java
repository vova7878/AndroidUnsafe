package com.v7878.unsafe.dex.bytecode;

import com.v7878.unsafe.dex.WriteContext;
import com.v7878.unsafe.dex.bytecode.InstructionReader.Reader_20t;
import com.v7878.unsafe.io.RandomOutput;

public class Goto16 extends Instruction {

    public static final int OPCODE = 0x29;

    static void init() {
        InstructionReader.register(OPCODE, new Reader_20t((A) -> {
            return new Goto16(A);
        }));
    }

    public final int signed_branch_offset;

    public Goto16(int A) {
        signed_branch_offset = A;
    }

    @Override
    public void write(WriteContext context, RandomOutput out) {
        InstructionWriter.write_20t(out, OPCODE, signed_branch_offset);
    }

    @Override
    public int opcode() {
        return OPCODE;
    }

    @Override
    public String name() {
        return "goto/16";
    }

    @Override
    public String toString() {
        return name() + " " + signed_branch_offset;
    }

    @Override
    public Goto16 clone() {
        return new Goto16(signed_branch_offset);
    }
}
