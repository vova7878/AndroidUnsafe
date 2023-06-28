package com.v7878.unsafe.dex.old_bytecode;

import com.v7878.unsafe.dex.WriteContext;
import com.v7878.unsafe.dex.old_bytecode.InstructionReader.Reader_11x;
import com.v7878.unsafe.io.RandomOutput;

public class MoveResultObject extends Instruction {

    public static final int OPCODE = 0x0c;

    static void init() {
        InstructionReader.register(OPCODE, new Reader_11x(MoveResultObject::new));
    }

    public final int destination_register;

    public MoveResultObject(int A) {
        destination_register = A;
    }

    @Override
    public void write(WriteContext context, RandomOutput out) {
        InstructionWriter.write_11x(out, OPCODE, destination_register);
    }

    @Override
    public int opcode_old() {
        return OPCODE;
    }

    @Override
    public String name() {
        return "move-result-object";
    }

    @Override
    public String toString() {
        return name() + " " + destination_register;
    }

    @Override
    public MoveResultObject clone() {
        return new MoveResultObject(destination_register);
    }
}
