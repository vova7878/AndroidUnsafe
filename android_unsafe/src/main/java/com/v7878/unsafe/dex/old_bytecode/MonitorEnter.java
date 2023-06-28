package com.v7878.unsafe.dex.old_bytecode;

import com.v7878.unsafe.dex.WriteContext;
import com.v7878.unsafe.dex.old_bytecode.InstructionReader.Reader_11x;
import com.v7878.unsafe.io.RandomOutput;

public class MonitorEnter extends Instruction {

    public static final int OPCODE = 0x1d;

    static void init() {
        InstructionReader.register(OPCODE, new Reader_11x(MonitorEnter::new));
    }

    public final int reference_bearing_register;

    public MonitorEnter(int A) {
        reference_bearing_register = A;
    }

    @Override
    public void write(WriteContext context, RandomOutput out) {
        InstructionWriter.write_11x(out, OPCODE, reference_bearing_register);
    }

    @Override
    public int opcode_old() {
        return OPCODE;
    }

    @Override
    public String name() {
        return "monitor-enter";
    }

    @Override
    public String toString() {
        return name() + " " + reference_bearing_register;
    }

    @Override
    public MonitorEnter clone() {
        return new MonitorEnter(reference_bearing_register);
    }
}
