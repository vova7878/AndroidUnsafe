package com.v7878.unsafe.dex.bytecode;

import com.v7878.unsafe.dex.TypeId;
import com.v7878.unsafe.dex.DataSet;
import com.v7878.unsafe.dex.bytecode.InstructionReader.*;

public class InstanceOf extends Instruction {

    public static final int OPCODE = 0x20;

    static void init() {
        InstructionReader.register(OPCODE, new Reader_22c_22cs((context, A, B, C) -> {
            return new InstanceOf(A, B, context.type(C));
        }));
    }

    public final int destination_register;
    public final int reference_bearing_register;
    public final TypeId type;

    public InstanceOf(int A, int B, TypeId C) {
        destination_register = A;
        reference_bearing_register = B;
        type = C;
    }

    @Override
    public void fillContext(DataSet data) {
        data.addType(type);
    }

    @Override
    public String toString() {
        return "instance-of " + destination_register + " " + reference_bearing_register + " " + type;
    }
}
