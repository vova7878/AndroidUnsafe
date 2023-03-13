package com.v7878.unsafe.bytecode.instructions;

import com.v7878.unsafe.bytecode.instructions.InstructionReader.*;

public class MoveObjectFrom16 extends Instruction {

    public static final int OPCODE = 0x08;

    static void init() {
        InstructionReader.register(OPCODE, new Reader_22x_21t_21s_21h((A, B) -> {
            return new MoveObjectFrom16(A, B);
        }));
    }

    public final int destination_register, source_register;

    public MoveObjectFrom16(int A, int B) {
        destination_register = A;
        source_register = B;
    }

    @Override
    public String toString() {
        return "move-object/from16 " + destination_register + " " + source_register;
    }
}
