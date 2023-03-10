package com.v7878.unsafe.bytecode.instructions;

import com.v7878.unsafe.bytecode.StringId;
import com.v7878.unsafe.bytecode.instructions.InstructionReader.*;

public class ConstStringJumbo extends Instruction {

    public static final int OPCODE = 0x1b;

    static void init() {
        InstructionReader.register(OPCODE, new Reader_31c((rc, A, B) -> {
            return new ConstStringJumbo(A, rc.strings[B]);
        }));
    }

    public final int destination_register;
    public final StringId value;

    public ConstStringJumbo(int A, StringId B) {
        destination_register = A;
        value = B;
    }

    @Override
    public String toString() {
        return "const-string/jumbo " + destination_register + " \"" + value + "\"";
    }
}
