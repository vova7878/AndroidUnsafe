package com.v7878.unsafe.bytecode.instructions;

import com.v7878.unsafe.bytecode.TypeId;
import com.v7878.unsafe.bytecode.instructions.InstructionReader.*;

public class NewInstance extends Instruction {

    public static final int OPCODE = 0x22;

    static void init() {
        InstructionReader.register(OPCODE, new Reader_20bc_21c((rc, A, B) -> {
            return new NewInstance(A, rc.types[B]);
        }));
    }

    public final int destination_register;
    public final TypeId type;

    public NewInstance(int A, TypeId B) {
        destination_register = A;
        type = B;
    }

    @Override
    public String toString() {
        return "new-instance " + destination_register + " " + type;
    }
}
