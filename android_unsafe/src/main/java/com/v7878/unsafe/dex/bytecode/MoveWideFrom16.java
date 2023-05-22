package com.v7878.unsafe.dex.bytecode;

import com.v7878.unsafe.dex.WriteContext;
import com.v7878.unsafe.dex.bytecode.InstructionReader.Reader_22x;
import com.v7878.unsafe.io.RandomOutput;

public class MoveWideFrom16 extends Instruction {

    public static final int OPCODE = 0x05;

    static void init() {
        InstructionReader.register(OPCODE, new Reader_22x(MoveWideFrom16::new));
    }

    public final int destination_register_pair, source_register_pair;

    public MoveWideFrom16(int A, int B) {
        destination_register_pair = A;
        source_register_pair = B;
    }

    @Override
    public void write(WriteContext context, RandomOutput out) {
        InstructionWriter.write_22x_20bc_21c(out, OPCODE,
                destination_register_pair, source_register_pair);
    }

    @Override
    public int opcode() {
        return OPCODE;
    }

    @Override
    public String name() {
        return "move-wide/from16";
    }

    @Override
    public String toString() {
        return name() + " " + destination_register_pair + " " + source_register_pair;
    }

    @Override
    public MoveWideFrom16 clone() {
        return new MoveWideFrom16(destination_register_pair, source_register_pair);
    }
}
