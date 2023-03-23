package com.v7878.unsafe.dex;

import com.v7878.unsafe.io.*;
import java.util.*;

public class StringId {

    public static final int SIZE = 0x04;

    public static final Comparator<String> COMPARATOR = (a, b) -> {
        int a_length = a.codePointCount(0, a.length());
        int b_length = b.codePointCount(0, b.length());

        int a_code;
        int b_code;

        int i = 0;
        do {
            if (i == a_length) {
                return (i == b_length) ? 0 : -1;
            } else if (i == b_length) {
                return 1;
            }

            a_code = a.codePointAt(i);
            b_code = b.codePointAt(i);
            i++;
        } while (a_code == b_code);

        int leading_surrogate_diff = Character.highSurrogate(a_code)
                - Character.highSurrogate(b_code);
        if (leading_surrogate_diff != 0) {
            return leading_surrogate_diff;
        }

        return Character.lowSurrogate(a_code)
                - Character.lowSurrogate(b_code);
    };

    public static String read(RandomInput in) {
        int data_off = in.readInt();
        return in.duplicate(data_off).readMUTF8();
    }

    public static void write(String value, WriteContext context,
            RandomOutput ids_out, RandomOutput data_out) {
        ids_out.writeInt((int) data_out.position());
        data_out.writeMUtf8(value);
    }
}
