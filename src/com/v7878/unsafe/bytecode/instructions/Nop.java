package com.v7878.unsafe.bytecode.instructions;

import com.v7878.unsafe.bytecode.instructions.InstructionReader.*;

public class Nop extends Instruction {

    public static final int OPCODE = 0x00;

    static void init() {
        InstructionReader.register(OPCODE, new Reader_10x(() -> {
            return new Nop();
        }));
    }

    @Override
    public String toString() {
        return "nop";
    }
}
