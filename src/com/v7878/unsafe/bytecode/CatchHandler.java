package com.v7878.unsafe.bytecode;

import android.util.Pair;
import com.v7878.unsafe.io.RandomInput;
import java.util.Arrays;
import java.util.stream.Collectors;

public class CatchHandler {

    public Pair<TypeId, Integer>[] handlers;
    public int catch_all_addr;

    public static CatchHandler read(RandomInput in, ReadContext rc) {
        CatchHandler out = new CatchHandler();
        int size = in.readSLeb128();
        int handlersCount = Math.abs(size);
        out.handlers = new Pair[handlersCount];
        for (int i = 0; i < handlersCount; i++) {
            out.handlers[i] = new Pair<>(
                    rc.types[in.readULeb128()],
                    in.readULeb128());
        }
        out.catch_all_addr = size <= 0 ? in.readULeb128() : -1;
        return out;
    }

    @Override
    public String toString() {
        return "catch handler " + catch_all_addr + " "
                + Arrays.stream(handlers)
                        .map((pair) -> pair.first + " " + pair.second)
                        .collect(Collectors.joining(", ", "{", "}"));
    }
}
