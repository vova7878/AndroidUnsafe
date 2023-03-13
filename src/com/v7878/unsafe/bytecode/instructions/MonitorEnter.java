package com.v7878.unsafe.bytecode.instructions;

import com.v7878.unsafe.bytecode.instructions.InstructionReader.*;

public class MonitorEnter extends Instruction {

    public static final int OPCODE = 0x1d;

    static void init() {
        InstructionReader.register(OPCODE, new Reader_11x_10t((A) -> {
            return new MonitorEnter(A);
        }));
    }

    public final int reference_bearing_register;

    public MonitorEnter(int A) {
        reference_bearing_register = A;
    }

    @Override
    public String toString() {
        return "monitor-enter " + reference_bearing_register;
    }
}
