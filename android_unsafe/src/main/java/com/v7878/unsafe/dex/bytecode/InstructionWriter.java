package com.v7878.unsafe.dex.bytecode;

import com.v7878.unsafe.io.RandomOutput;

class InstructionWriter {

    public static int check_unsigned(int value, int width) {
        if ((value >>> width) != 0) {
            throw new IllegalStateException("illegal instruction unsigned value "
                    + Integer.toHexString(width) + " for width " + width);
        }
        return value & (~0 >>> (32 - width));
    }

    public static int check_signed(int value, int width) {
        int empty_width = 32 - width;
        if (value << empty_width >> empty_width != value) {
            throw new IllegalStateException("illegal instruction signed value "
                    + Integer.toHexString(value) + " for width " + width);
        }
        return value & (~0 >>> empty_width);
    }

    public static long check_signed64(long value, int width) {
        int empty_width = 64 - width;
        if (value << empty_width >> empty_width != value) {
            throw new IllegalStateException("illegal instruction signed value "
                    + Long.toHexString(value) + " for width " + width);
        }
        return value & (~0L >>> empty_width);
    }

    public static int check_hat32(int value, int width) {
        if ((value & -1 >>> width) != 0) {
            throw new IllegalStateException("illegal instruction hat value "
                    + Integer.toHexString(value) + " for width " + width);
        }
        return value >>> (32 - width);
    }

    public static long check_hat64(long value, int width) {
        if ((value & -1L >>> width) != 0) {
            throw new IllegalStateException("illegal instruction hat value "
                    + Long.toHexString(value) + " for width " + width);
        }
        return value >>> (64 - width);
    }

    private static void write_base(RandomOutput out, int opcode, int arg) {
        if (opcode >>> 8 != 0) {
            throw new IllegalStateException("illegal opcode: " + opcode);
        }
        if (arg >>> 8 != 0) {
            throw new IllegalStateException("illegal arg: " + arg);
        }
        out.writeShort((arg << 8) | opcode);
    }

    public static void write_10x(RandomOutput out, int opcode) {
        write_base(out, opcode, 0);
    }

    public static void write_12x(RandomOutput out, int opcode, int A, int B) {
        A = check_unsigned(A, 4);
        B = check_unsigned(B, 4);
        write_base(out, opcode, (B << 4) | A);
    }

    public static void write_11n(RandomOutput out, int opcode, int A, int sB) {
        A = check_unsigned(A, 4);
        sB = check_signed(sB, 4);
        write_base(out, opcode, (sB << 4) | A);
    }

    public static void write_11x(RandomOutput out, int opcode, int AA) {
        AA = check_unsigned(AA, 8);
        write_base(out, opcode, AA);
    }

    public static void write_10t(RandomOutput out, int opcode, int sAA) {
        sAA = check_signed(sAA, 8);
        write_base(out, opcode, sAA);
    }

    public static void write_22x_21c(RandomOutput out, int opcode, int AA, int BBBB) {
        AA = check_unsigned(AA, 8);
        BBBB = check_unsigned(BBBB, 16);
        write_base(out, opcode, AA);
        out.writeShort(BBBB);
    }

    public static void write_21t_21s(RandomOutput out, int opcode, int AA, int sBBBB) {
        AA = check_unsigned(AA, 8);
        sBBBB = check_signed(sBBBB, 16);
        write_base(out, opcode, AA);
        out.writeShort(sBBBB);
    }

    public static void write_22c(RandomOutput out, int opcode, int A, int B, int cCCCC) {
        A = check_unsigned(A, 4);
        B = check_unsigned(B, 4);
        cCCCC = check_unsigned(cCCCC, 16);
        write_base(out, opcode, (B << 4) | A);
        out.writeShort(cCCCC);
    }

    public static void write_23x(RandomOutput out, int opcode, int AA, int BB, int CC) {
        AA = check_unsigned(AA, 8);
        BB = check_unsigned(BB, 8);
        CC = check_unsigned(CC, 8);
        write_base(out, opcode, AA);
        out.writeShort((CC << 8) | BB);
    }

    public static void write_22b(RandomOutput out, int opcode, int AA, int BB, int sCC) {
        AA = check_unsigned(AA, 8);
        BB = check_unsigned(BB, 8);
        sCC = check_signed(sCC, 8);
        write_base(out, opcode, AA);
        out.writeShort((sCC << 8) | BB);
    }

