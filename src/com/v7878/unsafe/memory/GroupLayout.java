package com.v7878.unsafe.memory;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class GroupLayout extends Layout {

    enum Kind {
        STRUCT(""),
        UNION("|");

        final String delimTag;

        Kind(String delimTag) {
            this.delimTag = delimTag;
        }
    }

    static int alignShiftOf(List<Layout> elems) {
        return elems.stream()
                .mapToInt(Layout::alignmentShift).max()
                .orElse(0);
    }

    private final Kind kind;
    private final List<Layout> elements;

    GroupLayout(Kind kind, List<Layout> elements, long size, int align_shift) {
        this(kind, elements, size, align_shift, Optional.empty());
    }

    GroupLayout(Kind kind, List<Layout> elements, long size, int align_shift, Optional<String> name) {
        super(size, align_shift, name);
        this.kind = kind;
        this.elements = elements;
    }

    public boolean isStruct() {
        return kind == Kind.STRUCT;
    }

    public boolean isUnion() {
        return kind == Kind.UNION;
    }

    public List<Layout> memberLayouts() {
        return Collections.unmodifiableList(elements);
    }

    @Override
    public boolean hasNaturalAlignment() {
        return alignmentShift() == alignShiftOf(elements);
    }

    @Override
    GroupLayout dup(int align_shift, Optional<String> name) {
        return new GroupLayout(kind, elements, size(), align_shift, name);
    }

    @Override
    public GroupLayout withName(String name) {
        return (GroupLayout) super.withName(name);
    }

    @Override
    public GroupLayout withAlignmentShift(int align_shift) {
        return (GroupLayout) super.withAlignmentShift(align_shift);
    }

    @Override
    public GroupLayout withAlignment(long alignment) {
        return (GroupLayout) super.withAlignment(alignment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), kind, elements);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!super.equals(other)) {
            return false;
        }
        if (!(other instanceof GroupLayout)) {
            return false;
        }
        GroupLayout otherLayout = (GroupLayout) other;
        return kind == otherLayout.kind
                && elements.equals(otherLayout.elements);
    }

    @Override
    public String toString() {
        return decorateLayoutString(elements.stream()
                .map(Object::toString)
                .collect(Collectors.joining(kind.delimTag, "[", "]")));
    }
}
