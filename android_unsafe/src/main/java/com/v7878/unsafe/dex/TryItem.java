package com.v7878.unsafe.dex;

import static com.v7878.unsafe.Utils.assert_;

import com.v7878.unsafe.io.RandomInput;
import com.v7878.unsafe.io.RandomOutput;

import java.util.Map;
import java.util.Objects;

public class TryItem implements PublicCloneable {

    public static final int SIZE = 8;

    public int start_addr;
    public int insn_count;
    public CatchHandler handler;

    public TryItem(int start_addr, int insn_count, CatchHandler handler) {
        setStartAddress(start_addr);
        setInstructionCount(insn_count);
        setHandler(handler);
    }

    public final void setStartAddress(int start_addr) {
        assert_(start_addr >= 0, IllegalArgumentException::new,
                "start address can`t be negative");
        this.start_addr = start_addr;
    }

    public final int getStartAddress() {
        return start_addr;
    }

    public final void setInstructionCount(int insn_count) {
        assert_(insn_count >= 0, IllegalArgumentException::new,
                "instruction count can`t be negative");
        this.insn_count = insn_count;
    }

    public final int getInstructionCount() {
        return insn_count;
    }

    public final void setHandler(CatchHandler handler) {
        this.handler = Objects.requireNonNull(handler,
                "catch handler can`t be null").clone();
    }

    public final CatchHandler getHandler() {
        return handler;
    }

    public static TryItem read(RandomInput in,
                               Map<Integer, CatchHandler> handlers, int[] offsets) {
        int tmp = in.readInt(); // start_addr in code units
        int start_addr = CodeItem.getInstructionIndex(offsets, tmp);

        tmp += in.readUnsignedShort(); // insn_count in code units
        int insn_count = CodeItem.getInstructionIndex(offsets, tmp);
        insn_count -= start_addr;

        int handler_off = in.readUnsignedShort();
        CatchHandler handler = handlers.get(handler_off);
        assert_(handler != null, IllegalStateException::new,
                "unable to find catch handler with offset " + handler_off);
        return new TryItem(start_addr, insn_count, handler);
    }

    public void collectData(DataCollector data) {
        data.fill(handler);
    }

    public void write(WriteContext context, RandomOutput out,
                      Map<CatchHandler, Integer> handlers, int[] offsets) {
        int tmp = offsets[start_addr];
        out.writeInt(tmp);
        tmp = offsets[start_addr + insn_count] - tmp;
        out.writeShort(tmp);
        Integer offset = handlers.get(handler);
        assert_(offset != null, IllegalStateException::new,
                "unable to find offset for catch handler: " + handler);
        out.writeShort(offset);
    }

    @Override
    public String toString() {
        return "try item " + start_addr + " " + insn_count + " " + handler;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TryItem) {
            TryItem tobj = (TryItem) obj;
            return start_addr == tobj.start_addr
                    && insn_count == tobj.insn_count
                    && Objects.equals(handler, tobj.handler);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(start_addr, insn_count, handler);
    }

    @Override
    public TryItem clone() {
        return new TryItem(start_addr, insn_count, handler);
    }
}
