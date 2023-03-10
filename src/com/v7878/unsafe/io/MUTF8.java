package com.v7878.unsafe.io;

public class MUTF8 {

    private MUTF8() {
    }

    public static String decode(RandomInput in, char[] out) {
        int s = 0;
        while (true) {
            char a = (char) (in.readByte() & 0xff);
            if (a == 0) {
                return new String(out, 0, s);
            }
            out[s] = a;
            if (a < '\u0080') {
                s++;
            } else if ((a & 0xe0) == 0xc0) {
                int b = in.readByte() & 0xff;
                if ((b & 0xC0) != 0x80) {
                    throw new IllegalStateException("bad second byte");
                }
                out[s++] = (char) (((a & 0x1F) << 6) | (b & 0x3F));
            } else if ((a & 0xf0) == 0xe0) {
                int b = in.readByte() & 0xff;
                int c = in.readByte() & 0xff;
                if (((b & 0xC0) != 0x80) || ((c & 0xC0) != 0x80)) {
                    throw new IllegalStateException("bad second or third byte");
                }
                out[s++] = (char) (((a & 0x0F) << 12) | ((b & 0x3F) << 6) | (c & 0x3F));
            } else {
                throw new IllegalStateException("bad byte");
            }
        }
    }

    public static String readMUTF8(RandomInput in) {
        int expectedLength = in.readULeb128();
        String result = decode(in, new char[expectedLength]);
        if (result.length() != expectedLength) {
            throw new IllegalStateException("Declared length " + expectedLength
                    + " doesn't match decoded length of " + result.length());
        }
        return result;
    }

    /*private static long countBytes(String s, boolean shortLength) {
        long result = 0;
        final int length = s.length();
        for (int i = 0; i < length; ++i) {
            char ch = s.charAt(i);
            if (ch != 0 && ch <= 127) { // U+0000 uses two bytes.
                ++result;
            } else if (ch <= 2047) {
                result += 2;
            } else {
                result += 3;
            }
            if (shortLength && result > 65535) {
                throw new IllegalArgumentException("String more than 65535 UTF bytes long");
            }
        }
        return result;
    }

    public static void encode(byte[] dst, int offset, String s) {
        final int length = s.length();
        for (int i = 0; i < length; i++) {
            char ch = s.charAt(i);
            if (ch != 0 && ch <= 127) { // U+0000 uses two bytes.
                dst[offset++] = (byte) ch;
            } else if (ch <= 2047) {
                dst[offset++] = (byte) (0xc0 | (0x1f & (ch >> 6)));
                dst[offset++] = (byte) (0x80 | (0x3f & ch));
            } else {
                dst[offset++] = (byte) (0xe0 | (0x0f & (ch >> 12)));
                dst[offset++] = (byte) (0x80 | (0x3f & (ch >> 6)));
                dst[offset++] = (byte) (0x80 | (0x3f & ch));
            }
        }
    }

    public static byte[] encode(String s) {
        int utfCount = (int) countBytes(s, true);
        byte[] result = new byte[utfCount];
        encode(result, 0, s);
        return result;
    }*/
}
