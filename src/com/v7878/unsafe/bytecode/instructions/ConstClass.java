package com.v7878.unsafe.bytecode.instructions;

import com.v7878.unsafe.bytecode.TypeId;
import com.v7878.unsafe.bytecode.instructions.InstructionReader.Reader_20bc_21c;

public class ConstClass extends Instruction {

    public static final int OPCODE = 0x1c;

    static void init() {
        InstructionReader.register(OPCODE, new Reader_20bc_21c((context, A, B) -> {
            return new ConstClass(A, context.type(B));
        }));
    }

    public final int destination_register;
    public final TypeId value;

    public ConstClass(int A, TypeId B) {
        destination_register = A;
        value = B;
    }

    @Override
    public String toString() {
        return "const-class " + destination_register + " \"" + value + "\"";
    }
}
