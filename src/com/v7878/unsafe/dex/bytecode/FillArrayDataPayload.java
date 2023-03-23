package com.v7878.unsafe.dex.bytecode;

import com.v7878.unsafe.dex.bytecode.InstructionReader.*;
import java.util.Arrays;

class FillArrayDataPayload extends Instruction {

    public static final int OPCODE = 0x03;

    static void init() {
        InstructionReader.registerExtra(OPCODE,
                new Reader_fill_array_data_payload((element_width, size, data) -> {
                    return new FillArrayDataPayload(element_width, size, data);
                }));
    }

    public final int element_width;
    public final int size;
    public final byte[] data;

    public FillArrayDataPayload(int element_width, int size, byte[] data) {
        this.element_width = element_width;
        this.size = size;
        this.data = data;
    }

    @Override
    public String toString() {
        return "fill-array-data-payload " + element_width + " " + size + " " + Arrays.toString(data);
    }
}
