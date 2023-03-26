package com.v7878.unsafe.dex.bytecode;

import com.v7878.unsafe.dex.TypeId;
import com.v7878.unsafe.dex.bytecode.InstructionReader.*;
import com.v7878.unsafe.dex.DataCollector;

public class NewInstance extends Instruction {

    public static final int OPCODE = 0x22;

    static void init() {
        InstructionReader.register(OPCODE, new Reader_20bc_21c((context, A, B) -> {
            return new NewInstance(A, context.type(B));
        }));
    }

    public final int destination_register;
    public final TypeId type;

    public NewInstance(int A, TypeId B) {
        destination_register = A;
        type = B;
    }

    @Override
    public void collectData(DataCollector data) {
        data.add(type);
    }

    @Override
    public String toString() {
        return "new-instance " + destination_register + " " + type;
    }
}
