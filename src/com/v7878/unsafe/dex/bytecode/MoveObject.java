package com.v7878.unsafe.dex.bytecode;

import com.v7878.unsafe.dex.bytecode.InstructionReader.*;

public class MoveObject extends Instruction {

    public static final int OPCODE = 0x07;

    static void init() {
        InstructionReader.register(OPCODE, new Reader_12x_11n((A, B) -> {
            return new MoveObject(A, B);
        }));
    }

    public final int destination_register, source_register;

    public MoveObject(int A, int B) {
        destination_register = A;
        source_register = B;
    }

    @Override
    public String toString() {
        return "move-object " + destination_register + " " + source_register;
    }
}
