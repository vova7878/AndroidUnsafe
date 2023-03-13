package com.v7878.unsafe.bytecode;

import com.v7878.unsafe.io.RandomInput;

public class ValueCoder {

//    public static void writeSignedIntegralValue(RandomOutput out, int type, long value) {
//        /*
//         * Figure out how many bits are needed to represent the value,
//         * including a sign bit: The bit count is subtracted from 65
//         * and not 64 to account for the sign bit. The xor operation
//         * has the effect of leaving non-negative values alone and
//         * unary complementing negative values (so that a leading zero
//         * count always returns a useful number for our present
//         * purpose).
//         */
//        int requiredBits = 65 - Long.numberOfLeadingZeros(value ^ (value >> 63));
//
//        // Round up the requiredBits to a number of bytes.
//        int requiredBytes = (requiredBits + 0x07) >> 3;
//
//        /*
//         * Write the header byte, which includes the type and
//         * requiredBytes - 1.
//         */
//        out.writeByte(type | ((requiredBytes - 1) << 5));
//
//        // Write the value, per se.
//        while (requiredBytes > 0) {
//            out.writeByte((byte) value);
//            value >>= 8;
//            requiredBytes--;
//        }
//    }
//
//    /**
//     * Writes an unsigned integral to {@code out}.
//     */
//    public static void writeUnsignedIntegralValue(RandomOutput out, int type, long value) {
//        // Figure out how many bits are needed to represent the value.
//        int requiredBits = 64 - Long.numberOfLeadingZeros(value);
//        if (requiredBits == 0) {
//            requiredBits = 1;
//        }
//
//        // Round up the requiredBits to a number of bytes.
//        int requiredBytes = (requiredBits + 0x07) >> 3;
//
//        /*
//         * Write the header byte, which includes the type and
//         * requiredBytes - 1.
//         */
//        out.writeByte(type | ((requiredBytes - 1) << 5));
//
//        // Write the value, per se.
//        while (requiredBytes > 0) {
//            out.writeByte((byte) value);
//            value >>= 8;
//            requiredBytes--;
//        }
//    }
//
//    /**
//     * Writes a right-zero-extended value to {@code out}.
//     */
//    public static void writeRightZeroExtendedValue(RandomOutput out, int type, long value) {
//        // Figure out how many bits are needed to represent the value.
//        int requiredBits = 64 - Long.numberOfTrailingZeros(value);
//        if (requiredBits == 0) {
//            requiredBits = 1;
//        }
//
//        // Round up the requiredBits to a number of bytes.
//        int requiredBytes = (requiredBits + 0x07) >> 3;
//
//        // Scootch the first bits to be written down to the low-order bits.
//        value >>= 64 - (requiredBytes * 8);
//
//        /*
//         * Write the header byte, which includes the type and
//         * requiredBytes - 1.
//         */
//        out.writeByte(type | ((requiredBytes - 1) << 5));
//
//        // Write the value, per se.
//        while (requiredBytes > 0) {
//            out.writeByte((byte) value);
//            value >>= 8;
//            requiredBytes--;
//        }
//    }
    /**
     * Read a signed integer.
     *
     * @param zwidth byte count minus one
     */
    public static int readSignedInt(RandomInput in, int zwidth) {
        int result = 0;
        for (int i = zwidth; i >= 0; i--) {
            result = (result >>> 8) | ((in.readByte() & 0xff) << 24);
        }
        result >>= (3 - zwidth) * 8;
        return result;
    }

    /**
     * Read an unsigned integer.
     *
     * @param zwidth byte count minus one
     * @param fillOnRight true to zero fill on the right; false on the left
     */
    public static int readUnsignedInt(RandomInput in, int zwidth, boolean fillOnRight) {
        int result = 0;
        if (!fillOnRight) {
            for (int i = zwidth; i >= 0; i--) {
                result = (result >>> 8) | ((in.readByte() & 0xff) << 24);
            }
            result >>>= (3 - zwidth) * 8;
        } else {
            for (int i = zwidth; i >= 0; i--) {
                result = (result >>> 8) | ((in.readByte() & 0xff) << 24);
            }
        }
        return result;
    }

    /**
     * Read a signed long.
     *
     * @param zwidth byte count minus one
     */
    public static long readSignedLong(RandomInput in, int zwidth) {
        long result = 0;
        for (int i = zwidth; i >= 0; i--) {
            result = (result >>> 8) | ((in.readByte() & 0xffL) << 56);
        }
        result >>= (7 - zwidth) * 8;
        return result;
    }

    /**
     * Read an unsigned long.
     *
     * @param zwidth byte count minus one
     * @param fillOnRight true to zero fill on the right; false on the left
     */
    public static long readUnsignedLong(RandomInput in, int zwidth, boolean fillOnRight) {
        long result = 0;
        if (!fillOnRight) {
            for (int i = zwidth; i >= 0; i--) {
                result = (result >>> 8) | ((in.readByte() & 0xffL) << 56);
            }
            result >>>= (7 - zwidth) * 8;
        } else {
            for (int i = zwidth; i >= 0; i--) {
                result = (result >>> 8) | ((in.readByte() & 0xffL) << 56);
            }
        }
        return result;
    }
}
