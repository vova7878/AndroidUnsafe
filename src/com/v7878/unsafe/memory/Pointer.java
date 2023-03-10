package com.v7878.unsafe.memory;

import static com.v7878.unsafe.AndroidUnsafe4.*;
import static com.v7878.unsafe.Checks.*;
import static com.v7878.unsafe.Utils.*;
import java.util.Objects;

public final class Pointer {

    private final Object base;
    private final long base_address;
    private final long offset;
    private final int alignShift;

    private Pointer(Object base, long base_address, long offset, int alignShift) {
        this.base = base;
        this.base_address = base_address;
        this.offset = offset;
        this.alignShift = alignShift;
    }

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

    public boolean hasRawAddress() {
        return (base == null) || (base_address != 0);
    }

    public long getRawAddress() {
        assert_(hasRawAddress(), UnsupportedOperationException::new);
        return base_address + offset;
    }

    public long getOffset() {
        return base == null ? base_address + offset : offset;
    }

    public Pointer addOffset(long add_offset) {
        if (hasRawAddress()) {
            long address = getRawAddress();
            address = Math.addExact(address, add_offset);
            assert_(checkNativeAddress(address), IllegalArgumentException::new);
            long new_offset = address - base_address;
            assert_(checkOffset(new_offset), IllegalArgumentException::new);
            return new Pointer(base, base_address, new_offset,
                    Long.numberOfTrailingZeros(address));
        }
        long new_offset = Math.addExact(offset, add_offset);
        assert_(checkOffset(new_offset), IllegalArgumentException::new);
        return new Pointer(base, 0, new_offset, OBJECT_ALIGNMENT_SHIFT);
    }

    public int getAlignmentShift() {
        return alignShift;
    }

    public boolean checkAlignmentShift(int shift) {
        return shift <= alignShift;
    }

    public boolean get(ValueLayout.OfBoolean layout) {
        return getBoolean(base, getOffset());
    }

    public byte get(ValueLayout.OfByte layout) {
        return getByte(base, getOffset());
    }

    public char get(ValueLayout.OfChar layout) {
        return getCharUnaligned(base, getOffset(), layout.order());
    }

    public short get(ValueLayout.OfShort layout) {
        return getShortUnaligned(base, getOffset(), layout.order());
    }

    public int get(ValueLayout.OfInt layout) {
        return getIntUnaligned(base, getOffset(), layout.order());
    }

    public float get(ValueLayout.OfFloat layout) {
        return getFloatUnaligned(base, getOffset(), layout.order());
    }

    public long get(ValueLayout.OfLong layout) {
        return getLongUnaligned(base, getOffset(), layout.order());
    }

    public double get(ValueLayout.OfDouble layout) {
        return getDoubleUnaligned(base, getOffset(), layout.order());
    }

    public Object get(ValueLayout.OfObject layout) {
        if (base == null) {
            return getObjectRaw(getOffset());
        }
        return getObject(base, getOffset());
    }

    public Pointer get(ValueLayout.OfAddress layout) {
        return new Pointer(getWordUnaligned(base, getOffset(), layout.order()));
    }

    public Word get(ValueLayout.OfWord layout) {
        return new Word(getWordUnaligned(base, getOffset(), layout.order()));
    }

    public Object getValue(ValueLayout layout) {
        Objects.requireNonNull(layout);
        Class<?> carrier = layout.carrier();
        if (carrier == boolean.class) {
            return get((ValueLayout.OfBoolean) layout);
        } else if (carrier == byte.class) {
            return get((ValueLayout.OfByte) layout);
        } else if (carrier == char.class) {
            return get((ValueLayout.OfChar) layout);
        } else if (carrier == short.class) {
            return get((ValueLayout.OfShort) layout);
        } else if (carrier == int.class) {
            return get((ValueLayout.OfInt) layout);
        } else if (carrier == float.class) {
            return get((ValueLayout.OfFloat) layout);
        } else if (carrier == long.class) {
            return get((ValueLayout.OfLong) layout);
        } else if (carrier == double.class) {
            return get((ValueLayout.OfDouble) layout);
        } else if (carrier == Object.class) {
            return get((ValueLayout.OfObject) layout);
        } else if (carrier == Pointer.class) {
            return get((ValueLayout.OfAddress) layout);
        } else if (carrier == Word.class) {
            return get((ValueLayout.OfWord) layout);
        }
        throw new IllegalStateException();
    }

    public void put(ValueLayout.OfBoolean layout, boolean value) {
        putBoolean(base, getOffset(), value);
    }

    public void put(ValueLayout.OfByte layout, byte value) {
        putByte(base, getOffset(), value);
    }

    public void put(ValueLayout.OfChar layout, char value) {
        putCharUnaligned(base, getOffset(), value, layout.order());
    }

    public void put(ValueLayout.OfShort layout, short value) {
        putShortUnaligned(base, getOffset(), value, layout.order());
    }

    public void put(ValueLayout.OfInt layout, int value) {
        putIntUnaligned(base, getOffset(), value, layout.order());
    }

    public void put(ValueLayout.OfFloat layout, float value) {
        putFloatUnaligned(base, getOffset(), value, layout.order());
    }

    public void put(ValueLayout.OfLong layout, long value) {
        putLongUnaligned(base, getOffset(), value, layout.order());
    }

    public void put(ValueLayout.OfDouble layout, double value) {
        putDoubleUnaligned(base, getOffset(), value, layout.order());
    }

    public void put(ValueLayout.OfObject layout, Object value) {
        if (base == null) {
            putObjectRaw(getOffset(), value);
        } else {
            putObject(base, getOffset(), value);
        }
    }

    public void put(ValueLayout.OfAddress layout, Pointer value) {
        putWordUnaligned(base, getOffset(), value.getRawAddress(), layout.order());
    }

    public void put(ValueLayout.OfWord layout, Word value) {
        putWordUnaligned(base, getOffset(), value.longValue(), layout.order());
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
        }
        out.append("+");
        out.append(offset);
        out.append("%");
        out.append(alignShift);
        return out.toString();
    }

    public static Pointer allocateHeap(int size, int alignment) {
        assert_(size >= 0, IllegalArgumentException::new);
        assert_(isPowerOfTwo(alignment), IllegalArgumentException::new);
        size = Math.addExact(size, alignment - 1);
        Object data = newNonMovableArrayVM(byte.class, size);
        long address = addressOfNonMovableArrayData(data);
        long aligned_address = roundUpL(address, alignment);
        return new Pointer(data, ARRAY_BYTE_BASE_OFFSET + aligned_address - address);
    }

    public static Pointer allocateNative(long size, long alignment) {
        Layout.requireValidSize(size);
        Layout.requireValidAlignment(alignment);
        size = Math.addExact(size, alignment - 1);
        long address = allocateMemory(size);
        long aligned_address = roundUpL(address, alignment);
        return new Pointer(null, address).addOffset(aligned_address - address);
    }
}
