package com.v7878.unsafe.bytecode;

import com.v7878.unsafe.io.*;
import java.util.*;

public class TypeId {

    public static final int SIZE = 0x04;

    public static final Comparator<TypeId> getComparator(WriteContext context) {
        return (a, b) -> {
            if (a.equals(b)) {
                return 0;
            }
            return Integer.compare(context.getStringIndex(a.descriptor),
                    context.getStringIndex(b.descriptor));
        };
    }

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

    public void fillContext(DataSet data) {
        data.addString(descriptor);
    }

    public void write(WriteContext context, RandomOutput out) {
        out.writeInt(context.getStringIndex(descriptor));
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
