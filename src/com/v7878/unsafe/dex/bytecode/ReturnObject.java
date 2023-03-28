package com.v7878.unsafe.dex.bytecode;

import com.v7878.unsafe.dex.WriteContext;
import com.v7878.unsafe.dex.bytecode.InstructionReader.Reader_11x;
import com.v7878.unsafe.io.RandomOutput;

public class ReturnObject extends Instruction {

    public static final int OPCODE = 0x11;

    static void init() {
        InstructionReader.register(OPCODE, new Reader_11x((A) -> {
            return new ReturnObject(A);
        }));
    }

    public final int return_value_register;

    public ReturnObject(int A) {
        return_value_register = A;
    }

    @Override
    public void write(WriteContext context, RandomOutput out) {
        InstructionWriter.write_11x(out, OPCODE, return_value_register);
    }

    @Override
    public int opcode() {
        return OPCODE;
    }

    @Override
    public String name() {
        return "return-object";
    }

    @Override
    public String toString() {
        return name() + " " + return_value_register;
    }

    @Override
    public ReturnObject clone() {
        return new ReturnObject(return_value_register);
    }
}
