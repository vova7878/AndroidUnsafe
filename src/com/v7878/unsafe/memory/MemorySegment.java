package com.v7878.unsafe.memory;

import static com.v7878.unsafe.Utils.*;
import java.util.Objects;

public class MemorySegment {

    private final Pointer pointer;
    private final Layout layout;

    MemorySegment(Pointer pointer, Layout layout) {
        Objects.requireNonNull(pointer);
        Objects.requireNonNull(layout);
        assert_(pointer.checkAlignmentShift(layout.alignmentShift()),
                IllegalArgumentException::new);
        this.pointer = pointer;
        this.layout = layout;
    }

    public Layout layout() {
        return layout;
    }

    public Pointer pointer() {
        return pointer;
    }

    public long size() {
        return layout.size();
    }

    public int alignmentShift() {
        return pointer.getAlignmentShift();
    }

    public long alignment() {
        return 1L << alignmentShift();
    }

    @Override
    public String toString() {
        return "MemorySegment{" + pointer + "; layout " + layout + "}";
    }
}
