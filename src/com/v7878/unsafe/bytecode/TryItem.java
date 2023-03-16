package com.v7878.unsafe.bytecode;

import static com.v7878.unsafe.Utils.*;
import com.v7878.unsafe.io.RandomInput;
import java.util.*;

public class TryItem {

    public static final int SIZE = 8;

    public int start_addr;
    public int insn_count;
    public CatchHandler handler;

    public static TryItem read(RandomInput in,
            Map<Integer, CatchHandler> handlers,
            int[] offsets) {
        TryItem out = new TryItem();

        int tmp = in.readInt(); // start_addr in code units
        out.start_addr = CodeItem.getInstructionIndex(offsets, tmp);

        tmp += in.readUnsignedShort(); // insn_count in code units
        out.insn_count = CodeItem.getInstructionIndex(offsets, tmp);
        out.insn_count -= out.start_addr;

        int handler_off = in.readUnsignedShort();
        out.handler = handlers.get(handler_off);
        assert_(out.handler != null, IllegalStateException::new,
                "unable to find catch handler with offset " + handler_off);
        return out;
    }

    public void fillContext(DataSet data) {
        handler.fillContext(data);
    }

    @Override
    public String toString() {
        return "try item " + start_addr + " " + insn_count + " " + handler;
    }
}
