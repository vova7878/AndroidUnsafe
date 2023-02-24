package com.v7878.unsafe.memory;

import static com.v7878.unsafe.AndroidUnsafe4.*;
import static com.v7878.unsafe.Utils.*;

public class Pointer {

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
        }
        out.append("+");
        out.append(offset);
        out.append("%");
        out.append(alignShift);
        return out.toString();
    }
}
