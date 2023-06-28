package com.v7878.unsafe.dex.old_bytecode;

import com.v7878.unsafe.dex.WriteContext;
import com.v7878.unsafe.dex.old_bytecode.InstructionReader.Reader_12x;
import com.v7878.unsafe.io.RandomOutput;

public class Move extends Instruction {

    public static final int OPCODE = 0x01;

    static void init() {
        InstructionReader.register(OPCODE, new Reader_12x(Move::new));
    }

    public final int destination_register, source_register;

    public Move(int A, int B) {
        destination_register = A;
        source_register = B;
    }

    @Override
    public void write(WriteContext context, RandomOutput out) {
        InstructionWriter.write_12x(out, OPCODE,
                destination_register, source_register);
    }

    @Override
    public int opcode_old() {
        return OPCODE;
    }

    @Override
    public String name() {
        return "move";
    }

    @Override
    public String toString() {
        return name() + " " + destination_register + " " + source_register;
    }

    @Override
    public Move clone() {
        return new Move(destination_register, source_register);
    }
}
