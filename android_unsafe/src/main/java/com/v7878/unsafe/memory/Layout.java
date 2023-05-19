package com.v7878.unsafe.memory;

import static com.v7878.unsafe.AndroidUnsafe4.ADDRESS_SIZE;
import static com.v7878.unsafe.AndroidUnsafe4.IS64BIT;
import static com.v7878.unsafe.AndroidUnsafe4.OBJECT_ALIGNMENT_SHIFT;
import static com.v7878.unsafe.AndroidUnsafe4.classSizeField;
import static com.v7878.unsafe.AndroidUnsafe4.fieldOffset;
import static com.v7878.unsafe.AndroidUnsafe4.getArrayLength;
import static com.v7878.unsafe.AndroidUnsafe4.getDeclaredFields0;
import static com.v7878.unsafe.AndroidUnsafe4.getEmbeddedVTableLength;
import static com.v7878.unsafe.AndroidUnsafe4.getInstanceFields;
import static com.v7878.unsafe.AndroidUnsafe4.isCompressedString;
import static com.v7878.unsafe.AndroidUnsafe4.objectSizeField;
import static com.v7878.unsafe.AndroidUnsafe4.shouldHaveEmbeddedVTableAndImt;
import static com.v7878.unsafe.Checks.checkSize;
import static com.v7878.unsafe.Utils.assert_;
import static com.v7878.unsafe.Utils.is32Bit;
import static com.v7878.unsafe.Utils.isAlignedL;
import static com.v7878.unsafe.Utils.isPowerOfTwoUL;
import static com.v7878.unsafe.Utils.log2;
import static com.v7878.unsafe.Utils.roundUpL;

import com.v7878.unsafe.memory.LayoutPath.PathElement;

