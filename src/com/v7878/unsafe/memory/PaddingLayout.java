package com.v7878.unsafe.memory;

import java.util.Objects;
import java.util.Optional;

public class PaddingLayout extends Layout {

    PaddingLayout(long size) {
        this(size, 0, Optional.empty());
    }

    PaddingLayout(long size, int align_shift, Optional<String> name) {
        super(size, align_shift, name);
    }

    @Override
    public boolean hasNaturalAlignment() {
        return alignmentShift() == 0;
    }

    @Override
    PaddingLayout dup(int align_shift, Optional<String> name) {
        return new PaddingLayout(size(), align_shift, name);
    }

    @Override
    public PaddingLayout withName(String name) {
        return (PaddingLayout) super.withName(name);
    }

    @Override
    public PaddingLayout withAlignmentShift(int align_shift) {
        return (PaddingLayout) super.withAlignmentShift(align_shift);
    }

    @Override
    public PaddingLayout withAlignment(long alignment) {
        return (PaddingLayout) super.withAlignment(alignment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), PaddingLayout.class);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!super.equals(other)) {
            return false;
        }
        return other instanceof PaddingLayout;
    }

    @Override
    public String toString() {
        return decorateLayoutString("x");
    }
}
