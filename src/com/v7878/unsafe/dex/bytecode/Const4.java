package com.v7878.unsafe.dex.bytecode;

import com.v7878.unsafe.dex.bytecode.InstructionReader.*;

public class Const4 extends Instruction {

    public static final int OPCODE = 0x12;

    static void init() {
        InstructionReader.register(OPCODE, new Reader_12x_11n((A, B) -> {
            return new Const4(A, (B << 28) >> 28);
        }));
    }

    public final int destination_register, value;

    public Const4(int A, int B) {
        destination_register = A;
        value = B;
    }

    @Override
    public String toString() {
        return "const/4 " + destination_register + " " + value;
    }
}
