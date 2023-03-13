package com.v7878.unsafe.bytecode.instructions;

import com.v7878.unsafe.bytecode.instructions.InstructionReader.*;

public class MonitorExit extends Instruction {

    public static final int OPCODE = 0x1e;

    static void init() {
        InstructionReader.register(OPCODE, new Reader_11x_10t((A) -> {
            return new MonitorExit(A);
        }));
    }

    public final int reference_bearing_register;

    public MonitorExit(int A) {
        reference_bearing_register = A;
    }

    @Override
    public String toString() {
        return "monitor-exit " + reference_bearing_register;
    }
}
