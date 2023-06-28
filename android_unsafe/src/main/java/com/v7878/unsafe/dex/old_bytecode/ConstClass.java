package com.v7878.unsafe.dex.old_bytecode;

import com.v7878.unsafe.dex.DataCollector;
import com.v7878.unsafe.dex.TypeId;
import com.v7878.unsafe.dex.WriteContext;
import com.v7878.unsafe.dex.old_bytecode.InstructionReader.Reader_20bc_21c;
import com.v7878.unsafe.io.RandomOutput;

public class ConstClass extends Instruction {

    public static final int OPCODE = 0x1c;

    static void init() {
        InstructionReader.register(OPCODE, new Reader_20bc_21c((context, A, B) -> new ConstClass(A, context.type(B))));
    }

    public final int destination_register;
    public final TypeId value;

    public ConstClass(int A, TypeId B) {
        destination_register = A;
        value = B;
    }

    @Override
    public void collectData(DataCollector data) {
        data.add(value);
    }

    @Override
    public void write(WriteContext context, RandomOutput out) {
        InstructionWriter.write_22x_20bc_21c(out, OPCODE,
                destination_register, context.getTypeIndex(value));
    }

    @Override
    public int opcode_old() {
        return OPCODE;
    }

    @Override
    public String name() {
        return "const-class";
    }

    @Override
    public String toString() {
        return name() + " " + destination_register + " " + value;
    }

    @Override
    public ConstClass clone() {
        return new ConstClass(destination_register, value);
    }
}
