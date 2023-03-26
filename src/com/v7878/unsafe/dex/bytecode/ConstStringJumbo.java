package com.v7878.unsafe.dex.bytecode;

import com.v7878.unsafe.dex.bytecode.InstructionReader.*;
import com.v7878.unsafe.dex.DataCollector;

public class ConstStringJumbo extends Instruction {

    public static final int OPCODE = 0x1b;

    static void init() {
        InstructionReader.register(OPCODE, new Reader_31c((context, A, B) -> {
            return new ConstStringJumbo(A, context.string(B));
        }));
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
    public String toString() {
        return "const-string/jumbo " + destination_register + " \"" + value + "\"";
    }
}
