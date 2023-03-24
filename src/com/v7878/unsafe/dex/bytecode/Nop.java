package com.v7878.unsafe.dex.bytecode;

import com.v7878.unsafe.dex.bytecode.InstructionReader.*;

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
