package com.v7878.unsafe.bytecode;

import com.v7878.unsafe.io.RandomInput;
import java.util.Objects;

public class StringId {

    public String data;

    public static StringId read(RandomInput in) {
        StringId out = new StringId();
        int data_off = in.readInt();
        out.data = in.duplicate(data_off).readMUTF8();
        return out;
    }

    @Override
    public String toString() {
        return data;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof StringId) {
            StringId sobj = (StringId) obj;
            return Objects.equals(data, sobj.data);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(data);
    }
}
