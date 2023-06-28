package com.v7878.unsafe.dex.old_bytecode;

import com.v7878.unsafe.dex.DataCollector;
import com.v7878.unsafe.dex.WriteContext;
import com.v7878.unsafe.dex.old_bytecode.InstructionReader.Reader_20bc_21c;
import com.v7878.unsafe.io.RandomOutput;

public class ConstString extends Instruction {

    public static final int OPCODE = 0x1a;

    static void init() {
        InstructionReader.register(OPCODE, new Reader_20bc_21c((context, A, B) -> new ConstString(A, context.string(B))));
    }

    public final int destination_register;
    public final String value;

    public ConstString(int A, String B) {
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
                destination_register, context.getStringIndex(value));
    }

    @Override
    public int opcode_old() {
        return OPCODE;
    }

    @Override
    public String name() {
        return "const-string";
    }

    @Override
    public String toString() {
        return name() + " " + destination_register + " \"" + value + "\"";
    }

    @Override
    public ConstString clone() {
        return new ConstString(destination_register, value);
    }
}
