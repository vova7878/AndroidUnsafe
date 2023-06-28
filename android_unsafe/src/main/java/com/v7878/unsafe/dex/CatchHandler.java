package com.v7878.unsafe.dex;

import static com.v7878.unsafe.Utils.assert_;

import com.v7878.unsafe.io.RandomInput;
import com.v7878.unsafe.io.RandomOutput;

import java.util.Objects;
import java.util.stream.Collectors;

public class CatchHandler implements PublicCloneable {

    private PCList<CatchHandlerElement> handlers;
    private Integer catch_all_addr;

    public CatchHandler(PCList<CatchHandlerElement> handlers,
                        Integer catch_all_addr) {
        setHandlers(handlers);
        setCatchAllAddress(catch_all_addr);
    }

    public final void setHandlers(PCList<CatchHandlerElement> handlers) {
        this.handlers = handlers == null
                ? PCList.empty() : handlers.clone();
    }

    public final PCList<CatchHandlerElement> getHandlers() {
        return handlers;
    }

    public final void setCatchAllAddress(Integer catch_all_addr) {
        assert_(catch_all_addr == null || catch_all_addr >= 0,
                IllegalArgumentException::new,
                "instruction address can`t be negative");
        this.catch_all_addr = catch_all_addr;
    }

    public final Integer getCatchAllAddress() {
        return catch_all_addr;
    }

    public static CatchHandler read(RandomInput in, ReadContext context) {
        int size = in.readSLeb128();
        int handlersCount = Math.abs(size);
        PCList<CatchHandlerElement> handlers = PCList.empty();
        for (int i = 0; i < handlersCount; i++) {
            handlers.add(CatchHandlerElement.read(in, context));
        }
        Integer catch_all_addr = null;
        if (size <= 0) {
            catch_all_addr = in.readULeb128();
        }
        return new CatchHandler(handlers, catch_all_addr);
    }

    public void collectData(DataCollector data) {
        for (CatchHandlerElement tmp : handlers) {
            data.fill(tmp);
        }
    }

    public void write(WriteContext context, RandomOutput out) {
        assert_(!(handlers.isEmpty() && catch_all_addr == null),
                IllegalStateException::new, "unable to write empty catch handler");
        out.writeSLeb128(catch_all_addr == null ? handlers.size() : -handlers.size());
        for (CatchHandlerElement tmp : handlers) {
            tmp.write(context, out);
        }
        if (catch_all_addr != null) {
            out.writeULeb128(catch_all_addr);
        }
    }

    @Override
    public String toString() {
        return "catch handler " + catch_all_addr + " " + handlers.stream()
                .map((handler) -> handler.getType() + " " + handler.getAddress())
                .collect(Collectors.joining(", ", "{", "}"));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CatchHandler) {
            CatchHandler chobj = (CatchHandler) obj;
            return Objects.equals(catch_all_addr, chobj.catch_all_addr)
                    && Objects.equals(handlers, chobj.handlers);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(handlers, catch_all_addr);
    }

    @Override
    public CatchHandler clone() {
        return new CatchHandler(handlers, catch_all_addr);
    }
}
