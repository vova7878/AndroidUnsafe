package com.v7878.unsafe.dex.bytecode;

import com.v7878.unsafe.dex.bytecode.InstructionReader.*;

public class FillArrayData extends Instruction {

    public static final int OPCODE = 0x26;

    static void init() {
        InstructionReader.register(OPCODE, new Reader_31i_31t((A, B) -> {
            return new FillArrayData(A, B);
        }));
    }

    public final int array_reference;
    public final int signed_branch_offset;

    public FillArrayData(int A, int B) {
        array_reference = A;
        signed_branch_offset = B;
    }

    @Override
    public String toString() {
        return "fill-array-data " + signed_branch_offset;
    }
}
