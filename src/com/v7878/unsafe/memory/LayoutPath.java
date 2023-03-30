package com.v7878.unsafe.memory;

import static com.v7878.unsafe.Utils.assert_;
import java.util.List;
import java.util.Objects;
import java.util.Spliterator;
import static java.util.Spliterator.*;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

public class LayoutPath {

    private final long offset;
    private final Layout layout;
    private final LayoutPath parent;

    private LayoutPath(long offset, Layout layout, LayoutPath parent) {
        this.offset = offset;
        this.layout = layout;
        this.parent = parent;
    }

    public Layout layout() {
        return layout;
    }

    public long offset() {
        if (parent == null) {
            return 0;
        }
        return parent.offset() + offset;
    }

    @Override
    public String toString() {
        return String.format("LayoutPath{offset = %s, layout = %s}", offset(), layout);
    }

    public Spliterator<LayoutPath> spliterator() {
        if (layout instanceof GroupLayout) {
            GroupLayout glayout = (GroupLayout) layout;
            return new GroupSplitter(glayout.memberLayouts(), glayout.isStruct(), this);
        }
        if (layout instanceof SequenceLayout) {
            SequenceLayout slayout = (SequenceLayout) layout;
            return new SequenceSplitter(slayout.elementLayout(), slayout.elementCount(), this);
        }
        throw new IllegalArgumentException("attempting get spliterator for a non-group or non-sequence layout");
    }

    private static IllegalArgumentException badLayoutPath(String cause) {
        return new IllegalArgumentException("Bad layout path: " + cause);
    }

    private LayoutPath groupElement(String name, boolean throw_if_not_found) {
        Objects.requireNonNull(name);
        assert_(layout instanceof GroupLayout, IllegalArgumentException::new,
                "attempting to select a group element from a non-group layout");
        GroupLayout g = (GroupLayout) layout;
        long elem_offset = 0;
        Layout elem = null;
        for (int i = 0; i < g.memberLayouts().size(); i++) {
            Layout l = g.memberLayouts().get(i);
            if (l.name().isPresent()) {
                if (l.name().get().equals(name)) {
                    elem = l;
                    break;
                }
            } else if (l instanceof GroupLayout) {
                LayoutPath tmp = new LayoutPath(elem_offset, l, this);
                LayoutPath out = tmp.groupElement(name, false);
                if (out != null) {
                    return out;
                }
            }
            if (g.isStruct()) {
                elem_offset += l.size();
            }
        }
        if (elem == null) {
            if (throw_if_not_found) {
                throw badLayoutPath("cannot resolve '" + name + "' in layout " + layout);
            }
            return null;
        }
        return new LayoutPath(elem_offset, elem, this);
    }

    public LayoutPath groupElement(String name) {
        return groupElement(name, true);
    }

    private void checkGroupBounds(int size, int index) {
        if (index >= size) {
            throw badLayoutPath(String.format("Group index out of bound; found: %d, size: %d", index, size));
        }
    }

    public LayoutPath groupElement(int index) {
        assert_(layout instanceof GroupLayout, IllegalArgumentException::new,
                "attempting to select a group element from a non-group layout");
        GroupLayout g = (GroupLayout) layout;
        List<Layout> members = g.memberLayouts();
        checkGroupBounds(members.size(), index);
        Layout elem = members.get(index);
        long elem_offset = 0;
        if (g.isStruct()) {
            for (int i = 0; i < g.memberLayouts().size(); i++) {
                elem_offset += members.get(i).size();
            }
        }
        return new LayoutPath(elem_offset, elem, this);
    }

    private void checkSequenceBounds(SequenceLayout seq, long index) {
        if (index >= seq.elementCount()) {
            throw badLayoutPath(String.format("Sequence index out of bound; found: %d, size: %d", index, seq.elementCount()));
        }
    }

    public LayoutPath sequenceElement(long index) {
        assert_(layout instanceof SequenceLayout, IllegalArgumentException::new,
                "attempting to select a sequence element from a non-sequence layout");
        SequenceLayout seq = (SequenceLayout) layout;
        checkSequenceBounds(seq, index);
        long elemSize = seq.elementLayout().size();
        long elemOffset = elemSize * index;
        return new LayoutPath(elemOffset, seq.elementLayout(), this);
    }

    public static LayoutPath rootPath(Layout layout) {
        return new LayoutPath(0, layout, null);
    }

    public static final class PathElement implements UnaryOperator<LayoutPath> {

