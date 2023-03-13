package com.v7878.unsafe.bytecode.instructions;

import com.v7878.unsafe.bytecode.instructions.InstructionReader.*;

public class ArrayLength extends Instruction {

    public static final int OPCODE = 0x21;

    static void init() {
        InstructionReader.register(OPCODE, new Reader_12x_11n((A, B) -> {
            return new ArrayLength(A, B);
        }));
    }

    public final int destination_register;
    public final int array_reference_bearing_register;

    public ArrayLength(int A, int B) {
        destination_register = A;
        array_reference_bearing_register = B;
    }

    @Override
    public String toString() {
        return "array-length " + destination_register + " " + array_reference_bearing_register;
    }
}
