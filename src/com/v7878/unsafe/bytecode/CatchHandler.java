package com.v7878.unsafe.bytecode;

import android.util.Pair;
import static com.v7878.unsafe.bytecode.DexConstants.NO_INDEX;
import com.v7878.unsafe.io.RandomInput;
import java.util.Arrays;
import java.util.stream.Collectors;

public class CatchHandler {

    public Pair<TypeId, Integer>[] handlers;
    public int catch_all_addr;

    public static CatchHandler read(RandomInput in,
            ReadContext context, int[] offsets) {
        CatchHandler out = new CatchHandler();
        int size = in.readSLeb128();
        int handlersCount = Math.abs(size);
        out.handlers = new Pair[handlersCount];

        for (int i = 0; i < handlersCount; i++) {
            out.handlers[i] = new Pair<>(context.type(in.readULeb128()),
                    CodeItem.getInstructionIndex(offsets, in.readULeb128()));
        }
        out.catch_all_addr = size <= 0
                ? CodeItem.getInstructionIndex(offsets, in.readULeb128()) : NO_INDEX;
        return out;
    }

    public void fillContext(DataSet data) {
        for (Pair<TypeId, Integer> tmp : handlers) {
            data.addType(tmp.first);
        }
    }

    @Override
    public String toString() {
        return "catch handler " + catch_all_addr + " "
                + Arrays.stream(handlers)
                        .map((pair) -> pair.first + " " + pair.second)
                        .collect(Collectors.joining(", ", "{", "}"));
    }
}
