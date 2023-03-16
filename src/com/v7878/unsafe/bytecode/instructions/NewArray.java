package com.v7878.unsafe.bytecode.instructions;

import com.v7878.unsafe.bytecode.TypeId;
import com.v7878.unsafe.bytecode.instructions.InstructionReader.*;

public class NewArray extends Instruction {

    public static final int OPCODE = 0x23;

    static void init() {
        InstructionReader.register(OPCODE, new Reader_22c_22cs((context, A, B, C) -> {
            return new NewArray(A, B, context.type(C));
        }));
    }

    public final int destination_register;
    public final int size_register;
    public final TypeId type;

    public NewArray(int A, int B, TypeId C) {
        destination_register = A;
        size_register = B;
        type = C;
    }

    @Override
    public String toString() {
        return "new-array " + destination_register + " " + size_register + " " + type;
    }
}
