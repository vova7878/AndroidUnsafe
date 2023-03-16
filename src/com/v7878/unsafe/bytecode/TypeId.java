package com.v7878.unsafe.bytecode;

import com.v7878.unsafe.io.RandomInput;
import java.util.Objects;

public class TypeId {

    public String descriptor;

    public static TypeId read(RandomInput in, Context context) {
        TypeId out = new TypeId();
        out.descriptor = context.string(in.readInt());
        return out;
    }

    public static TypeId[] readTypeList(RandomInput in, Context context) {
        int size = in.readInt();
        TypeId[] out = new TypeId[size];
        for (int i = 0; i < size; i++) {
            out[i] = context.type(in.readUnsignedShort());
        }
        return out;
    }

    @Override
    public String toString() {
        return descriptor;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TypeId) {
            TypeId tobj = (TypeId) obj;
            return Objects.equals(descriptor, tobj.descriptor);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(descriptor);
    }
}
