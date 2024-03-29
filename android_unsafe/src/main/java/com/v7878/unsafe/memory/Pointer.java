package com.v7878.unsafe.memory;

import static com.v7878.misc.Math.isPowerOfTwo;
import static com.v7878.misc.Math.roundUpUL;
import static com.v7878.unsafe.AndroidUnsafe4.ARRAY_BYTE_BASE_OFFSET;
import static com.v7878.unsafe.AndroidUnsafe4.IS64BIT;
import static com.v7878.unsafe.AndroidUnsafe4.OBJECT_ALIGNMENT_SHIFT;
import static com.v7878.unsafe.AndroidUnsafe4.addressOfNonMovableArray;
import static com.v7878.unsafe.AndroidUnsafe4.addressOfNonMovableArrayData;
import static com.v7878.unsafe.AndroidUnsafe4.allocateMemory;
import static com.v7878.unsafe.AndroidUnsafe4.copyMemory;
import static com.v7878.unsafe.AndroidUnsafe4.getBoolean;
import static com.v7878.unsafe.AndroidUnsafe4.getByte;
import static com.v7878.unsafe.AndroidUnsafe4.getCharUnaligned;
import static com.v7878.unsafe.AndroidUnsafe4.getDoubleUnaligned;
import static com.v7878.unsafe.AndroidUnsafe4.getFloatUnaligned;
import static com.v7878.unsafe.AndroidUnsafe4.getIntUnaligned;
import static com.v7878.unsafe.AndroidUnsafe4.getLongUnaligned;
import static com.v7878.unsafe.AndroidUnsafe4.getObject;
import static com.v7878.unsafe.AndroidUnsafe4.getObjectRaw;
import static com.v7878.unsafe.AndroidUnsafe4.getShortUnaligned;
import static com.v7878.unsafe.AndroidUnsafe4.getWordUnaligned;
import static com.v7878.unsafe.AndroidUnsafe4.newNonMovableArrayVM;
import static com.v7878.unsafe.AndroidUnsafe4.objectToInt;
import static com.v7878.unsafe.AndroidUnsafe4.putBoolean;
import static com.v7878.unsafe.AndroidUnsafe4.putByte;
import static com.v7878.unsafe.AndroidUnsafe4.putCharUnaligned;
import static com.v7878.unsafe.AndroidUnsafe4.putDoubleUnaligned;
import static com.v7878.unsafe.AndroidUnsafe4.putFloatUnaligned;
import static com.v7878.unsafe.AndroidUnsafe4.putIntUnaligned;
import static com.v7878.unsafe.AndroidUnsafe4.putLongUnaligned;
import static com.v7878.unsafe.AndroidUnsafe4.putObject;
import static com.v7878.unsafe.AndroidUnsafe4.putObjectRaw;
import static com.v7878.unsafe.AndroidUnsafe4.putShortUnaligned;
import static com.v7878.unsafe.AndroidUnsafe4.putWordUnaligned;
import static com.v7878.unsafe.MemoryChecks.checkNativeAddress;
import static com.v7878.unsafe.MemoryChecks.checkOffset;
import static com.v7878.unsafe.Utils.assert_;
import static com.v7878.unsafe.memory.ValueLayout.JAVA_BYTE;

import com.v7878.unsafe.AndroidUnsafe8.ScopedDisableGC;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

//important: all get/put operations without security checks
public final class Pointer implements Addressable {

    public static final Pointer NULL = new Pointer(0);

    public static final long MAX_ADDRESS = IS64BIT ? ~0L : 0xffffffffL;
    public static final long MAX_SIZE = IS64BIT ? Long.MAX_VALUE : 0xffffffffL;

    private final Object base;
    private final long base_address;
    private final long offset;

