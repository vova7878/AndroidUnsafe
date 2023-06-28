package com.v7878.unsafe.dex.old_bytecode;

import com.v7878.unsafe.dex.DataCollector;
import com.v7878.unsafe.dex.TypeId;
import com.v7878.unsafe.dex.WriteContext;
import com.v7878.unsafe.dex.old_bytecode.InstructionReader.Reader_20bc_21c;
import com.v7878.unsafe.io.RandomOutput;

public class NewInstance extends Instruction {

    public static final int OPCODE = 0x22;

    static void init() {
        InstructionReader.register(OPCODE, new Reader_20bc_21c((context, A, B) -> new NewInstance(A, context.type(B))));
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
    public void write(WriteContext context, RandomOutput out) {
        InstructionWriter.write_22x_20bc_21c(out, OPCODE,
                destination_register, context.getTypeIndex(type));
    }

    @Override
    public int opcode_old() {
        return OPCODE;
    }

    @Override
    public String name() {
        return "new-instance";
    }

    @Override
    public String toString() {
        return name() + " " + destination_register + " " + type;
    }

    @Override
    public NewInstance clone() {
        return new NewInstance(destination_register, type);
    }
}
