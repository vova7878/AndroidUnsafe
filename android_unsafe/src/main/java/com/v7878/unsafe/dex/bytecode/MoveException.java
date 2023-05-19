package com.v7878.unsafe.dex.bytecode;

import com.v7878.unsafe.dex.WriteContext;
import com.v7878.unsafe.dex.bytecode.InstructionReader.Reader_11x;
import com.v7878.unsafe.io.RandomOutput;

public class MoveException extends Instruction {

    public static final int OPCODE = 0x0d;

    static void init() {
        InstructionReader.register(OPCODE, new Reader_11x((A) -> {
            return new MoveException(A);
        }));
    }

    public final int destination_register;

    public MoveException(int A) {
        destination_register = A;
    }

    @Override
    public void write(WriteContext context, RandomOutput out) {
        InstructionWriter.write_11x(out, OPCODE, destination_register);
    }

    @Override
    public int opcode() {
        return OPCODE;
    }

    @Override
    public String name() {
        return "move-exception";
    }

    @Override
    public String toString() {
        return name() + " " + destination_register;
    }

    @Override
    public MoveException clone() {
        return new MoveException(destination_register);
    }
}