    public static void write_22t_22s(RandomOutput out, int opcode, int A, int B, int sCCCC) {
        A = check_unsigned(A, 4);
        B = check_unsigned(B, 4);
        sCCCC = check_signed(sCCCC, 16);
        write_base(out, opcode, (B << 4) | A);
        out.writeShort(sCCCC);
    }

    public static void write_30t(RandomOutput out, int opcode, int AAAAAAAA) {
        // no need to check AAAAAAAA
        write_base(out, opcode, 0);
        out.writeShort(AAAAAAAA & 0xffff);
        out.writeShort(AAAAAAAA >>> 16);
    }

    public static void write_32x(RandomOutput out, int opcode, int AAAA, int BBBB) {
        AAAA = check_unsigned(AAAA, 16);
        BBBB = check_unsigned(BBBB, 16);
        write_base(out, opcode, 0);
        out.writeShort(AAAA);
        out.writeShort(BBBB);
    }

    public static void write_31i_31t_31c(RandomOutput out, int opcode, int AA, int BBBBBBBB) {
        check_unsigned(AA, 8);
        // no need to check BBBBBBBB
        write_base(out, opcode, AA);
        out.writeShort(BBBBBBBB & 0xffff);
        out.writeShort(BBBBBBBB >>> 16);
    }

    public static void write_35c_35ms_35mi(RandomOutput out, int opcode, int A,
                                           int BBBB, int C, int D, int E, int F, int G) {
        A = check_unsigned(A, 4);
        BBBB = check_unsigned(BBBB, 16);
        C = check_unsigned(C, 4);
        D = check_unsigned(D, 4);
        E = check_unsigned(E, 4);
        F = check_unsigned(F, 4);
        G = check_unsigned(G, 4);
        write_base(out, opcode, (A << 4) | G);
        out.writeShort(BBBB);
        out.writeShort((F << 12) | (E << 8) | (D << 4) | C);
    }

    public static void write_45cc(RandomOutput out, int opcode, int A,
                                  int BBBB, int C, int D, int E, int F, int G, int HHHH) {
        A = check_unsigned(A, 4);
        BBBB = check_unsigned(BBBB, 16);
        C = check_unsigned(C, 4);
        D = check_unsigned(D, 4);
        E = check_unsigned(E, 4);
        F = check_unsigned(F, 4);
        G = check_unsigned(G, 4);
        HHHH = check_unsigned(HHHH, 16);
        write_base(out, opcode, (A << 4) | G);
        out.writeShort(BBBB);
        out.writeShort((F << 12) | (E << 8) | (D << 4) | C);
        out.writeShort(HHHH);
    }

    /*
    public static void write_20t(RandomOutput out, int opcode, int sAAAA) {
        sAAAA = check_signed(sAAAA, 16);
        write_base(out, opcode, 0);
        out.writeShort(sAAAA);
    }

    public static void write_21h32(RandomOutput out, int opcode, int AA, int BBBB0000) {
        check_unsigned(AA, 8);
        int BBBB = check_hat32(BBBB0000, 16);
        write_base(out, opcode, AA);
        out.writeShort(BBBB);
    }

    public static void write_21h64(RandomOutput out, int opcode, int AA, long BBBB000000000000) {
        check_unsigned(AA, 8);
        int BBBB = (int) check_hat64(BBBB000000000000, 16);
        write_base(out, opcode, AA);
        out.writeShort(BBBB);
    }

    public static void write_51l(RandomOutput out, int opcode, int AA, long BBBBBBBBBBBBBBBB) {
        check_unsigned(AA, 8);
        write_base(out, opcode, AA);
        out.writeShort((int) (BBBBBBBBBBBBBBBB & 0xffff));
        out.writeShort((int) ((BBBBBBBBBBBBBBBB >>> 16) & 0xffff));
        out.writeShort((int) ((BBBBBBBBBBBBBBBB >>> 32) & 0xffff));
        out.writeShort((int) (BBBBBBBBBBBBBBBB >>> 48));
    }

    public static void write_fill_array_data_payload(RandomOutput out,
                                                     int opcode, int element_width, byte[] data) {
        //TODO
        throw new UnsupportedOperationException("Not supported yet.");
    }
    */
}