        public enum PathKind {
            SEQUENCE_ELEMENT_INDEX("bound sequence element"),
            //SEQUENCE_RANGE("sequence range"),
            GROUP_ELEMENT("group element"),
            GROUP_ELEMENT_INDEX("group element by index");

            private final String description;

            PathKind(String description) {
                this.description = description;
            }

            public String description() {
                return description;
            }
        }

        private final PathKind kind;
        private final UnaryOperator<LayoutPath> pathOp;

        private PathElement(PathKind kind, UnaryOperator<LayoutPath> pathOp) {
            this.kind = kind;
            this.pathOp = pathOp;
        }

        @Override
        public LayoutPath apply(LayoutPath layoutPath) {
            return pathOp.apply(layoutPath);
        }

        public PathKind kind() {
            return kind;
        }

        public static PathElement groupElement(String name) {
            Objects.requireNonNull(name);
            return new PathElement(PathKind.GROUP_ELEMENT,
                    path -> path.groupElement(name));
        }

        public static PathElement groupElement(int index) {
            if (index < 0) {
                throw new IllegalArgumentException("Index must be positive: " + index);
            }
            return new LayoutPath.PathElement(PathKind.GROUP_ELEMENT_INDEX,
                    path -> path.groupElement(index));
        }

        public static PathElement sequenceElement(long index) {
            if (index < 0) {
                throw new IllegalArgumentException("Index must be positive: " + index);
            }
            return new LayoutPath.PathElement(PathKind.SEQUENCE_ELEMENT_INDEX,
                    path -> path.sequenceElement(index));
        }
    }

    static class GroupSplitter implements Spliterator<LayoutPath> {

        private final LayoutPath parent;
        private final List<Layout> members;
        private final boolean collect_offset;
        long currentOffset;
        int currentIndex;

        GroupSplitter(List<Layout> members, boolean collect_offset, LayoutPath parent) {
            this.parent = parent;
            this.members = members;
            this.collect_offset = collect_offset;
            currentOffset = 0;
            currentIndex = 0;
        }

        @Override
        public GroupSplitter trySplit() {
            return null;
        }

        @Override
        public boolean tryAdvance(Consumer<? super LayoutPath> action) {
            Objects.requireNonNull(action);
            if (currentIndex < members.size()) {
                Layout elem = members.get(currentIndex);
                try {
                    action.accept(new LayoutPath(currentOffset, elem, parent));
                } finally {
                    currentIndex++;
                    if (collect_offset) {
                        currentOffset += elem.size();
                    }
                }
                return true;
            } else {
                return false;
            }
        }

        @Override
        public void forEachRemaining(Consumer<? super LayoutPath> action) {
            Objects.requireNonNull(action);
            try {
                for (; currentIndex < members.size(); currentIndex++) {
                    Layout elem = members.get(currentIndex);
                    action.accept(new LayoutPath(currentOffset, elem, parent));
                    if (collect_offset) {
                        currentOffset += elem.size();
                    }
                }
            } finally {
                currentIndex = members.size();
            }
        }

        @Override
        public long estimateSize() {
            return members.size();
        }

        @Override
        public int characteristics() {
            return NONNULL | SUBSIZED | SIZED | IMMUTABLE | ORDERED;
        }
    }

    static class SequenceSplitter implements Spliterator<LayoutPath> {

        private final LayoutPath parent;
        private final Layout element;
        private final long elementCount;
        long currentIndex;

        SequenceSplitter(Layout element, long elementCount, LayoutPath parent) {
            this.parent = parent;
            this.element = element;
            this.elementCount = elementCount;
            currentIndex = 0;
        }

        @Override
        public GroupSplitter trySplit() {
            return null;
        }

        @Override
        public boolean tryAdvance(Consumer<? super LayoutPath> action) {
            Objects.requireNonNull(action);
            if (currentIndex < elementCount) {
                try {
                    action.accept(new LayoutPath(currentIndex * element.size(), element, parent));
                } finally {
                    currentIndex++;
                }
                return true;
            } else {
                return false;
            }
        }

        @Override
        public void forEachRemaining(Consumer<? super LayoutPath> action) {
            Objects.requireNonNull(action);
            try {
                for (; currentIndex < elementCount; currentIndex++) {
                    action.accept(new LayoutPath(currentIndex * element.size(), element, parent));
                }
            } finally {
                currentIndex = elementCount;
            }
        }

        @Override
        public long estimateSize() {
            return elementCount;
        }

        @Override
        public int characteristics() {
            return NONNULL | SUBSIZED | SIZED | IMMUTABLE | ORDERED;
        }
    }
}
