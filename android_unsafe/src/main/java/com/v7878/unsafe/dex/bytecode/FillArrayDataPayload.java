package com.v7878.unsafe.dex.bytecode;

import com.v7878.unsafe.dex.WriteContext;
import com.v7878.unsafe.dex.bytecode.InstructionReader.Reader_fill_array_data_payload;
import com.v7878.unsafe.io.RandomOutput;

import java.util.Arrays;

class FillArrayDataPayload extends Instruction {

    public static final int OPCODE = 0x03;

    static void init() {
        InstructionReader.registerExtra(OPCODE,
                new Reader_fill_array_data_payload((element_width, data) -> {
                    return new FillArrayDataPayload(element_width, data);
                }));
    }

    public final int element_width;
    public final byte[] data;

    public FillArrayDataPayload(int element_width, byte[] data) {
        this.element_width = element_width;
        this.data = data;
    }

    @Override
    public void write(WriteContext context, RandomOutput out) {
        InstructionWriter.write_fill_array_data_payload(out,
                OPCODE, element_width, data);
    }

    @Override
    public int opcode() {
        return OPCODE << 8;
    }

    @Override
    public String name() {
        return "fill-array-data-payload";
    }

    @Override
    public String toString() {
        return name() + " " + element_width + " " + Arrays.toString(data);
    }

    @Override
    public FillArrayDataPayload clone() {
        return new FillArrayDataPayload(element_width, data);
    }
}
