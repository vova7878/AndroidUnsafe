package com.v7878.unsafe.memory;

import static com.v7878.misc.Math.minUL;
import static com.v7878.unsafe.AndroidUnsafe4.ARRAY_BYTE_BASE_OFFSET;
import static com.v7878.unsafe.AndroidUnsafe4.copyMemory;
import static com.v7878.unsafe.AndroidUnsafe4.newNonMovableArrayVM;
import static com.v7878.unsafe.Utils.assert_;
import static com.v7878.unsafe.Utils.toHexString;
import static com.v7878.unsafe.memory.ValueLayout.JAVA_BYTE;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

//TODO: size checks
//TODO: copy method
public class MemorySegment implements Addressable {

    private final Pointer pointer;
    private final Layout layout;

    //infinity segment
    public MemorySegment(Pointer pointer) {
        Objects.requireNonNull(pointer);
        this.pointer = pointer;
        if (pointer.hasRawAddress()) {
            long value = Pointer.MAX_ADDRESS - pointer.getRawAddress();
            layout = Layout.rawLayout(minUL(Pointer.MAX_SIZE, value));
        } else {
            layout = Layout.rawLayout(Pointer.MAX_SIZE);
        }
    }

    MemorySegment(Pointer pointer, Layout layout, boolean ignore_alignment) {
        Objects.requireNonNull(pointer);
        Objects.requireNonNull(layout);
        assert_(ignore_alignment || pointer
                        .checkAlignmentShift(layout.alignmentShift()),
                IllegalArgumentException::new);
        pointer.addOffset(layout.size()); //check pointer + size
        this.pointer = pointer;
        this.layout = layout;
    }

    public Layout layout() {
        return layout;
    }

    @Override
    public Pointer pointer() {
        return pointer;
    }

    public long size() {
        return layout.size();
    }

    public int alignmentShift() {
        return pointer.alignmentShift();
    }

    public long alignment() {
        return 1L << alignmentShift();
    }

    private static final String INDENT = "    ";

    private String readToString(String spaces) {
        if (layout instanceof ValueLayout) {
            return spaces + layout + " = " + getValue();
        }
        if (layout instanceof GroupLayout || layout instanceof SequenceLayout) {
            String spaces2 = spaces + INDENT;
            String name = layout.name().isPresent() ? "(" + layout.name().get() + ")" : "";
            String type;
            if (layout instanceof GroupLayout) {
                type = ((GroupLayout) layout).isStruct() ? "struct" : "union";
            } else {
                type = "sequence";
            }
            String out = "%s[%s%s\n%s%s]";
            StringBuilder data = new StringBuilder();
            layout.elements().forEachOrdered((pe) -> {
                data.append(new MemorySegment(
                        pointer.addOffset(pe.offset()),
                        pe.layout(), true)
                        .readToString(spaces2));
                data.append("\n");
            });
            return String.format(out, spaces, type, name, data, spaces);
        }
        if (layout instanceof RawLayout) {
            //TODO: refactor
            assert_(layout.size() < Integer.MAX_VALUE, IllegalStateException::new);
            byte[] arr = new byte[(int) layout.size()];
            copyMemory(pointer.getBase(), pointer.getOffset(), arr, ARRAY_BYTE_BASE_OFFSET, arr.length);
            return spaces + (layout.isPadding() ? "padding " : "raw ") + layout + " = " + toHexString(arr);
        }
        throw new IllegalStateException();
    }

    public String readToString() {
        String data = readToString(INDENT);
        return "MemorySegment{" + pointer + "\n" + data + "\n}";
    }

