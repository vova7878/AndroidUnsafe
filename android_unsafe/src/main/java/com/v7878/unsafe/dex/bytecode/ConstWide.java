package com.v7878.unsafe.dex.bytecode;

import com.v7878.unsafe.dex.WriteContext;
import com.v7878.unsafe.dex.bytecode.InstructionReader.Reader_51l;
import com.v7878.unsafe.io.RandomOutput;

public class ConstWide extends Instruction {

    public static final int OPCODE = 0x18;

    static void init() {
        InstructionReader.register(OPCODE, new Reader_51l((A, B) -> {
            return new ConstWide(A, B);
        }));
    }

    public final int destination_register;
    public final long value;

    public ConstWide(int A, long B) {
        destination_register = A;
        value = B;
    }

    @Override
    public void write(WriteContext context, RandomOutput out) {
        InstructionWriter.write_51l(out, OPCODE,
                destination_register, value);
    }

    @Override
    public int opcode() {
        return OPCODE;
    }

    @Override
    public String name() {
        return "const-wide";
    }

    @Override
    public String toString() {
        return name() + " " + destination_register + " " + value;
    }

    @Override
    public ConstWide clone() {
        return new ConstWide(destination_register, value);
    }
}
