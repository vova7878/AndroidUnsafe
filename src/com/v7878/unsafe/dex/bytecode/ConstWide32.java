package com.v7878.unsafe.dex.bytecode;

import com.v7878.unsafe.dex.bytecode.InstructionReader.*;

public class ConstWide32 extends Instruction {

    public static final int OPCODE = 0x17;

    static void init() {
        InstructionReader.register(OPCODE, new Reader_31i_31t((A, B) -> {
            return new ConstWide32(A, (long) B);
        }));
    }

    public final int destination_register;
    public final long value;

    public ConstWide32(int A, long B) {
        destination_register = A;
        value = B;
    }

    @Override
    public String toString() {
        return "const-wide/32 " + destination_register + " " + value;
    }
}
