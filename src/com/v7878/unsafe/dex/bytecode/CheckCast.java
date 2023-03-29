package com.v7878.unsafe.dex.bytecode;

import com.v7878.unsafe.dex.DataCollector;
import com.v7878.unsafe.dex.TypeId;
import com.v7878.unsafe.dex.WriteContext;
import com.v7878.unsafe.dex.bytecode.InstructionReader.Reader_20bc_21c;
import com.v7878.unsafe.io.RandomOutput;

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
    public void collectData(DataCollector data) {
        data.add(type);
    }

    @Override
    public void write(WriteContext context, RandomOutput out) {
        InstructionWriter.write_22x_20bc_21c(out, OPCODE,
                reference_bearing_register, context.getTypeIndex(type));
    }

    @Override
    public int opcode() {
        return OPCODE;
    }

    @Override
    public String name() {
        return "check-cast";
    }

    @Override
    public String toString() {
        return name() + " " + reference_bearing_register + " " + type;
    }

    @Override
    public CheckCast clone() {
        return new CheckCast(reference_bearing_register, type);
    }
}
