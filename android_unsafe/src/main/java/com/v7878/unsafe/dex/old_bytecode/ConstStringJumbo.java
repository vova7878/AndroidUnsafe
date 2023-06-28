package com.v7878.unsafe.dex.old_bytecode;

import com.v7878.unsafe.dex.DataCollector;
import com.v7878.unsafe.dex.WriteContext;
import com.v7878.unsafe.dex.old_bytecode.InstructionReader.Reader_31c;
import com.v7878.unsafe.io.RandomOutput;

public class ConstStringJumbo extends Instruction {

    public static final int OPCODE = 0x1b;

    static void init() {
        InstructionReader.register(OPCODE, new Reader_31c((context, A, B) -> new ConstStringJumbo(A, context.string(B))));
    }

    public final int destination_register;
    public final String value;

    public ConstStringJumbo(int A, String B) {
        destination_register = A;
        value = B;
    }

    @Override
    public void collectData(DataCollector data) {
        data.add(value);
    }

    @Override
    public void write(WriteContext context, RandomOutput out) {
        InstructionWriter.write_31i32_31t_31c(out, OPCODE,
                destination_register, context.getStringIndex(value));
    }

    @Override
    public int opcode_old() {
        return OPCODE;
    }

    @Override
    public String name() {
        return "const-string/jumbo";
    }

    @Override
    public String toString() {
        return name() + " " + destination_register + " \"" + value + "\"";
    }

    @Override
    public ConstStringJumbo clone() {
        return new ConstStringJumbo(destination_register, value);
    }
}
