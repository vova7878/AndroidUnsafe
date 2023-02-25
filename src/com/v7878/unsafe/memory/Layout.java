package com.v7878.unsafe.memory;

import static com.v7878.unsafe.AndroidUnsafe4.*;
import com.v7878.unsafe.Utils;
import static com.v7878.unsafe.Utils.*;
import java.lang.reflect.Field;
import java.nio.ByteOrder;
import java.util.*;
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
        assert_(Utils.checkSize(size) || (includeZero && size == 0),
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

    public static GroupLayout structLayout(boolean fill_to_alignment, Layout... elements) {
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

    public static GroupLayout structLayout(Layout... elements) {
        return structLayout(true, elements);
    }

    public static Layout getClassLayout(Class<?> clazz) {
        assert_((clazz != Class.class) && (clazz != String.class) && (!clazz.isArray()),
                IllegalArgumentException::new);
        if (clazz.isPrimitive()) {
            return Layout.valueLayout(clazz).withName(clazz.getName());
        }
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

    public final MemorySegment allocateNative() {
        Pointer p = Pointer.allocateNative(size(), alignment());
        return new MemorySegment(p, this);
    }

    public final MemorySegment allocateHeap() {
        long alignment = alignment();
        if (size > Integer.MAX_VALUE || alignment > Integer.MAX_VALUE) {
            throw new IllegalStateException();
        }
        Pointer p = Pointer.allocateHeap((int) size, (int) alignment);
        return new MemorySegment(p, this);
    }
}
