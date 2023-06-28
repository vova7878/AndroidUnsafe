package com.v7878.unsafe.dex.old_bytecode;

import com.v7878.unsafe.dex.WriteContext;
import com.v7878.unsafe.dex.old_bytecode.InstructionReader.Reader_31i64;
import com.v7878.unsafe.io.RandomOutput;

public class ConstWide32 extends Instruction {

    public static final int OPCODE = 0x17;

    static void init() {
        InstructionReader.register(OPCODE, new Reader_31i64(ConstWide32::new));
    }

    public final int destination_register;
    public final long value;

    public ConstWide32(int A, long B) {
        destination_register = A;
        value = B;
    }

    @Override
    public void write(WriteContext context, RandomOutput out) {
        InstructionWriter.write_31i64(out, OPCODE,
                destination_register, value);
    }

    @Override
    public int opcode_old() {
        return OPCODE;
    }

    @Override
    public String name() {
        return "const-wide/32";
    }

    @Override
    public String toString() {
        return name() + " " + destination_register + " " + value;
    }

    @Override
    public ConstWide32 clone() {
        return new ConstWide32(destination_register, value);
    }
}
