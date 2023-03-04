package com.v7878.unsafe.memory;

import static com.v7878.unsafe.Utils.*;
import java.util.Objects;

public class MemorySegment {

    private final Pointer pointer;
    private final Layout layout;

    MemorySegment(Pointer pointer, Layout layout, boolean ignore_alignment) {
        Objects.requireNonNull(pointer);
        Objects.requireNonNull(layout);
        assert_(ignore_alignment || pointer.checkAlignmentShift(layout.alignmentShift()),
                IllegalArgumentException::new);
        pointer.addOffset(layout.size()); //check pointer + size
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

    public final MemorySegment select(LayoutPath.PathElement... elements) {
        LayoutPath p = layout.selectPath(elements);
        return new MemorySegment(pointer.addOffset(p.offset()), p.layout(), true);
    }

    public Object get() {
        return pointer.get((ValueLayout) layout);
    }

    public static MemorySegment getInstanceSegment(Object obj) {
        return Layout.getInstanceLayout(obj).bind(new Pointer(obj));
    }
}
