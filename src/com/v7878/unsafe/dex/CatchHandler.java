package com.v7878.unsafe.dex;

import static com.v7878.unsafe.dex.DexConstants.NO_INDEX;
import com.v7878.unsafe.io.RandomInput;
import java.util.stream.Collectors;

public class CatchHandler {

    public PCList<CatchHandlerElement> handlers;
    public int catch_all_addr;

    public static CatchHandler read(RandomInput in,
            ReadContext context, int[] offsets) {
        CatchHandler out = new CatchHandler();
        int size = in.readSLeb128();
        int handlersCount = Math.abs(size);
        out.handlers = PCList.empty();

        for (int i = 0; i < handlersCount; i++) {
            out.handlers.add(CatchHandlerElement.read(in, context, offsets));
        }
        out.catch_all_addr = size <= 0
                ? CodeItem.getInstructionIndex(offsets, in.readULeb128()) : NO_INDEX;
        return out;
    }

    public void collectData(DataCollector data) {
        for (CatchHandlerElement tmp : handlers) {
            data.fill(tmp);
        }
    }

    @Override
    public String toString() {
        return "catch handler " + catch_all_addr + " " + handlers.stream()
                .map((handler) -> handler.getType() + " " + handler.getAddress())
                .collect(Collectors.joining(", ", "{", "}"));
    }
}
