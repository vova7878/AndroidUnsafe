package com.v7878.unsafe.bytecode.instructions;

import com.v7878.unsafe.bytecode.instructions.InstructionReader.*;

public class ConstStringJumbo extends Instruction {

    public static final int OPCODE = 0x1b;

    static void init() {
        InstructionReader.register(OPCODE, new Reader_31c((context, A, B) -> {
            return new ConstStringJumbo(A, context.string(B));
        }));
    }

    public final int destination_register;
    public final String value;

    public ConstStringJumbo(int A, String B) {
        destination_register = A;
        value = B;
    }

    @Override
    public String toString() {
        return "const-string/jumbo " + destination_register + " \"" + value + "\"";
    }
}
