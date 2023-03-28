package com.v7878.unsafe.dex.bytecode;

import com.v7878.unsafe.dex.WriteContext;
import com.v7878.unsafe.dex.bytecode.InstructionReader.Reader_31i32_31t;
import com.v7878.unsafe.io.RandomOutput;

public class FillArrayData extends Instruction {

    public static final int OPCODE = 0x26;

    static void init() {
        InstructionReader.register(OPCODE, new Reader_31i32_31t((A, B) -> {
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
    public void write(WriteContext context, RandomOutput out) {
        InstructionWriter.write_31i32_31t_31c(out, OPCODE,
                array_reference, signed_branch_offset);
    }

    @Override
    public int opcode() {
        return OPCODE;
    }

    @Override
    public String name() {
        return "fill-array-data";
    }

    @Override
    public String toString() {
        return name() + " " + array_reference + " " + signed_branch_offset;
    }

    @Override
    public FillArrayData clone() {
        return new FillArrayData(array_reference, signed_branch_offset);
    }
}