    private Pointer(Object base, long base_address, long offset) {
        this.base = base;
        this.base_address = base_address;
        this.offset = offset;
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
            if (!checkNativeAddress(offset)) {
                throw new IllegalArgumentException(
                        "illegal native address: " + offset);
            }
            this.base_address = offset & MAX_ADDRESS;
            this.offset = 0;
        } else {
            if (!checkOffset(offset)) {
                throw new IllegalArgumentException("illegal offset: " + offset);
            }
            this.offset = offset;
            long address = 0;
            try {
                address = addressOfNonMovableArray(base);
            } catch (Throwable ignored) {
            }
            if (address != 0) {
                //checks
                long raw_address = address + offset;
                if (Long.compareUnsigned(raw_address, address) < 0) {
                    throw new IllegalArgumentException(String.format(
                            "base address(%s) + offset(%s) overflows",
                            address, offset));
                }
                if (!checkNativeAddress(raw_address)) {
                    throw new IllegalArgumentException(
                            "illegal raw address: " + raw_address);
                }
            }
            this.base_address = address & MAX_ADDRESS;
        }
    }

    @Override
    public Pointer pointer() {
        return this;
    }

    public boolean isNull() {
        return base == null && base_address == 0 && offset == 0;
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

    private long getRawAddressNoCheck() {
        return base_address + offset;
    }

    public long getRawAddress() {
        assert_(hasRawAddress(), UnsupportedOperationException::new);
        return getRawAddressNoCheck();
    }

    public long getOffset() {
        return base == null ? base_address + offset : offset;
    }

    public Pointer addOffset(long off) {
        if (hasRawAddress()) {
            long address = getRawAddressNoCheck();
            address = Math.addExact(address, off);
            assert_(checkNativeAddress(address), IllegalArgumentException::new);
            long new_offset = address - base_address;
            assert_(checkOffset(new_offset), IllegalArgumentException::new);
            return new Pointer(base, base_address, new_offset);
        }
        long new_offset = Math.addExact(offset, off);
        assert_(checkOffset(new_offset), IllegalArgumentException::new);
        return new Pointer(base, 0, new_offset);
    }

    public int alignmentShift() {
        if (hasRawAddress()) {
            return Long.numberOfTrailingZeros(base_address + offset);
        }
        return Math.min(OBJECT_ALIGNMENT_SHIFT,
                Long.numberOfTrailingZeros(offset));
    }

    public int alignment() {
        return 1 << alignmentShift();
    }

    public boolean checkAlignmentShift(int shift) {
        return shift <= alignmentShift();
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

    public <T> T get(ValueLayout.OfAddress<T> layout) {
        return layout.content().bind(new Pointer(getWordUnaligned(base,
                getOffset(), layout.order())));
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
        } else if (carrier == Addressable.class) {
            return get((ValueLayout.OfAddress<?>) layout);
        } else if (carrier == Word.class) {
            return get((ValueLayout.OfWord) layout);
        }
        throw new IllegalStateException();
    }

    private static int strlen(Pointer data) {
        //noinspection OverflowingLoopIndex
        for (int offset = 0; offset >= 0; offset++) {
            byte curr = data.addOffset(offset).get(JAVA_BYTE);
            if (curr == 0) {
                return offset;
            }
        }
        throw new IllegalArgumentException("String too large");
    }

    public String getCString(Charset charset) {
        int len = strlen(this);
        byte[] bytes = new byte[len];
        copyMemory(getBase(), getOffset(),
                bytes, ARRAY_BYTE_BASE_OFFSET, len);
        return new String(bytes, charset);
    }

    public String getCString() {
        return getCString(StandardCharsets.UTF_8);
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

    public void put(ValueLayout.OfAddress<?> layout, Pointer value) {
        putWordUnaligned(base, getOffset(), value.getRawAddress(), layout.order());
    }

    public void put(ValueLayout.OfWord layout, Word value) {
        putWordUnaligned(base, getOffset(), value.longValue(), layout.order());
    }

    public static void copy(Pointer from, Pointer to, long size) {
        if (from.hasRawAddress() && to.hasRawAddress()) {
            copyMemory(from.getRawAddress(), to.getRawAddress(), size);
            return;
        }
        try (ScopedDisableGC dgc = new ScopedDisableGC()) {
            copyMemory((objectToInt(from.getBase()) & 0xffffffffL) + from.getOffset(),
                    (objectToInt(to.getBase()) & 0xffffffffL) + to.getOffset(), size);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(base, base_address, offset);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Pointer)) {
            return false;
        }
        Pointer other_ptr = (Pointer) other;
        return base == other_ptr.base
                && base_address == other_ptr.base_address
                && offset == other_ptr.offset;
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
        out.append(alignmentShift());
        return out.toString();
    }

    public static Pointer allocateHeap(int size, int alignment) {
        assert_(size >= 0, IllegalArgumentException::new);
        assert_(isPowerOfTwo(alignment), IllegalArgumentException::new);
        size = Math.addExact(size, alignment - 1);
        Object data = newNonMovableArrayVM(byte.class, size);
        long address = addressOfNonMovableArrayData(data);
        long aligned_address = roundUpUL(address, alignment);
        return new Pointer(data, ARRAY_BYTE_BASE_OFFSET + aligned_address - address);
    }

    public static Pointer allocateNative(long size, long alignment) {
        Layout.requireValidSize(size);
        Layout.requireValidAlignment(alignment);
        size = Math.addExact(size, alignment - 1);
        long address = allocateMemory(size);
        long aligned_address = roundUpUL(address, alignment);
        return new Pointer(null, address).addOffset(aligned_address - address);
    }
}
