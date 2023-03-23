package com.v7878.unsafe.dex.bytecode;

import com.v7878.unsafe.dex.bytecode.InstructionReader.*;

public class MoveResult extends Instruction {

    public static final int OPCODE = 0x0a;

    static void init() {
        InstructionReader.register(OPCODE, new Reader_11x_10t((A) -> {
            return new MoveResult(A);
        }));
    }

    public final int destination_register;

    public MoveResult(int A) {
        destination_register = A;
    }

    @Override
    public String toString() {
        return "move-result " + destination_register;
    }
}
