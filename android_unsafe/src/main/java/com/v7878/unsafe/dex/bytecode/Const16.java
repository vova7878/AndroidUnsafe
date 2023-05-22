package com.v7878.unsafe.dex.bytecode;

import com.v7878.unsafe.dex.WriteContext;
import com.v7878.unsafe.dex.bytecode.InstructionReader.Reader_21t_21s32;
import com.v7878.unsafe.io.RandomOutput;

public class Const16 extends Instruction {

    public static final int OPCODE = 0x13;

    static void init() {
        InstructionReader.register(OPCODE, new Reader_21t_21s32(Const16::new));
    }

    public final int destination_register, value;

    public Const16(int A, int B) {
        destination_register = A;
        value = B;
    }

    @Override
    public void write(WriteContext context, RandomOutput out) {
        InstructionWriter.write_21t_21s32(out, OPCODE,
                destination_register, value);
    }

    @Override
    public int opcode() {
        return OPCODE;
    }

    @Override
    public String name() {
        return "const/16";
    }

    @Override
    public String toString() {
        return name() + " " + destination_register + " " + value;
    }

    @Override
    public Const16 clone() {
        return new Const16(destination_register, value);
    }
}
