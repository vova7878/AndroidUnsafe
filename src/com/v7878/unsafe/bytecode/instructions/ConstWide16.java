package com.v7878.unsafe.bytecode.instructions;

import com.v7878.unsafe.bytecode.instructions.InstructionReader.*;

public class ConstWide16 extends Instruction {

    public static final int OPCODE = 0x16;

    static void init() {
        InstructionReader.register(OPCODE, new Reader_22x_21t_21s_21h((A, B) -> {
            return new ConstWide16(A, ((long) B << 48) >> 48);
        }));
    }

    public final int destination_register;
    public final long value;

    public ConstWide16(int A, long B) {
        destination_register = A;
        value = B;
    }

    @Override
    public String toString() {
        return "const-wide/16 " + destination_register + " " + value;
    }
}
