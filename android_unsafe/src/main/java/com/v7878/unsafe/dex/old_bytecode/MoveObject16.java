package com.v7878.unsafe.dex.old_bytecode;

import com.v7878.unsafe.dex.WriteContext;
import com.v7878.unsafe.dex.old_bytecode.InstructionReader.Reader_32x;
import com.v7878.unsafe.io.RandomOutput;

public class MoveObject16 extends Instruction {

    public static final int OPCODE = 0x09;

    static void init() {
        InstructionReader.register(OPCODE, new Reader_32x(MoveObject16::new));
    }

    public final int destination_register, source_register;

    public MoveObject16(int A, int B) {
        destination_register = A;
        source_register = B;
    }

    @Override
    public void write(WriteContext context, RandomOutput out) {
        InstructionWriter.write_32x(out, OPCODE,
                destination_register, source_register);
    }

    @Override
    public int opcode_old() {
        return OPCODE;
    }

    @Override
    public String name() {
        return "move-object/16";
    }

    @Override
    public String toString() {
        return name() + " " + destination_register + " " + source_register;
    }

    @Override
    public MoveObject16 clone() {
        return new MoveObject16(destination_register, source_register);
    }
}
