package com.v7878.unsafe.memory;

import java.util.Objects;
import java.util.Optional;

public class SequenceLayout extends Layout {

    private final long elementCount;
    private final Layout elementLayout;

    SequenceLayout(long elemCount, Layout elementLayout) {
        this(elemCount, elementLayout, elementLayout.alignmentShift(), Optional.empty());
    }

    SequenceLayout(long elementCount, Layout elementLayout, int align_shift, Optional<String> name) {
        super(Math.multiplyExact(elementCount, elementLayout.size()), align_shift, name);
        this.elementCount = elementCount;
        this.elementLayout = elementLayout;
    }

    public Layout elementLayout() {
        return elementLayout;
    }

    public long elementCount() {
        return elementCount;
    }

    @Override
    public boolean hasNaturalAlignment() {
        return alignmentShift() == elementLayout.alignmentShift();
    }

    @Override
    SequenceLayout dup(int align_shift, Optional<String> name) {
        return new SequenceLayout(elementCount, elementLayout, align_shift, name);
    }

    @Override
    public SequenceLayout withName(String name) {
        return (SequenceLayout) super.withName(name);
    }

    @Override
    public SequenceLayout withAlignmentShift(int align_shift) {
        return (SequenceLayout) super.withAlignmentShift(align_shift);
    }

    @Override
    public SequenceLayout withAlignment(long alignment) {
        return (SequenceLayout) super.withAlignment(alignment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), elementCount, elementLayout);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!super.equals(other)) {
            return false;
        }
        if (!(other instanceof SequenceLayout)) {
            return false;
        }
        SequenceLayout otherLayout = (SequenceLayout) other;
        return elementCount == otherLayout.elementCount
                && elementLayout.equals(otherLayout.elementLayout);
    }

    @Override
    public String toString() {
        return decorateLayoutString(String.format("[%s:%s]",
                elementCount, elementLayout));
    }

    //TODO
    /*public SequenceLayout withElementCount(long elementCount) {
        AbstractLayout.checkSize(elementCount, true);
        return new SequenceLayout(elementCount, elementLayout, alignment, name());
    }
    public SequenceLayout reshape(long... elementCounts)
    public SequenceLayout flatten()*/
}
