package com.v7878.unsafe.dex.old_bytecode;

import com.v7878.unsafe.dex.WriteContext;
import com.v7878.unsafe.dex.old_bytecode.InstructionReader.Reader_11x;
import com.v7878.unsafe.io.RandomOutput;

public class Throw extends Instruction {

    public static final int OPCODE = 0x27;

    static void init() {
        InstructionReader.register(OPCODE, new Reader_11x(Throw::new));
    }

    public final int exception_bearing_register;

    public Throw(int A) {
        exception_bearing_register = A;
    }

    @Override
    public void write(WriteContext context, RandomOutput out) {
        InstructionWriter.write_11x(out, OPCODE, exception_bearing_register);
    }

    @Override
    public int opcode_old() {
        return OPCODE;
    }

    @Override
    public String name() {
        return "throw";
    }

    @Override
    public String toString() {
        return name() + " " + exception_bearing_register;
    }

    @Override
    public Throw clone() {
        return new Throw(exception_bearing_register);
    }
}
