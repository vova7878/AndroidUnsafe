package com.v7878.unsafe.dex.bytecode;

import com.v7878.unsafe.dex.bytecode.InstructionReader.*;

public class ConstHigh16 extends Instruction {

    public static final int OPCODE = 0x15;

    static void init() {
        InstructionReader.register(OPCODE, new Reader_22x_21t_21s_21h((A, B) -> {
            return new ConstHigh16(A, B << 16);
        }));
    }

    public final int destination_register, value;

    public ConstHigh16(int A, int B) {
        destination_register = A;
        value = B;
    }

    @Override
    public String toString() {
        return "const/high16 " + destination_register + " " + value;
    }
}
