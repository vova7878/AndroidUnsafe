package com.v7878.unsafe.dex.bytecode;

import com.v7878.unsafe.dex.WriteContext;
import com.v7878.unsafe.dex.bytecode.InstructionReader.Reader_12x;
import com.v7878.unsafe.io.RandomOutput;

public class MoveObject extends Instruction {

    public static final int OPCODE = 0x07;

    static void init() {
        InstructionReader.register(OPCODE, new Reader_12x(MoveObject::new));
    }

    public final int destination_register, source_register;

    public MoveObject(int A, int B) {
        destination_register = A;
        source_register = B;
    }

    @Override
    public void write(WriteContext context, RandomOutput out) {
        InstructionWriter.write_12x(out, OPCODE,
                destination_register, source_register);
    }

    @Override
    public int opcode() {
        return OPCODE;
    }

    @Override
    public String name() {
        return "move-object";
    }

    @Override
    public String toString() {
        return name() + " " + destination_register + " " + source_register;
    }

    @Override
    public MoveObject clone() {
        return new MoveObject(destination_register, source_register);
    }
}
