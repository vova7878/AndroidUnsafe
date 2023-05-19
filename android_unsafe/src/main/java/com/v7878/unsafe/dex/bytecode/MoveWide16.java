package com.v7878.unsafe.dex.bytecode;

import com.v7878.unsafe.dex.WriteContext;
import com.v7878.unsafe.dex.bytecode.InstructionReader.Reader_32x;
import com.v7878.unsafe.io.RandomOutput;

public class MoveWide16 extends Instruction {

    public static final int OPCODE = 0x06;

    static void init() {
        InstructionReader.register(OPCODE, new Reader_32x((A, B) -> {
            return new MoveWide16(A, B);
        }));
    }

    public final int destination_register_pair, source_register_pair;

    public MoveWide16(int A, int B) {
        destination_register_pair = A;
        source_register_pair = B;
    }

    @Override
    public void write(WriteContext context, RandomOutput out) {
        InstructionWriter.write_32x(out, OPCODE,
                destination_register_pair, source_register_pair);
    }

    @Override
    public int opcode() {
        return OPCODE;
    }

    @Override
    public String name() {
        return "move-wide/16";
    }

    @Override
    public String toString() {
        return name() + " " + destination_register_pair + " " + source_register_pair;
    }

    @Override
    public MoveWide16 clone() {
        return new MoveWide16(destination_register_pair, source_register_pair);
    }
}
