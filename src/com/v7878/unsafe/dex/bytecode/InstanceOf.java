package com.v7878.unsafe.dex.bytecode;

import com.v7878.unsafe.dex.DataCollector;
import com.v7878.unsafe.dex.TypeId;
import com.v7878.unsafe.dex.WriteContext;
import com.v7878.unsafe.dex.bytecode.InstructionReader.Reader_22c_22cs;
import com.v7878.unsafe.io.RandomOutput;

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
    public void collectData(DataCollector data) {
        data.add(type);
    }

    @Override
    public void write(WriteContext context, RandomOutput out) {
        InstructionWriter.write_22c_22cs(out, OPCODE, destination_register,
                reference_bearing_register, context.getTypeIndex(type));
    }

    @Override
    public int opcode() {
        return OPCODE;
    }

    @Override
    public String name() {
        return "instance-of";
    }

    @Override
    public String toString() {
        return name() + " " + destination_register
                + " " + reference_bearing_register + " " + type;
    }

    @Override
    public InstanceOf clone() {
        return new InstanceOf(destination_register,
                reference_bearing_register, type);
    }
}