    @Override
    public int hashCode() {
        return Objects.hash(pointer, layout);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof MemorySegment)) {
            return false;
        }
        MemorySegment other_ms = (MemorySegment) other;
        return Objects.equals(pointer, other_ms.pointer)
                && Objects.equals(layout, other_ms.layout);
    }

    @Override
    public String toString() {
        return "MemorySegment{" + pointer + "; layout " + layout + "}";
    }

    public final MemorySegment select(LayoutPath.PathElement... elements) {
        LayoutPath p = layout.selectPath(elements);
        return new MemorySegment(pointer.addOffset(p.offset()), p.layout(), true);
    }

    public boolean get(ValueLayout.OfBoolean layout, long offset) {
        return pointer.addOffset(offset).get(layout);
    }

    public byte get(ValueLayout.OfByte layout, long offset) {
        return pointer.addOffset(offset).get(layout);
    }

    public char get(ValueLayout.OfChar layout, long offset) {
        return pointer.addOffset(offset).get(layout);
    }

    public short get(ValueLayout.OfShort layout, long offset) {
        return pointer.addOffset(offset).get(layout);
    }

    public int get(ValueLayout.OfInt layout, long offset) {
        return pointer.addOffset(offset).get(layout);
    }

    public float get(ValueLayout.OfFloat layout, long offset) {
        return pointer.addOffset(offset).get(layout);
    }

    public long get(ValueLayout.OfLong layout, long offset) {
        return pointer.addOffset(offset).get(layout);
    }

    public double get(ValueLayout.OfDouble layout, long offset) {
        return pointer.addOffset(offset).get(layout);
    }

    public Object get(ValueLayout.OfObject layout, long offset) {
        return pointer.addOffset(offset).get(layout);
    }

    public <T> T get(ValueLayout.OfAddress<T> layout, long offset) {
        return pointer.addOffset(offset).get(layout);
    }

    public Word get(ValueLayout.OfWord layout, long offset) {
        return pointer.addOffset(offset).get(layout);
    }

    public Object getValue() {
        return pointer.getValue((ValueLayout) layout);
    }

    public String getCString(long offset, Charset charset) {
        return pointer.addOffset(offset).getCString(charset);
    }

    public String getCString(long offset) {
        return pointer.addOffset(offset).getCString();
    }

    public void put(ValueLayout.OfBoolean layout, long offset, boolean value) {
        pointer.addOffset(offset).put(layout, value);
    }

    public void put(ValueLayout.OfByte layout, long offset, byte value) {
        pointer.addOffset(offset).put(layout, value);
    }

    public void put(ValueLayout.OfChar layout, long offset, char value) {
        pointer.addOffset(offset).put(layout, value);
    }

    public void put(ValueLayout.OfShort layout, long offset, short value) {
        pointer.addOffset(offset).put(layout, value);
    }

    public void put(ValueLayout.OfInt layout, long offset, int value) {
        pointer.addOffset(offset).put(layout, value);
    }

    public void put(ValueLayout.OfFloat layout, long offset, float value) {
        pointer.addOffset(offset).put(layout, value);
    }

    public void put(ValueLayout.OfLong layout, long offset, long value) {
        pointer.addOffset(offset).put(layout, value);
    }

    public void put(ValueLayout.OfDouble layout, long offset, double value) {
        pointer.addOffset(offset).put(layout, value);
    }

    public void put(ValueLayout.OfObject layout, long offset, Object value) {
        pointer.addOffset(offset).put(layout, value);
    }

    public void put(ValueLayout.OfAddress<?> layout, long offset, Pointer value) {
        pointer.addOffset(offset).put(layout, value);
    }

    public void put(ValueLayout.OfWord layout, long offset, Word value) {
        pointer.addOffset(offset).put(layout, value);
    }

    public static MemorySegment getInstanceSegment(Object obj) {
        return Layout.getInstanceLayout(obj).bind(new Pointer(obj));
    }

    public static MemorySegment allocateCString(String value, Charset charset) {
        Objects.requireNonNull(value);
        byte[] data = value.getBytes(charset);
        for (byte tmp : data) {
            if (tmp == 0) {
                throw new IllegalArgumentException("C string can`t contain \\0 char");
            }
        }
        byte[] out = (byte[]) newNonMovableArrayVM(byte.class, data.length + 1);
        System.arraycopy(data, 0, out, 0, data.length);
        out[data.length] = 0;
        return Layout.sequenceLayout(data.length + 1, JAVA_BYTE)
                .bind(new Pointer(out, ARRAY_BYTE_BASE_OFFSET));
    }

    public static MemorySegment allocateCString(String value) {
        return allocateCString(value, StandardCharsets.UTF_8);
    }
}
