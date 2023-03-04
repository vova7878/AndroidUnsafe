package com.v7878.unsafe.memory;

import static com.v7878.unsafe.AndroidUnsafe4.*;
import static com.v7878.unsafe.Checks.*;
import static com.v7878.unsafe.Utils.*;
import com.v7878.unsafe.memory.LayoutPath.PathElement;
import java.lang.reflect.Field;
import java.nio.ByteOrder;
import java.util.*;
import java.util.function.Function;
import java.util.stream.*;

public abstract class Layout {

    static void requireValidAlignment(long alignment) {
        assert_(isPowerOfTwoL(alignment)
                && (IS64BIT || is32BitOnly(alignment)),
                IllegalArgumentException::new,
                "Invalid alignment: " + alignment);
    }

    static void requireValidAlignmentShift(int align_shift) {
        assert_((align_shift >= 0) && (align_shift < (ADDRESS_SIZE * 8)),
                IllegalArgumentException::new,
                "Invalid alignment shift: " + align_shift);
    }

    static void requireValidSize(long size, boolean includeZero) {
        assert_(checkSize(size) || (includeZero && size == 0),
                IllegalArgumentException::new,
                "Invalid size: " + size);
    }

    static void requireValidSize(long size) {
        Layout.requireValidSize(size, false);
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

    private static <T> T computePathOp(LayoutPath path, Function<LayoutPath, T> finalizer, PathElement... elements) {
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
        } else if (carrier == Pointer.class) {
            return new ValueLayout.OfAddress(order);
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

    private static GroupLayout getLayoutForFields(Field[] fields, int full_size) {
        Arrays.sort(fields, (a, b) -> {
            return Integer.compare(fieldOffset(a), fieldOffset(b));
        });
        ValueLayout[] vls = new ValueLayout[fields.length];
        for (int i = 0; i < fields.length; i++) {
            Field ifield = fields[i];
            Class<?> ft = ifield.getType();
            vls[i] = Layout.valueLayout(ft.isPrimitive() ? ft : Object.class)
                    .withName(ifield.getDeclaringClass().getName() + "." + ifield.getName());
        }
        GroupLayout out = Layout.structLayout(false, OBJECT_ALIGNMENT_SHIFT, vls);

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

    private static Layout getClassLayout(Class<?> clazz, boolean allow_string) {
        Objects.requireNonNull(clazz);
        assert_((clazz != Class.class) && (allow_string || clazz != String.class) && (!clazz.isArray()),
                IllegalArgumentException::new);
        if (clazz.isPrimitive()) {
            return Layout.valueLayout(clazz).withName(clazz.getName());
        }
        Field[] ifields = getInstanceFields(clazz);
        return getLayoutForFields(ifields, allow_string ? -1
                : objectSizeField(clazz)).withName(clazz.getName());
    }

    public static Layout getClassLayout(Class<?> clazz) {
        return getClassLayout(clazz, false);
    }

    public static Layout getInstanceLayout(Object obj) {
        Objects.requireNonNull(obj);
        if (obj instanceof Class) {
            Class<?> clazz = (Class<?>) obj;
            ArrayList<Field> tmp = new ArrayList<>();
            tmp.addAll(Arrays.asList(getInstanceFields(Class.class)));
            tmp.addAll(Arrays.asList(getDeclaredFields0(clazz, true)));
            return getLayoutForFields(tmp.stream().toArray(Field[]::new),
                    classSizeField(clazz))
                    .withName(clazz.getName() + ".class");
        }
        if (obj instanceof String) {
            String sobj = (String) obj;
            int length = sobj.length();
            Class<?> component = isCompressedString(sobj) ? byte.class : short.class;
            Layout data = sequenceLayout(length, valueLayout(component));
            ArrayList<Layout> out = new ArrayList();
            GroupLayout object = (GroupLayout) getClassLayout(String.class, true);
            out.addAll(object.memberLayouts());
            out.add(data.withName("data"));
            return structLayout(true, OBJECT_ALIGNMENT_SHIFT,
                    out.stream().toArray(Layout[]::new))
                    .withName(String.class.getName());
        }
        Class<?> clazz = obj.getClass();
        if (clazz.isArray()) {
            int length = getArrayLength(obj);
            Class<?> component = obj.getClass().getComponentType();
            Layout data = sequenceLayout(length,
                    valueLayout(component.isPrimitive() ? component : Object.class));
            ArrayList<Layout> out = new ArrayList();
            GroupLayout object = (GroupLayout) getClassLayout(Object.class);
            out.addAll(object.memberLayouts());
            out.add(ValueLayout.JAVA_INT.withName("length"));
            out.add(data.withName("value")); // classical name
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
            throw new IllegalStateException();
        }
        Pointer p = Pointer.allocateHeap((int) size, (int) alignment);
        return new MemorySegment(p, this, false);
    }

    public final MemorySegment bind(Pointer p, boolean ignore_alignment) {
        return new MemorySegment(p, this, ignore_alignment);
    }

    public final MemorySegment bind(Pointer p) {
        return bind(p, false);
    }
}
