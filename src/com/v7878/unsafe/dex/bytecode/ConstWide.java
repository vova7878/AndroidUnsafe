package com.v7878.unsafe.dex.bytecode;

public class ConstWide extends Instruction {

    public static final int OPCODE = 0x18;

    static void init() {
        InstructionReader.register(OPCODE, new InstructionReader.Reader_51l((A, B) -> {
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
    public String toString() {
        return "const-wide " + destination_register + " " + value;
    }
}
