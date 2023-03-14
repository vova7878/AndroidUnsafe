package com.v7878.unsafe.bytecode;

import static com.v7878.unsafe.Utils.*;
import com.v7878.unsafe.io.RandomInput;
import java.util.*;

public class TryItem {

    public static final int SIZE = 8;

    public int start_addr;
    public int insn_count;
    public CatchHandler handler;

    public static TryItem read(RandomInput in, Map<Integer, CatchHandler> handlers) {
        TryItem out = new TryItem();
        out.start_addr = in.readInt();
        out.insn_count = in.readUnsignedShort();
        int handler_off = in.readUnsignedShort();
        out.handler = handlers.get(handler_off);
        assert_(out.handler != null, IllegalStateException::new,
                "can't find catch handler with offset " + handler_off);
        return out;
    }

    @Override
    public String toString() {
        return "try item " + start_addr + " " + insn_count + " " + handler;
    }
}
