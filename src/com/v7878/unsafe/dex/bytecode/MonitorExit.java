package com.v7878.unsafe.dex.bytecode;

import com.v7878.unsafe.dex.WriteContext;
import com.v7878.unsafe.dex.bytecode.InstructionReader.Reader_11x;
import com.v7878.unsafe.io.RandomOutput;

public class MonitorExit extends Instruction {

    public static final int OPCODE = 0x1e;

    static void init() {
        InstructionReader.register(OPCODE, new Reader_11x((A) -> {
            return new MonitorExit(A);
        }));
    }

    public final int reference_bearing_register;

    public MonitorExit(int A) {
        reference_bearing_register = A;
    }

    @Override
    public void write(WriteContext context, RandomOutput out) {
        InstructionWriter.write_11x(out, OPCODE, reference_bearing_register);
    }

    @Override
    public int opcode() {
        return OPCODE;
    }

    @Override
    public String name() {
        return "monitor-exit";
    }

    @Override
    public String toString() {
        return name() + " " + reference_bearing_register;
    }

    @Override
    public MonitorExit clone() {
        return new MonitorExit(reference_bearing_register);
    }
}