import java.lang.reflect.Field;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public abstract class Layout implements Bindable<MemorySegment> {

    static void requireValidAlignment(long alignment) {
        if (!isPowerOfTwoUL(alignment)
                || !(IS64BIT || is32Bit(alignment))) {
            throw new IllegalArgumentException(
                    "illegal alignment: " + alignment);
        }
    }

    static void requireValidAlignmentShift(int align_shift) {
        if ((align_shift < 0) || (align_shift >= (ADDRESS_SIZE * 8))) {
            throw new IllegalArgumentException(
                    "Invalid alignment shift: " + align_shift);
        }
    }

    static void requireValidSize(long size, boolean includeZero) {
        if (!checkSize(size) || (!includeZero && size == 0)) {
            throw new IllegalArgumentException("Invalid size: " + size);
        }
    }

    static void requireValidSize(long size) {
        requireValidSize(size, false);
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
        return this instanceof RawLayout.Padding;
    }

    public final boolean isValue() {
        return this instanceof ValueLayout;
    }

    abstract Layout dup(int align_shift, Optional<String> name);

    public Layout withName(String name) {
        Objects.requireNonNull(name);
        return dup(alignShift, Optional.of(name));
    }

    public Layout withAlignmentShift(int align_shift) {
        requireValidAlignmentShift(align_shift);
        return dup(align_shift, name);
    }

    public Layout withAlignment(long alignment) {
        requireValidAlignment(alignment);
        return withAlignmentShift(log2(alignment));
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
            //TODO: check big alignments
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

    private static <T> T computePathOp(LayoutPath path,
                                       Function<LayoutPath, T> finalizer, PathElement... elements) {
        Objects.requireNonNull(elements);
        for (PathElement e : elements) {
            path = e.apply(path);
        }
        return finalizer.apply(path);
    }

    public final Layout select(PathElement... elements) {
        return computePathOp(LayoutPath.rootPath(this), LayoutPath::layout, elements);
    }

    public final LayoutPath selectPath(PathElement... elements) {
        return computePathOp(LayoutPath.rootPath(this), obj -> obj, elements);
    }

    public Spliterator<LayoutPath> spliterator() {
        return LayoutPath.rootPath(this).spliterator();
    }

    public Stream<LayoutPath> elements() {
        return StreamSupport.stream(spliterator(), false);
    }

    public static RawLayout rawLayout(long size) {
        Layout.requireValidSize(size, true);
        return new RawLayout(size);
    }

    static Layout paddingLayout(long size) {
        Layout.requireValidSize(size, false);
        return new RawLayout.Padding(size);
    }

    public static ValueLayout valueLayout(Class<?> carrier) {
        return valueLayout(carrier, ByteOrder.nativeOrder());
    }

    public static ValueLayout valueLayout(Class<?> carrier, ByteOrder order) {
        Objects.requireNonNull(carrier);
        Objects.requireNonNull(order);
        if (carrier == boolean.class) {
            return new ValueLayout.OfBoolean(order);
        } else if (carrier == byte.class) {
            return new ValueLayout.OfByte(order);
        } else if (carrier == char.class) {
            return new ValueLayout.OfChar(order);
        } else if (carrier == short.class) {
            return new ValueLayout.OfShort(order);
        } else if (carrier == int.class) {
            return new ValueLayout.OfInt(order);
        } else if (carrier == float.class) {
            return new ValueLayout.OfFloat(order);
        } else if (carrier == long.class) {
            return new ValueLayout.OfLong(order);
        } else if (carrier == double.class) {
            return new ValueLayout.OfDouble(order);
        } else if (carrier == Addressable.class) {
            return ValueLayout.ADDRESS.withOrder(order);
        } else if (carrier == Word.class) {
            return new ValueLayout.OfWord(order);
        } else if (carrier == Object.class) {
            return new ValueLayout.OfObject(order);
        } else {
            throw new IllegalArgumentException("Unsupported carrier: " + carrier.getName());
        }
    }

    //TODO array copy
    public static GroupLayout unionLayout(boolean fill_to_alignment, Layout... elements) {
        Objects.requireNonNull(elements);
        assert_(elements.length != 0, IllegalArgumentException::new);
        int align_shift = 0;
        long size = 0;
        for (Layout element : elements) {
            align_shift = Math.max(align_shift, element.alignmentShift());
            size = Math.max(size, element.size());
        }
        if (fill_to_alignment) {
            size = roundUpL(size, 1 << align_shift);
        }
        requireValidSize(size);
        return new GroupLayout(GroupLayout.Kind.UNION,
                Stream.of(elements)
                        .collect(Collectors.toList()), size, align_shift);
    }

    public static GroupLayout unionLayout(Layout... elements) {
        return unionLayout(true, elements);
    }

    public static GroupLayout fullStructLayout(Layout... elements) {
        Objects.requireNonNull(elements);
        assert_(elements.length != 0, IllegalArgumentException::new);
        int align_shift = 0;
        long size = 0;
        for (Layout element : elements) {
            align_shift = Math.max(align_shift, element.alignmentShift());
            assert_(isAlignedL(size, element.alignment()),
                    IllegalArgumentException::new,
                    "Incompatible alignment constraints");
            size = Math.addExact(size, element.size());
        }
        requireValidSize(size);
        assert_(isAlignedL(size, 1 << align_shift),
                IllegalArgumentException::new,
                "Incompatible alignment constraints");
        return new GroupLayout(GroupLayout.Kind.STRUCT,
                Stream.of(elements)
                        .collect(Collectors.toList()), size, align_shift);
    }

    public static GroupLayout structLayout(boolean fill_to_alignment,
                                           int min_align_shift, Layout... elements) {
        Objects.requireNonNull(elements);
        assert_(elements.length != 0, IllegalArgumentException::new);
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
            size = Math.addExact(new_size, element.size());
        }
        if (min_align_shift != -1) {
            requireValidAlignmentShift(min_align_shift);
            align_shift = Math.max(align_shift, min_align_shift);
        }
        if (fill_to_alignment) {
            long new_size = roundUpL(size, 1 << align_shift);
            if (new_size != size) {
                out_list.add(paddingLayout(new_size - size));
            }
            size = new_size;
        }
        requireValidSize(size);
        return new GroupLayout(GroupLayout.Kind.STRUCT,
                out_list, size, align_shift);
    }

    public static GroupLayout structLayout(boolean fill_to_alignment, Layout... elements) {
        return structLayout(fill_to_alignment, -1, elements);
    }

    public static GroupLayout structLayout(Layout... elements) {
        return structLayout(true, -1, elements);
    }

    public static SequenceLayout sequenceLayout(long elementCount, Layout elementLayout) {
        assert_(elementLayout.hasAlignedSize(), IllegalArgumentException::new);
        requireValidSize(Math.multiplyExact(elementCount, elementLayout.size()), true);
        return new SequenceLayout(elementCount, elementLayout);
    }

    private static Layout[] fieldsToLayouts(Field[] fields) {
        Arrays.sort(fields, (a, b) -> {
            return Integer.compare(fieldOffset(a), fieldOffset(b));
        });
        ValueLayout[] out = new ValueLayout[fields.length];
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            Class<?> ft = field.getType();
            out[i] = Layout.valueLayout(ft.isPrimitive() ? ft : Object.class)
                    .withName(field.getDeclaringClass().getName() + "." + field.getName());
        }
        return out;
    }

    private static GroupLayout getLayoutForFields(Field[] fields, int full_size) {
        GroupLayout out = Layout.structLayout(false,
                OBJECT_ALIGNMENT_SHIFT, fieldsToLayouts(fields));

        //checks
        assert_(full_size == -1 || out.size() == full_size, IllegalStateException::new);
        long offset = 0;
        int index = 0;
        for (Layout tmp : out.memberLayouts()) {
            if (!tmp.isPadding()) {
                assert_(offset == fieldOffset(fields[index]), IllegalStateException::new);
                index++;
            }
            offset += tmp.size();
        }
        assert_(index == fields.length, IllegalStateException::new);
        return out;
    }

    public static Layout getClassLayout(Class<?> clazz) {
        Objects.requireNonNull(clazz);
        assert_((clazz != Class.class) && (clazz != String.class) && (!clazz.isArray()),
                IllegalArgumentException::new);
        if (clazz.isPrimitive()) {
            return Layout.valueLayout(clazz).withName(clazz.getName());
        }
        Field[] ifields = getInstanceFields(clazz);
        return getLayoutForFields(ifields, objectSizeField(clazz))
                .withName(clazz.getName());
    }

    public static Layout getInstanceLayout(Object obj) {
        Objects.requireNonNull(obj);
        if (obj instanceof Class) {
            ArrayList<Layout> out = new ArrayList();
            out.addAll(Arrays.asList(fieldsToLayouts(getInstanceFields(Class.class))));
            Class<?> clazz = (Class<?>) obj;
            if (shouldHaveEmbeddedVTableAndImt(clazz)) {
                out.add(ValueLayout.JAVA_INT.withName("embedded_vtable_length_"));
                out.add(ValueLayout.ADDRESS.withName("embedded_imtable_"));
                int vtable_length = getEmbeddedVTableLength(clazz);
                out.add(Layout.sequenceLayout(vtable_length,
                        ValueLayout.ADDRESS).withName("embedded_vtable_"));
            }
            out.addAll(Arrays.asList(fieldsToLayouts(getDeclaredFields0(clazz, true))));
            Layout tmp = Layout.structLayout(false,
                    OBJECT_ALIGNMENT_SHIFT,
                    out.stream().toArray(Layout[]::new));
            assert_(classSizeField(clazz) == tmp.size(), IllegalStateException::new);
            //TODO: more checks
            return tmp.withName(clazz.getName() + ".class");
        }
        if (obj instanceof String) {
            ArrayList<Layout> out = new ArrayList();
            out.addAll(Arrays.asList(fieldsToLayouts(getInstanceFields(String.class))));
            String sobj = (String) obj;
            int length = sobj.length();
            Class<?> component = isCompressedString(sobj) ? byte.class : short.class;
            Layout chars = sequenceLayout(length, valueLayout(component));
            out.add(chars.withName("value")); // classical name
            return structLayout(true, OBJECT_ALIGNMENT_SHIFT,
                    out.stream().toArray(Layout[]::new))
                    .withName(String.class.getName());
        }
        @SuppressWarnings("null")
        Class<?> clazz = obj.getClass();
        if (clazz.isArray()) {
            ArrayList<Layout> out = new ArrayList();
            out.addAll(Arrays.asList(fieldsToLayouts(getInstanceFields(Object.class))));
            out.add(ValueLayout.JAVA_INT.withName("length"));
            int length = getArrayLength(obj);
            Class<?> component = obj.getClass().getComponentType();
            Layout elements = sequenceLayout(length,
                    valueLayout(component.isPrimitive() ? component : Object.class));
            out.add(elements.withName("data"));
            return structLayout(false, out.stream().toArray(Layout[]::new))
                    .withAlignmentShift(OBJECT_ALIGNMENT_SHIFT)
                    .withName(clazz.getName());
        }
        return getClassLayout(clazz);
    }

    public final MemorySegment allocateNative() {
        Pointer p = Pointer.allocateNative(size(), alignment());
        return new MemorySegment(p, this, false);
    }

    public final MemorySegment allocateHeap() {
        long alignment = alignment();
        if (size > Integer.MAX_VALUE || alignment > Integer.MAX_VALUE) {
            throw new IllegalArgumentException();
        }
        Pointer p = Pointer.allocateHeap((int) size, (int) alignment);
        return new MemorySegment(p, this, false);
    }

    public final MemorySegment bind(Addressable a, boolean ignore_alignment) {
        return new MemorySegment(a.pointer(), this, ignore_alignment);
    }

    @Override
    public final MemorySegment bind(Addressable a) {
        return bind(a, false);
    }
}
