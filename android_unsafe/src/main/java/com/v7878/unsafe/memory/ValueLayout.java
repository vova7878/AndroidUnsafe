package com.v7878.unsafe.memory;

import static com.v7878.unsafe.AndroidUnsafe4.ADDRESS_SIZE;
import static com.v7878.unsafe.AndroidUnsafe4.OBJECT_FIELD_SIZE_SHIFT;
import static com.v7878.unsafe.Utils.assert_;
import static com.v7878.unsafe.Utils.log2;

import java.nio.ByteOrder;
import java.util.Objects;
import java.util.Optional;

public abstract class ValueLayout extends Layout {

    public static final OfByte JAVA_BYTE = new OfByte(ByteOrder.nativeOrder());
    public static final OfBoolean JAVA_BOOLEAN = new OfBoolean(ByteOrder.nativeOrder());
    public static final OfChar JAVA_CHAR = new OfChar(ByteOrder.nativeOrder());
    public static final OfShort JAVA_SHORT = new OfShort(ByteOrder.nativeOrder());
    public static final OfInt JAVA_INT = new OfInt(ByteOrder.nativeOrder());
    public static final OfLong JAVA_LONG = new OfLong(ByteOrder.nativeOrder());
    public static final OfFloat JAVA_FLOAT = new OfFloat(ByteOrder.nativeOrder());
    public static final OfDouble JAVA_DOUBLE = new OfDouble(ByteOrder.nativeOrder());
    public static final OfObject JAVA_OBJECT = new OfObject(ByteOrder.nativeOrder());
    public static final OfAddress<Pointer> ADDRESS = new OfAddress<>(ByteOrder.nativeOrder(), POINTER);
    public static final OfWord WORD = new OfWord(ByteOrder.nativeOrder());

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

        private static ByteOrder checkOrder(ByteOrder order) {
            assert_(order == ByteOrder.nativeOrder(), IllegalArgumentException::new);
            return order;
        }

        private static int checkAlignmentShift(int align_shift) {
            assert_(align_shift >= OBJECT_FIELD_SIZE_SHIFT, IllegalArgumentException::new);
            return align_shift;
        }

        OfObject(ByteOrder order) {
            super(4, 2, checkOrder(order));
        }

        OfObject(int align_shift, ByteOrder order, Optional<String> name) {
            super(4, checkAlignmentShift(align_shift), checkOrder(order), name);
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

    public static final class OfAddress<T> extends ValueLayout {

        private final Bindable<T> content;

        OfAddress(ByteOrder order, Bindable<T> content) {
            super(ADDRESS_SIZE, log2(ADDRESS_SIZE), order);
            this.content = content;
        }

        OfAddress(int align_shift, ByteOrder order,
                  Bindable<T> content, Optional<String> name) {
            super(ADDRESS_SIZE, align_shift, order, name);
            this.content = content;
        }

        @Override
        public Class<?> carrier() {
            return Addressable.class;
        }

        public Bindable<T> content() {
            return content;
        }

        <E> OfAddress<E> dup(int align_shift, ByteOrder order,
                             Bindable<E> content, Optional<String> name) {
            return new OfAddress<>(align_shift, order,
                    Objects.requireNonNull(content), name);
        }

        @Override
        OfAddress<T> dup(int align_shift, ByteOrder order, Optional<String> name) {
            return dup(align_shift, order, content, name);
        }

        @Override
        public OfAddress<T> withName(String name) {
            //noinspection unchecked
            return (OfAddress<T>) super.withName(name);
        }

        @Override
        public OfAddress<T> withAlignmentShift(int align_shift) {
            //noinspection unchecked
            return (OfAddress<T>) super.withAlignmentShift(align_shift);
        }

        @Override
        public OfAddress<T> withAlignment(long alignment) {
            //noinspection unchecked
            return (OfAddress<T>) super.withAlignment(alignment);
        }

        @Override
        public OfAddress<T> withOrder(ByteOrder order) {
            //noinspection unchecked
            return (OfAddress<T>) super.withOrder(order);
        }

        public <E> OfAddress<E> withContent(Bindable<E> content) {
            return dup(alignmentShift(), order(), content, name());
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), content());
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!super.equals(other)) {
                return false;
            }
            OfAddress<?> otherLayout = (OfAddress<?>) other;
            return content.equals(otherLayout.content);
        }

        @Override
        public String toString() {
            //TODO: add content
            return super.toString('A');
        }
    }

    public static final class OfWord extends ValueLayout {

        OfWord(ByteOrder order) {
            super(ADDRESS_SIZE, log2(ADDRESS_SIZE), order);
        }

        OfWord(int align_shift, ByteOrder order, Optional<String> name) {
            super(ADDRESS_SIZE, align_shift, order, name);
        }

        @Override
        public Class<?> carrier() {
            return Word.class;
        }

        @Override
        OfWord dup(int align_shift, ByteOrder order, Optional<String> name) {
            return new OfWord(align_shift, order, name);
        }

        @Override
        public OfWord withName(String name) {
            return (OfWord) super.withName(name);
        }

        @Override
        public OfWord withAlignmentShift(int align_shift) {
            return (OfWord) super.withAlignmentShift(align_shift);
        }

        @Override
        public OfWord withAlignment(long alignment) {
            return (OfWord) super.withAlignment(alignment);
        }

        @Override
        public OfWord withOrder(ByteOrder order) {
            return (OfWord) super.withOrder(order);
        }

        @Override
        public String toString() {
            return super.toString('W');
        }
    }
}
