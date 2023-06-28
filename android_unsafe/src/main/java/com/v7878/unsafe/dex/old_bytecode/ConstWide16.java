package com.v7878.unsafe.dex.old_bytecode;

import com.v7878.unsafe.dex.WriteContext;
import com.v7878.unsafe.dex.old_bytecode.InstructionReader.Reader_21s64;
import com.v7878.unsafe.io.RandomOutput;

public class ConstWide16 extends Instruction {

    public static final int OPCODE = 0x16;

    static void init() {
        InstructionReader.register(OPCODE, new Reader_21s64(ConstWide16::new));
    }

    public final int destination_register;
    public final long value;

    public ConstWide16(int A, long B) {
        destination_register = A;
        value = B;
    }

    @Override
    public void write(WriteContext context, RandomOutput out) {
        InstructionWriter.write_21s64(out, OPCODE,
                destination_register, value);
    }

    @Override
    public int opcode_old() {
        return OPCODE;
    }

    @Override
    public String name() {
        return "const-wide/16";
    }

    @Override
    public String toString() {
        return name() + " " + destination_register + " " + value;
    }

    @Override
    public ConstWide16 clone() {
        return new ConstWide16(destination_register, value);
    }
}
