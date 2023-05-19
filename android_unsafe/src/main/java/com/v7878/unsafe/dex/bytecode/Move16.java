package com.v7878.unsafe.dex.bytecode;

import com.v7878.unsafe.dex.WriteContext;
import com.v7878.unsafe.dex.bytecode.InstructionReader.Reader_32x;
import com.v7878.unsafe.io.RandomOutput;

public class Move16 extends Instruction {

    public static final int OPCODE = 0x03;

    static void init() {
        InstructionReader.register(OPCODE, new Reader_32x((A, B) -> {
            return new Move16(A, B);
        }));
    }

    public final int destination_register, source_register;

    public Move16(int A, int B) {
        destination_register = A;
        source_register = B;
    }

    @Override
    public void write(WriteContext context, RandomOutput out) {
        InstructionWriter.write_32x(out, OPCODE,
                destination_register, source_register);
    }

    @Override
    public int opcode() {
        return OPCODE;
    }

    @Override
    public String name() {
        return "move/16";
    }

    @Override
    public String toString() {
        return name() + " " + destination_register + " " + source_register;
    }

    @Override
    public Move16 clone() {
        return new Move16(destination_register, source_register);
    }
}
