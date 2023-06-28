package com.v7878.unsafe.dex.old_bytecode;

import com.v7878.unsafe.dex.WriteContext;
import com.v7878.unsafe.dex.old_bytecode.InstructionReader.Reader_21h32;
import com.v7878.unsafe.io.RandomOutput;

public class ConstHigh16 extends Instruction {

    public static final int OPCODE = 0x15;

    static void init() {
        InstructionReader.register(OPCODE, new Reader_21h32(ConstHigh16::new));
    }

    public final int destination_register, value;

    public ConstHigh16(int A, int B) {
        destination_register = A;
        value = B;
    }

    @Override
    public void write(WriteContext context, RandomOutput out) {
        InstructionWriter.write_21h32(out, OPCODE,
                destination_register, value);
    }

    @Override
    public int opcode_old() {
        return OPCODE;
    }

    @Override
    public String name() {
        return "const/high16";
    }

    @Override
    public String toString() {
        return name() + " " + destination_register + " " + value;
    }

    @Override
    public ConstHigh16 clone() {
        return new ConstHigh16(destination_register, value);
    }
}
