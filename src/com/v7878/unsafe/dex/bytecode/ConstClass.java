package com.v7878.unsafe.dex.bytecode;

import com.v7878.unsafe.dex.TypeId;
import com.v7878.unsafe.dex.DataSet;
import com.v7878.unsafe.dex.bytecode.InstructionReader.Reader_20bc_21c;

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
    public void fillContext(DataSet data) {
        data.addType(value);
    }

    @Override
    public String toString() {
        return "const-class " + destination_register + " \"" + value + "\"";
    }
}
