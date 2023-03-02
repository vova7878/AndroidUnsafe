package com.v7878.unsafe.memory;

import static com.v7878.unsafe.Utils.assert_;
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

    public LayoutPath groupElement(String name) {
        Objects.requireNonNull(name);
        assert_(layout instanceof GroupLayout, IllegalArgumentException::new,
                "attempting to select a group element from a non-group layout");
        GroupLayout g = (GroupLayout) layout;
        long elem_offset = 0;
        Layout elem = null;
        for (int i = 0; i < g.memberLayouts().size(); i++) {
            Layout l = g.memberLayouts().get(i);
            if (l.name().isPresent()
                    && l.name().get().equals(name)) {
                elem = l;
                break;
            } else if (g.isStruct()) {
                elem_offset += l.size();
            }
        }
        if (elem == null) {
            throw badLayoutPath("cannot resolve '" + name + "' in layout " + layout);
        }
        return new LayoutPath(offset() + elem_offset, elem, this);
    }

    public static LayoutPath rootPath(Layout layout) {
        return new LayoutPath(0, layout, null);
    }

    public static final class PathElement implements UnaryOperator<LayoutPath> {

        public enum PathKind {
            //SEQUENCE_ELEMENT("unbound sequence element"),
            //SEQUENCE_ELEMENT_INDEX("bound sequence element"),
            //SEQUENCE_RANGE("sequence range"),
            GROUP_ELEMENT("group element");

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

        /*static PathElement sequenceElement(long index) {
            if (index < 0) {
                throw new IllegalArgumentException("Index must be positive: " + index);
            }
            return new LayoutPath.PathElement(PathKind.SEQUENCE_ELEMENT_INDEX,
                    path -> path.sequenceElement(index));
        }*/
    }
}
