package com.v7878.unsafe.dex.old_bytecode;

import com.v7878.unsafe.dex.WriteContext;
import com.v7878.unsafe.dex.old_bytecode.InstructionReader.Reader_12x;
import com.v7878.unsafe.io.RandomOutput;

public class ArrayLength extends Instruction {

    public static final int OPCODE = 0x21;

    static void init() {
        InstructionReader.register(OPCODE, new Reader_12x(ArrayLength::new));
    }

    public final int destination_register;
    public final int array_reference_bearing_register;

    public ArrayLength(int A, int B) {
        destination_register = A;
        array_reference_bearing_register = B;
    }

    @Override
    public void write(WriteContext context, RandomOutput out) {
        InstructionWriter.write_12x(out, OPCODE,
                destination_register, array_reference_bearing_register);
    }

    @Override
    public int opcode_old() {
        return OPCODE;
    }

    @Override
    public String name() {
        return "array-length";
    }

    @Override
    public String toString() {
        return name() + " " + destination_register
                + " " + array_reference_bearing_register;
    }

    @Override
    public ArrayLength clone() {
        return new ArrayLength(destination_register,
                array_reference_bearing_register);
    }
}
