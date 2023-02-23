package com.v7878;

import static com.v7878.AndroidUnsafe4.*;
import static com.v7878.Utils.*;
import java.lang.reflect.Field;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Memory {

    public static class Pointer {

        private final Object base;
        private final long base_address;
        private final long offset;
        private final int alignShift;

        public Pointer(long address) {
            this(null, address);
        }

        public Pointer(Object data) {
            this(data, 0);
        }

        public Pointer(Object base, long offset) {
            this.base = base;
            if (base == null) {
                assert_(checkNativeAddress(offset), IllegalArgumentException::new);
                this.alignShift = Long.numberOfTrailingZeros(offset);
                this.base_address = offset;
                this.offset = 0;
            } else {
                assert_(checkOffset(offset), IllegalArgumentException::new);
                this.offset = offset;
                long address = 0;
                try {
                    address = addressOfNonMovableArray(base);
                } catch (Throwable th) {
                }
                int align_shift;
                if (address != 0) {
                    long raw_address = address + offset;
                    assert_(Long.compareUnsigned(raw_address, address) >= 0,
                            IllegalArgumentException::new);
                    assert_(checkNativeAddress(raw_address), IllegalArgumentException::new);
                    align_shift = Long.numberOfTrailingZeros(raw_address);
                } else {
                    align_shift = OBJECT_ALIGNMENT_SHIFT;
                }
                this.base_address = address;
                this.alignShift = align_shift;
            }
        }

        public Object getBase() {
            return base;
        }

        public boolean isNative() {
            return base == null;
        }

        public boolean isRaw() {
            return (base == null) || (base_address != 0);
        }

        public long getRawAddress() {
            assert_(isRaw(), UnsupportedOperationException::new);
            return base_address + offset;
        }

        public long getOffset() {
            return base == null ? base_address + offset : offset;
        }

        public int getAlignmentShift() {
            return alignShift;
        }

        public boolean checkAlignmentShift(int shift) {
            return shift <= alignShift;
        }

        @Override
        public String toString() {
            StringBuilder out = new StringBuilder();
            if (isNative()) {
                out.append("native");
            } else {
                if (base_address != 0) {
                    out.append("non-movable ");
                }
                out.append("object");
            }
            out.append(" pointer ");
            if (base_address != 0) {
                out.append("0x");
                out.append(Long.toHexString(base_address));
                out.append(" ");
            }
            out.append("+");
            out.append(offset);
            out.append(" %");
            out.append(alignShift);
            return out.toString();
        }
    }

    public static abstract class Layout {

        static void checkAlignment(long alignment) {
            assert_(Utils.isPowerOfTwoL(alignment)
                    && (IS64BIT || is32BitClean(alignment)),
                    IllegalArgumentException::new,
                    "Invalid alignment: " + alignment);
        }

        static void checkAlignmentShift(int align_shift) {
            assert_((align_shift >= 0) && (align_shift < (ADDRESS_SIZE * 8)),
                    IllegalArgumentException::new,
                    "Invalid alignment shift: " + align_shift);
        }

        static void checkSize(long size, boolean includeZero) {
            assert_(Utils.checkSize(size) || (includeZero && size == 0),
                    IllegalArgumentException::new,
                    "Invalid size for layout: " + size);
        }

        static void checkSize(long size) {
            checkSize(size, false);
        }

        private final long size;
        private final int alignShift;
        private final Optional<String> name;

        Layout(long size, int align_shift, Optional<String> name) {
            this.size = size;
            this.alignShift = align_shift;
            this.name = name;
        }

        public final Optional<String> name() {
            return name;
        }

        public final long size() {
            return size;
        }

        public final int alignmentShift() {
            return alignShift;
        }

        public final long alignment() {
            return 1L << alignShift;
        }

        public final boolean isPadding() {
            return this instanceof PaddingLayout;
        }

        abstract Layout dup(int align_shift, Optional<String> name);

        public Layout withName(String name) {
            Objects.requireNonNull(name);
            return dup(alignShift, Optional.of(name));
        }

        public Layout withAlignmentShift(int align_shift) {
            checkAlignmentShift(align_shift);
            return dup(align_shift, name);
        }

        public Layout withAlignment(long alignment) {
            checkAlignment(alignment);
            return withAlignmentShift(Utils.log2(alignment));
        }

        public boolean hasNaturalAlignment() {
            return size == alignment();
        }

        public final boolean hasAlignedSize() {
            return isAlignedL(size(), alignment());
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, size, alignShift);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof Layout)) {
                return false;
            }
            Layout otherLayout = (Layout) other;
            return name.equals(otherLayout.name)
                    && size == otherLayout.size
                    && alignShift == otherLayout.alignShift;
        }

        String decorateLayoutString(String s) {
            s = String.format("%s%d", s, size());
            if (!hasAlignedSize()) {
                s = String.format("%s+%d", s,
                        roundUpL(size(), alignment()) - size());
            }
            if (!hasNaturalAlignment()) {
                s = String.format("%s%%%d", s, alignShift);
            }
            if (name().isPresent()) {
                s = String.format("%s(%s)", s, name().get());
            }
            return s;
        }

        @Override
        public abstract String toString();

        public static Layout paddingLayout(long size) {
            checkSize(size);
            return new PaddingLayout(size);
        }

        public static ValueLayout valueLayout(Class<?> carrier) {
            return valueLayout(carrier, ByteOrder.nativeOrder());
        }

        public static ValueLayout valueLayout(Class<?> carrier, ByteOrder order) {
            Objects.requireNonNull(carrier);
            Objects.requireNonNull(order);
            if (carrier == boolean.class) {
                return new OfBoolean(order);
            } else if (carrier == byte.class) {
                return new OfByte(order);
            } else if (carrier == char.class) {
                return new OfChar(order);
            } else if (carrier == short.class) {
                return new OfShort(order);
            } else if (carrier == int.class) {
                return new OfInt(order);
            } else if (carrier == float.class) {
                return new OfFloat(order);
            } else if (carrier == long.class) {
                return new OfLong(order);
            } else if (carrier == double.class) {
                return new OfDouble(order);
            } else if (carrier == Pointer.class) {
                return new OfAddress(order);
            } else if (carrier == Object.class) {
                return new OfObject(order);
            } else {
                throw new IllegalArgumentException("Unsupported carrier: " + carrier.getName());
            }
        }

        //TODO array copy
        public static GroupLayout unionLayout(boolean fill_to_alignment, Layout... elements) {
            Objects.requireNonNull(elements);
            int align_shift = 0;
            long size = 0;
            for (Layout element : elements) {
                align_shift = Math.max(align_shift, element.alignmentShift());
                size = Math.max(size, element.size());
            }
            if (fill_to_alignment) {
                size = roundUpL(size, 1 << align_shift);
                checkSize(size, true);
            }
            return new GroupLayout(GroupLayout.Kind.UNION,
                    Stream.of(elements)
                            .collect(Collectors.toList()), size, align_shift);
        }

        public static GroupLayout unionLayout(Layout... elements) {
            return unionLayout(true, elements);
        }

        public static GroupLayout fullStructLayout(Layout... elements) {
            Objects.requireNonNull(elements);
            int align_shift = 0;
            long size = 0;
            for (Layout element : elements) {
                align_shift = Math.max(align_shift, element.alignmentShift());
                assert_(isAlignedL(size, element.alignment()),
                        IllegalArgumentException::new,
                        "Incompatible alignment constraints");
                size += element.size();
                checkSize(size, true);
            }
            assert_(isAlignedL(size, 1 << align_shift),
                    IllegalArgumentException::new,
                    "Incompatible alignment constraints");
            return new GroupLayout(GroupLayout.Kind.STRUCT,
                    Stream.of(elements)
                            .collect(Collectors.toList()), size, align_shift);
        }

        public static GroupLayout structLayout(boolean fill_to_alignment, Layout... elements) {
            Objects.requireNonNull(elements);
            ArrayList<Layout> out_list = new ArrayList<>(elements.length);
            int align_shift = 0;
            long size = 0;
            for (Layout element : elements) {
                align_shift = Math.max(align_shift, element.alignmentShift());
                long new_size = roundUpL(size, element.alignment());
                if (new_size != size) {
                    out_list.add(paddingLayout(new_size - size));
                }
                out_list.add(element);
                size = new_size + element.size();
                checkSize(size, true);
            }
            if (fill_to_alignment) {
                long new_size = roundUpL(size, 1 << align_shift);
                if (new_size != size) {
                    out_list.add(paddingLayout(new_size - size));
                }
                size = new_size;
                checkSize(size, true);
            }
            return new GroupLayout(GroupLayout.Kind.STRUCT,
                    out_list, size, align_shift);
        }

        public static GroupLayout structLayout(Layout... elements) {
            return structLayout(true, elements);
        }
    }

    public static class PaddingLayout extends Layout {

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

    public abstract static class ValueLayout extends Layout {

        public static final OfByte JAVA_BYTE = new OfByte(ByteOrder.nativeOrder());
        public static final OfBoolean JAVA_BOOLEAN = new OfBoolean(ByteOrder.nativeOrder());
        public static final OfChar JAVA_CHAR = new OfChar(ByteOrder.nativeOrder());
        public static final OfShort JAVA_SHORT = new OfShort(ByteOrder.nativeOrder());
        public static final OfInt JAVA_INT = new OfInt(ByteOrder.nativeOrder());
        public static final OfLong JAVA_LONG = new OfLong(ByteOrder.nativeOrder());
        public static final OfFloat JAVA_FLOAT = new OfFloat(ByteOrder.nativeOrder());
        public static final OfDouble JAVA_DOUBLE = new OfDouble(ByteOrder.nativeOrder());
        public static final OfObject JAVA_OBJECT = new OfObject(ByteOrder.nativeOrder());
        public static final OfAddress ADDRESS = new OfAddress(ByteOrder.nativeOrder());

        private final ByteOrder order;

        ValueLayout(long size, int align_shift, ByteOrder order) {
            this(size, align_shift, order, Optional.empty());
        }

        ValueLayout(long size, int align_shift, ByteOrder order, Optional<String> name) {
            super(size, align_shift, name);
            this.order = order;
        }

        public final ByteOrder order() {
            return order;
        }

        public abstract Class<?> carrier();

        abstract ValueLayout dup(int align_shift, ByteOrder order, Optional<String> name);

        @Override
        ValueLayout dup(int align_shift, Optional<String> name) {
            return dup(align_shift, order, name);
        }

        @Override
        public ValueLayout withName(String name) {
            return (ValueLayout) super.withName(name);
        }

        @Override
        public ValueLayout withAlignmentShift(int align_shift) {
            return (ValueLayout) super.withAlignmentShift(align_shift);
        }

        @Override
        public ValueLayout withAlignment(long alignment) {
            return (ValueLayout) super.withAlignment(alignment);
        }

        public ValueLayout withOrder(ByteOrder order) {
            return dup(alignmentShift(), Objects.requireNonNull(order), name());
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), order, carrier());
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!super.equals(other)) {
                return false;
            }
            if (!(other instanceof ValueLayout)) {
                return false;
            }
            ValueLayout otherLayout = (ValueLayout) other;
            return order.equals(otherLayout.order)
                    && carrier() == otherLayout.carrier();
        }

        String toString(char descriptor) {
            if (order == ByteOrder.LITTLE_ENDIAN) {
                descriptor = Character.toLowerCase(descriptor);
            }
            return decorateLayoutString(Character.toString(descriptor));
        }
    }

    public static final class OfBoolean extends ValueLayout {

        OfBoolean(ByteOrder order) {
            super(1, 0, order);
        }

        OfBoolean(int align_shift, ByteOrder order, Optional<String> name) {
            super(1, align_shift, order, name);
        }

        @Override
        public Class<?> carrier() {
            return Boolean.TYPE;
        }

        @Override
        OfBoolean dup(int align_shift, ByteOrder order, Optional<String> name) {
            return new OfBoolean(align_shift, order, name);
        }

        @Override
        public OfBoolean withName(String name) {
            return (OfBoolean) super.withName(name);
        }

        @Override
        public OfBoolean withAlignmentShift(int align_shift) {
            return (OfBoolean) super.withAlignmentShift(align_shift);
        }

        @Override
        public OfBoolean withAlignment(long alignment) {
            return (OfBoolean) super.withAlignment(alignment);
        }

        @Override
        public OfBoolean withOrder(ByteOrder order) {
            return (OfBoolean) super.withOrder(order);
        }

        @Override
        public String toString() {
            return super.toString('Z');
        }
    }

    public static final class OfByte extends ValueLayout {

        OfByte(ByteOrder order) {
            super(1, 0, order);
        }

        OfByte(int align_shift, ByteOrder order, Optional<String> name) {
            super(1, align_shift, order, name);
        }

        @Override
        public Class<?> carrier() {
            return Byte.TYPE;
        }

        @Override
        OfByte dup(int align_shift, ByteOrder order, Optional<String> name) {
            return new OfByte(align_shift, order, name);
        }

        @Override
        public OfByte withName(String name) {
            return (OfByte) super.withName(name);
        }

        @Override
        public OfByte withAlignmentShift(int align_shift) {
            return (OfByte) super.withAlignmentShift(align_shift);
        }

        @Override
        public OfByte withAlignment(long alignment) {
            return (OfByte) super.withAlignment(alignment);
        }

        @Override
        public OfByte withOrder(ByteOrder order) {
            return (OfByte) super.withOrder(order);
        }

        @Override
        public String toString() {
            return super.toString('B');
        }
    }

    public static final class OfChar extends ValueLayout {

        OfChar(ByteOrder order) {
            super(2, 1, order);
        }

        OfChar(int align_shift, ByteOrder order, Optional<String> name) {
            super(2, align_shift, order, name);
        }

        @Override
        public Class<?> carrier() {
            return Character.TYPE;
        }

        @Override
        OfChar dup(int align_shift, ByteOrder order, Optional<String> name) {
            return new OfChar(align_shift, order, name);
        }

        @Override
        public OfChar withName(String name) {
            return (OfChar) super.withName(name);
        }

        @Override
        public OfChar withAlignmentShift(int align_shift) {
            return (OfChar) super.withAlignmentShift(align_shift);
        }

        @Override
        public OfChar withAlignment(long alignment) {
            return (OfChar) super.withAlignment(alignment);
        }

        @Override
        public OfChar withOrder(ByteOrder order) {
            return (OfChar) super.withOrder(order);
        }

        @Override
        public String toString() {
            return super.toString('C');
        }
    }

    public static final class OfShort extends ValueLayout {

        OfShort(ByteOrder order) {
            super(2, 1, order);
        }

        OfShort(int align_shift, ByteOrder order, Optional<String> name) {
            super(2, align_shift, order, name);
        }

        @Override
        public Class<?> carrier() {
            return Short.TYPE;
        }

        @Override
        OfShort dup(int align_shift, ByteOrder order, Optional<String> name) {
            return new OfShort(align_shift, order, name);
        }

        @Override
        public OfShort withName(String name) {
            return (OfShort) super.withName(name);
        }

        @Override
        public OfShort withAlignmentShift(int align_shift) {
            return (OfShort) super.withAlignmentShift(align_shift);
        }

        @Override
        public OfShort withAlignment(long alignment) {
            return (OfShort) super.withAlignment(alignment);
        }

        @Override
        public OfShort withOrder(ByteOrder order) {
            return (OfShort) super.withOrder(order);
        }

        @Override
        public String toString() {
            return super.toString('S');
        }
    }

    public static final class OfInt extends ValueLayout {

        OfInt(ByteOrder order) {
            super(4, 2, order);
        }

        OfInt(int align_shift, ByteOrder order, Optional<String> name) {
            super(4, align_shift, order, name);
        }

        @Override
        public Class<?> carrier() {
            return Integer.TYPE;
        }

        @Override
        OfInt dup(int align_shift, ByteOrder order, Optional<String> name) {
            return new OfInt(align_shift, order, name);
        }

        @Override
        public OfInt withName(String name) {
            return (OfInt) super.withName(name);
        }

        @Override
        public OfInt withAlignmentShift(int align_shift) {
            return (OfInt) super.withAlignmentShift(align_shift);
        }

        @Override
        public OfInt withAlignment(long alignment) {
            return (OfInt) super.withAlignment(alignment);
        }

        @Override
        public OfInt withOrder(ByteOrder order) {
            return (OfInt) super.withOrder(order);
        }

        @Override
        public String toString() {
            return super.toString('I');
        }
    }

    public static final class OfFloat extends ValueLayout {

        OfFloat(ByteOrder order) {
            super(4, 2, order);
        }

        OfFloat(int align_shift, ByteOrder order, Optional<String> name) {
            super(4, align_shift, order, name);
        }

        @Override
        public Class<?> carrier() {
            return Float.TYPE;
        }

        @Override
        OfFloat dup(int align_shift, ByteOrder order, Optional<String> name) {
            return new OfFloat(align_shift, order, name);
        }

        @Override
        public OfFloat withName(String name) {
            return (OfFloat) super.withName(name);
        }

        @Override
        public OfFloat withAlignmentShift(int align_shift) {
            return (OfFloat) super.withAlignmentShift(align_shift);
        }

        @Override
        public OfFloat withAlignment(long alignment) {
            return (OfFloat) super.withAlignment(alignment);
        }

        @Override
        public OfFloat withOrder(ByteOrder order) {
            return (OfFloat) super.withOrder(order);
        }

        @Override
        public String toString() {
            return super.toString('F');
        }
    }

    public static final class OfLong extends ValueLayout {

        OfLong(ByteOrder order) {
            super(8, 3, order);
        }

        OfLong(int align_shift, ByteOrder order, Optional<String> name) {
            super(8, align_shift, order, name);
        }

        @Override
        public Class<?> carrier() {
            return Long.TYPE;
        }

        @Override
        OfLong dup(int align_shift, ByteOrder order, Optional<String> name) {
            return new OfLong(align_shift, order, name);
        }

        @Override
        public OfLong withName(String name) {
            return (OfLong) super.withName(name);
        }

        @Override
        public OfLong withAlignmentShift(int align_shift) {
            return (OfLong) super.withAlignmentShift(align_shift);
        }

        @Override
        public OfLong withAlignment(long alignment) {
            return (OfLong) super.withAlignment(alignment);
        }

        @Override
        public OfLong withOrder(ByteOrder order) {
            return (OfLong) super.withOrder(order);
        }

        @Override
        public String toString() {
            return super.toString('J');
        }
    }

    public static final class OfDouble extends ValueLayout {

        OfDouble(ByteOrder order) {
            super(8, 3, order);
        }

        OfDouble(int align_shift, ByteOrder order, Optional<String> name) {
            super(8, align_shift, order, name);
        }

        @Override
        public Class<?> carrier() {
            return Double.TYPE;
        }

        @Override
        OfDouble dup(int align_shift, ByteOrder order, Optional<String> name) {
            return new OfDouble(align_shift, order, name);
        }

        @Override
        public OfDouble withName(String name) {
            return (OfDouble) super.withName(name);
        }

        @Override
        public OfDouble withAlignmentShift(int align_shift) {
            return (OfDouble) super.withAlignmentShift(align_shift);
        }

        @Override
        public OfDouble withAlignment(long alignment) {
            return (OfDouble) super.withAlignment(alignment);
        }

        @Override
        public OfDouble withOrder(ByteOrder order) {
            return (OfDouble) super.withOrder(order);
        }

        @Override
        public String toString() {
            return super.toString('D');
        }
    }

    public static final class OfObject extends ValueLayout {

        OfObject(ByteOrder order) {
            super(4, 2, order);
        }

        OfObject(int align_shift, ByteOrder order, Optional<String> name) {
            super(4, align_shift, order, name);
        }

        @Override
        public Class<?> carrier() {
            return Object.class;
        }

        @Override
        OfObject dup(int align_shift, ByteOrder order, Optional<String> name) {
            return new OfObject(align_shift, order, name);
        }

        @Override
        public OfObject withName(String name) {
            return (OfObject) super.withName(name);
        }

        @Override
        public OfObject withAlignmentShift(int align_shift) {
            return (OfObject) super.withAlignmentShift(align_shift);
        }

        @Override
        public OfObject withAlignment(long alignment) {
            return (OfObject) super.withAlignment(alignment);
        }

        @Override
        public OfObject withOrder(ByteOrder order) {
            return (OfObject) super.withOrder(order);
        }

        @Override
        public String toString() {
            return super.toString('L');
        }
    }

    public static final class OfAddress extends ValueLayout {

        OfAddress(ByteOrder order) {
            super(ADDRESS_SIZE, log2(ADDRESS_SIZE), order);
        }

        OfAddress(int align_shift, ByteOrder order, Optional<String> name) {
            super(ADDRESS_SIZE, align_shift, order, name);
        }

        @Override
        public Class<?> carrier() {
            return Pointer.class;
        }

        @Override
        OfAddress dup(int align_shift, ByteOrder order, Optional<String> name) {
            return new OfAddress(align_shift, order, name);
        }

        @Override
        public OfAddress withName(String name) {
            return (OfAddress) super.withName(name);
        }

        @Override
        public OfAddress withAlignmentShift(int align_shift) {
            return (OfAddress) super.withAlignmentShift(align_shift);
        }

        @Override
        public OfAddress withAlignment(long alignment) {
            return (OfAddress) super.withAlignment(alignment);
        }

        @Override
        public OfAddress withOrder(ByteOrder order) {
            return (OfAddress) super.withOrder(order);
        }

        @Override
        public String toString() {
            return super.toString('A');
        }
    }

    public static class GroupLayout extends Layout {

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

    public static GroupLayout getClassLayout(Class<?> clazz) {
        assert_((clazz != Class.class) && (clazz != String.class) && (!clazz.isArray()),
                IllegalArgumentException::new);
        Field[] ifields = getInstanceFields(clazz);
        Arrays.sort(ifields, (a, b) -> {
            return Integer.compare(fieldOffset(a), fieldOffset(b));
        });
        ValueLayout[] vls = new ValueLayout[ifields.length];
        for (int i = 0; i < ifields.length; i++) {
            Field ifield = ifields[i];
            Class<?> ft = ifield.getType();
            vls[i] = Layout.valueLayout(ft.isPrimitive() ? ft : Object.class)
                    .withName(ifield.getDeclaringClass().getName() + "." + ifield.getName());
        }
        return Layout.structLayout(false, vls).withName(clazz.getName()).withAlignmentShift(OBJECT_ALIGNMENT_SHIFT);
    }
}
