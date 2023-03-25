package com.v7878.unsafe.dex.bytecode;

import com.v7878.unsafe.dex.TypeId;
import com.v7878.unsafe.dex.DataSet;
import com.v7878.unsafe.dex.bytecode.InstructionReader.*;

public class CheckCast extends Instruction {

    public static final int OPCODE = 0x1f;

    static void init() {
        InstructionReader.register(OPCODE, new Reader_20bc_21c((context, A, B) -> {
            return new CheckCast(A, context.type(B));
        }));
    }

    public final int reference_bearing_register;
    public final TypeId type;

    public CheckCast(int A, TypeId B) {
        reference_bearing_register = A;
        type = B;
    }

    @Override
    public void fillContext(DataSet data) {
        data.addType(type);
    }

    @Override
    public String toString() {
        return "check-cast " + reference_bearing_register + " " + type;
    }
}