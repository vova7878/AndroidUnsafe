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

    public boolean get(ValueLayout.OfBoolean layout, long offset) {
        return pointer.addOffset(offset).get(layout);
    }

    public byte get(ValueLayout.OfByte layout, long offset) {
        return pointer.addOffset(offset).get(layout);
    }

    public char get(ValueLayout.OfChar layout, long offset) {
        return pointer.addOffset(offset).get(layout);
    }

    public short get(ValueLayout.OfShort layout, long offset) {
        return pointer.addOffset(offset).get(layout);
    }

    public int get(ValueLayout.OfInt layout, long offset) {
        return pointer.addOffset(offset).get(layout);
    }

    public float get(ValueLayout.OfFloat layout, long offset) {
        return pointer.addOffset(offset).get(layout);
    }

    public long get(ValueLayout.OfLong layout, long offset) {
        return pointer.addOffset(offset).get(layout);
    }

    public double get(ValueLayout.OfDouble layout, long offset) {
        return pointer.addOffset(offset).get(layout);
    }

    public Object get(ValueLayout.OfObject layout, long offset) {
        return pointer.addOffset(offset).get(layout);
    }

    public Pointer get(ValueLayout.OfAddress layout, long offset) {
        return pointer.addOffset(offset).get(layout);
    }

    public Object getValue() {
        return pointer.getValue((ValueLayout) layout);
    }

    public void put(ValueLayout.OfBoolean layout, long offset, boolean value) {
        pointer.addOffset(offset).put(layout, value);
    }

    public void put(ValueLayout.OfByte layout, long offset, byte value) {
        pointer.addOffset(offset).put(layout, value);
    }

    public void put(ValueLayout.OfChar layout, long offset, char value) {
        pointer.addOffset(offset).put(layout, value);
    }

    public void put(ValueLayout.OfShort layout, long offset, short value) {
        pointer.addOffset(offset).put(layout, value);
    }

    public void put(ValueLayout.OfInt layout, long offset, int value) {
        pointer.addOffset(offset).put(layout, value);
    }

    public void put(ValueLayout.OfFloat layout, long offset, float value) {
        pointer.addOffset(offset).put(layout, value);
    }

    public void put(ValueLayout.OfLong layout, long offset, long value) {
        pointer.addOffset(offset).put(layout, value);
    }

    public void put(ValueLayout.OfDouble layout, long offset, double value) {
        pointer.addOffset(offset).put(layout, value);
    }

    public void put(ValueLayout.OfObject layout, long offset, Object value) {
        pointer.addOffset(offset).put(layout, value);
    }

    public void put(ValueLayout.OfAddress layout, long offset, Pointer value) {
        pointer.addOffset(offset).put(layout, value);
    }

    public static MemorySegment getInstanceSegment(Object obj) {
        return Layout.getInstanceLayout(obj).bind(new Pointer(obj));
    }
}
