package com.v7878.unsafe.bytecode;

import com.v7878.unsafe.io.RandomInput;

public class StringId {

    public String data;

    public static String read(RandomInput in) {
        int data_off = in.readInt();
        return in.duplicate(data_off).readMUTF8();
    }
}
