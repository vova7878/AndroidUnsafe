package com.v7878.unsafe.dex.old_bytecode;

import com.v7878.unsafe.dex.WriteContext;
import com.v7878.unsafe.dex.old_bytecode.InstructionReader.Reader_11n;
import com.v7878.unsafe.io.RandomOutput;

public class Const4 extends Instruction {

    public static final int OPCODE = 0x12;

    static void init() {
        InstructionReader.register(OPCODE, new Reader_11n(Const4::new));
    }

    public final int destination_register, value;

    public Const4(int A, int B) {
        destination_register = A;
        value = B;
    }

    @Override
    public void write(WriteContext context, RandomOutput out) {
        InstructionWriter.write_11n(out, OPCODE,
                destination_register, value);
    }

    @Override
    public int opcode_old() {
        return OPCODE;
    }

    @Override
    public String name() {
        return "const/4";
    }

    @Override
    public String toString() {
        return name() + " " + destination_register + " " + value;
    }

    @Override
    public Const4 clone() {
        return new Const4(destination_register, value);
    }
}
