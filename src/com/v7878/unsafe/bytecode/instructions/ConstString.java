package com.v7878.unsafe.bytecode.instructions;

import com.v7878.unsafe.bytecode.StringId;
import com.v7878.unsafe.bytecode.instructions.InstructionReader.*;

public class ConstString extends Instruction {

    public static final int OPCODE = 0x1a;

    static void init() {
        InstructionReader.register(OPCODE, new Reader_20bc_21c((context, A, B) -> {
            return new ConstString(A, context.string(B));
        }));
    }

    public final int destination_register;
    public final StringId value;

    public ConstString(int A, StringId B) {
        destination_register = A;
        value = B;
    }

    @Override
    public String toString() {
        return "const-string " + destination_register + " \"" + value + "\"";
    }
}
