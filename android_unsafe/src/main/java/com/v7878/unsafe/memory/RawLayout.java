package com.v7878.unsafe.memory;

import java.util.Objects;
import java.util.Optional;

public class RawLayout extends Layout {

    RawLayout(long size) {
        this(size, 0, Optional.empty());
    }

    RawLayout(long size, int align_shift, Optional<String> name) {
        super(size, align_shift, name);
    }

    @Override
    public boolean hasNaturalAlignment() {
        return alignmentShift() == 0;
    }

    @Override
    RawLayout dup(int align_shift, Optional<String> name) {
        return new RawLayout(size(), align_shift, name);
    }

    @Override
    public RawLayout withName(String name) {
        return (RawLayout) super.withName(name);
    }

    @Override
    public RawLayout withAlignmentShift(int align_shift) {
        return (RawLayout) super.withAlignmentShift(align_shift);
    }

    @Override
    public RawLayout withAlignment(long alignment) {
        return (RawLayout) super.withAlignment(alignment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), RawLayout.class);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!super.equals(other)) {
            return false;
        }
        return other instanceof RawLayout;
    }

    @Override
    public String toString() {
        return decorateLayoutString("X");
    }

    static class Padding extends RawLayout {

        public Padding(long size) {
            super(size);
        }

        public Padding(long size, int align_shift, Optional<String> name) {
            super(size, align_shift, name);
        }

        @Override
        Padding dup(int align_shift, Optional<String> name) {
            return new Padding(size(), align_shift, name);
        }

        @Override
        public Padding withName(String name) {
            return (Padding) super.withName(name);
        }

        @Override
        public Padding withAlignmentShift(int align_shift) {
            return (Padding) super.withAlignmentShift(align_shift);
        }

        @Override
        public Padding withAlignment(long alignment) {
            return (Padding) super.withAlignment(alignment);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), Padding.class);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!super.equals(other)) {
                return false;
            }
            return other instanceof Padding;
        }

        @Override
        public String toString() {
            return decorateLayoutString("x");
        }
    }
}
