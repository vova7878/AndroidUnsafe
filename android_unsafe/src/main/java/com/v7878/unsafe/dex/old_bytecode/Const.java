package com.v7878.unsafe.dex.old_bytecode;

import com.v7878.unsafe.dex.WriteContext;
import com.v7878.unsafe.dex.old_bytecode.InstructionReader.Reader_31i32_31t;
import com.v7878.unsafe.io.RandomOutput;

public class Const extends Instruction {

    public static final int OPCODE = 0x14;

    static void init() {
        InstructionReader.register(OPCODE, new Reader_31i32_31t(Const::new));
    }

    public final int destination_register, value;

    public Const(int A, int B) {
        destination_register = A;
        value = B;
    }

    @Override
    public void write(WriteContext context, RandomOutput out) {
        InstructionWriter.write_31i32_31t_31c(out, OPCODE,
                destination_register, value);
    }

    @Override
    public int opcode_old() {
        return OPCODE;
    }

    @Override
    public String name() {
        return "const";
    }

    @Override
    public String toString() {
        return name() + " " + destination_register + " " + value;
    }

    @Override
    public Const clone() {
        return new Const(destination_register, value);
    }
}
