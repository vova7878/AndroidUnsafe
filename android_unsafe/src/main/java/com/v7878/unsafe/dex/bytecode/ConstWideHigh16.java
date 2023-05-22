package com.v7878.unsafe.dex.bytecode;

import com.v7878.unsafe.dex.WriteContext;
import com.v7878.unsafe.dex.bytecode.InstructionReader.Reader_21h64;
import com.v7878.unsafe.io.RandomOutput;

public class ConstWideHigh16 extends Instruction {

    public static final int OPCODE = 0x19;

    static void init() {
        InstructionReader.register(OPCODE, new Reader_21h64(ConstWideHigh16::new));
    }

    public final int destination_register;
    public final long value;

    public ConstWideHigh16(int A, long B) {
        destination_register = A;
        value = B;
    }

    @Override
    public void write(WriteContext context, RandomOutput out) {
        InstructionWriter.write_21h64(out, OPCODE,
                destination_register, value);
    }

    @Override
    public int opcode() {
        return OPCODE;
    }

    @Override
    public String name() {
        return "const-wide/high16";
    }

    @Override
    public String toString() {
        return name() + " " + destination_register + " " + value;
    }

    @Override
    public ConstWideHigh16 clone() {
        return new ConstWideHigh16(destination_register, value);
    }
}
