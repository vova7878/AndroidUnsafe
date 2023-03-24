package com.v7878.unsafe.dex.bytecode;

import com.v7878.unsafe.dex.bytecode.InstructionReader.*;

public class MoveFrom16 extends Instruction {

    public static final int OPCODE = 0x02;

    static void init() {
        InstructionReader.register(OPCODE, new Reader_22x_21t_21s_21h((A, B) -> {
            return new MoveFrom16(A, B);
        }));
    }

    public final int destination_register, source_register;

    public MoveFrom16(int A, int B) {
        destination_register = A;
        source_register = B;
    }

    @Override
    public String toString() {
        return "move/from16 " + destination_register + " " + source_register;
    }
}
