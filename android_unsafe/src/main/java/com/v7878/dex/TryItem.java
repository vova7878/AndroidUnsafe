package com.v7878.dex;

import static com.v7878.unsafe.Utils.assert_;

import android.util.SparseArray;

import com.v7878.dex.io.RandomInput;
import com.v7878.dex.io.RandomOutput;
import com.v7878.misc.Checks;

import java.util.Map;
import java.util.Objects;

public class TryItem implements PublicCloneable {

    public static final int SIZE = 8;
    public static final int ALIGNMENT = 4;

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
        Checks.checkRange(insn_count, 0, 1 << 16);
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

    public static TryItem read(RandomInput in, SparseArray<CatchHandler> handlers) {
        int start_addr = in.readInt(); // in code units
        int insn_count = in.readUnsignedShort(); // in code units

        int handler_off = in.readUnsignedShort();
        CatchHandler handler = handlers.get(handler_off);
        assert_(handler != null, IllegalStateException::new,
                "unable to find catch handler with offset " + handler_off);
        return new TryItem(start_addr, insn_count, handler);
    }

    public void collectData(DataCollector data) {
        data.fill(handler);
    }

    public void write(RandomOutput out, Map<CatchHandler, Integer> handlers) {
        out.writeInt(start_addr);
        out.writeShort(insn_count);
        Integer offset = handlers.get(handler);
        assert_(offset != null, IllegalStateException::new,
                "unable to find offset for catch handler");
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
