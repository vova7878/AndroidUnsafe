package com.v7878.unsafe.dex.bytecode;

import com.v7878.unsafe.dex.WriteContext;
import com.v7878.unsafe.dex.bytecode.InstructionReader.Reader_12x;
import com.v7878.unsafe.io.RandomOutput;

public class MoveWide extends Instruction {

    public static final int OPCODE = 0x04;

    static void init() {
        InstructionReader.register(OPCODE, new Reader_12x((A, B) -> {
            return new MoveWide(A, B);
        }));
    }

    public final int destination_register_pair, source_register_pair;

    public MoveWide(int A, int B) {
        destination_register_pair = A;
        source_register_pair = B;
    }

    @Override
    public void write(WriteContext context, RandomOutput out) {
        InstructionWriter.write_12x(out, OPCODE,
                destination_register_pair, source_register_pair);
    }

    @Override
    public int opcode() {
        return OPCODE;
    }

    @Override
    public String name() {
        return "move-wide";
    }

    @Override
    public String toString() {
        return name() + " " + destination_register_pair + " " + source_register_pair;
    }

    @Override
    public MoveWide clone() {
        return new MoveWide(destination_register_pair, source_register_pair);
    }
}
