package com.v7878.unsafe.dex.bytecode;

import com.v7878.unsafe.dex.WriteContext;
import com.v7878.unsafe.dex.bytecode.InstructionReader.Reader_22x;
import com.v7878.unsafe.io.RandomOutput;

public class MoveObjectFrom16 extends Instruction {

    public static final int OPCODE = 0x08;

    static void init() {
        InstructionReader.register(OPCODE, new Reader_22x(MoveObjectFrom16::new));
    }

    public final int destination_register, source_register;

    public MoveObjectFrom16(int A, int B) {
        destination_register = A;
        source_register = B;
    }

    @Override
    public void write(WriteContext context, RandomOutput out) {
        InstructionWriter.write_22x_20bc_21c(out, OPCODE,
                destination_register, source_register);
    }

    @Override
    public int opcode() {
        return OPCODE;
    }

    @Override
    public String name() {
        return "move-object/from16";
    }

    @Override
    public String toString() {
        return name() + " " + destination_register + " " + source_register;
    }

    @Override
    public MoveObjectFrom16 clone() {
        return new MoveObjectFrom16(destination_register, source_register);
    }
}
