package com.v7878.unsafe.dex.bytecode;

import com.v7878.unsafe.dex.bytecode.InstructionReader.*;

public class ReturnVoid extends Instruction {

    public static final int OPCODE = 0x0e;

    static void init() {
        InstructionReader.register(OPCODE, new Reader_10x(() -> {
            return new ReturnVoid();
        }));
    }

    @Override
    public String toString() {
        return "return-void";
    }
}
