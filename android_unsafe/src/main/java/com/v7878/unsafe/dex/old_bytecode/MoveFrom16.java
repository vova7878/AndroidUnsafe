package com.v7878.unsafe.dex.old_bytecode;

import com.v7878.unsafe.dex.WriteContext;
import com.v7878.unsafe.dex.old_bytecode.InstructionReader.Reader_22x;
import com.v7878.unsafe.io.RandomOutput;

public class MoveFrom16 extends Instruction {

    public static final int OPCODE = 0x02;

    static void init() {
        InstructionReader.register(OPCODE, new Reader_22x(MoveFrom16::new));
    }

    public final int destination_register, source_register;

    public MoveFrom16(int A, int B) {
        destination_register = A;
        source_register = B;
    }

    @Override
    public void write(WriteContext context, RandomOutput out) {
        InstructionWriter.write_22x_20bc_21c(out, OPCODE,
                destination_register, source_register);
    }

    @Override
    public int opcode_old() {
        return OPCODE;
    }

    @Override
    public String name() {
        return "move/from16";
    }

    @Override
    public String toString() {
        return name() + " " + destination_register + " " + source_register;
    }

    @Override
    public MoveFrom16 clone() {
        return new MoveFrom16(destination_register, source_register);
    }
}
