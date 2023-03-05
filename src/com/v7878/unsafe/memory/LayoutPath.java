package com.v7878.unsafe.memory;

import static com.v7878.unsafe.Utils.assert_;
import java.util.List;
import java.util.Objects;
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
}
