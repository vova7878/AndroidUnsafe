package com.v7878.unsafe.dex.bytecode;

import com.v7878.unsafe.dex.WriteContext;
import com.v7878.unsafe.dex.bytecode.InstructionReader.Reader_11x;
import com.v7878.unsafe.io.RandomOutput;

public class MoveResultWide extends Instruction {

    public static final int OPCODE = 0x0b;

    static void init() {
        InstructionReader.register(OPCODE, new Reader_11x(MoveResultWide::new));
    }

    public final int destination_register_pair;

    public MoveResultWide(int A) {
        destination_register_pair = A;
    }

    @Override
    public void write(WriteContext context, RandomOutput out) {
        InstructionWriter.write_11x(out, OPCODE, destination_register_pair);
    }

    @Override
    public int opcode() {
        return OPCODE;
    }

    @Override
    public String name() {
        return "move-result-wide";
    }

    @Override
    public String toString() {
        return name() + " " + destination_register_pair;
    }

    @Override
    public MoveResultWide clone() {
        return new MoveResultWide(destination_register_pair);
    }
}
