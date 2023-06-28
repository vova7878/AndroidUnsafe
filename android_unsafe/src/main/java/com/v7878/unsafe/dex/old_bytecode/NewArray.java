package com.v7878.unsafe.dex.old_bytecode;

import com.v7878.unsafe.dex.DataCollector;
import com.v7878.unsafe.dex.TypeId;
import com.v7878.unsafe.dex.WriteContext;
import com.v7878.unsafe.dex.old_bytecode.InstructionReader.Reader_22c_22cs;
import com.v7878.unsafe.io.RandomOutput;

public class NewArray extends Instruction {

    public static final int OPCODE = 0x23;

    static void init() {
        InstructionReader.register(OPCODE, new Reader_22c_22cs((context, A, B, C) -> new NewArray(A, B, context.type(C))));
    }

    public final int destination_register;
    public final int size_register;
    public final TypeId type;

    public NewArray(int A, int B, TypeId C) {
        destination_register = A;
        size_register = B;
        type = C;
    }

    @Override
    public void collectData(DataCollector data) {
        data.add(type);
    }

    @Override
    public void write(WriteContext context, RandomOutput out) {
        InstructionWriter.write_22c_22cs(out, OPCODE,
                destination_register, size_register,
                context.getTypeIndex(type));
    }

    @Override
    public int opcode_old() {
        return OPCODE;
    }

    @Override
    public String name() {
        return "new-array";
    }

    @Override
    public String toString() {
        return name() + " " + destination_register + " " + size_register + " " + type;
    }

    @Override
    public NewArray clone() {
        return new NewArray(destination_register, size_register, type);
    }
}
