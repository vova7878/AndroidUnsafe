package com.v7878.unsafe.dex.bytecode;

import com.v7878.unsafe.dex.bytecode.InstructionReader.*;

public class MoveResultObject extends Instruction {

    public static final int OPCODE = 0x0c;

    static void init() {
        InstructionReader.register(OPCODE, new Reader_11x_10t((A) -> {
            return new MoveResultObject(A);
        }));
    }

    public final int destination_register;

    public MoveResultObject(int A) {
        destination_register = A;
    }

    @Override
    public String toString() {
        return "move-result-object " + destination_register;
    }
}
