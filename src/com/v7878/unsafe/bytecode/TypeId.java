package com.v7878.unsafe.bytecode;

import com.v7878.unsafe.io.*;
import java.util.*;
import java.util.stream.*;

public class TypeId {

    public static class TypeList {

        public static final int ALIGNMENT = 4;

        public static final Comparator<TypeList> getComparator(WriteContext context) {
            return (a, b) -> {
                if (a == b) {
                    return 0;
                }

                int a_size = a.list.length;
                int b_size = b.list.length;
                int size = Math.min(a_size, b_size);

                for (int i = 0; i < size; i++) {
                    TypeId a_type = a.list[i];
                    TypeId b_type = b.list[i];

                    int out = context.type_comparator.compare(a_type, b_type);

                    if (out != 0) {
                        return out;
                    }
                }

                if (a_size < b_size) {
                    return -1;
                } else if (a_size > b_size) {
                    return 1;
                } else {
                    return 0;
                }
            };
        }

        TypeId[] list;

        public static TypeList read(RandomInput in, Context context) {
            int size = in.readInt();
            TypeList out = new TypeList();
            out.list = new TypeId[size];
            for (int i = 0; i < size; i++) {
                out.list[i] = context.type(in.readUnsignedShort());
            }
            return out;
        }

        public static TypeList empty() {
            TypeList out = new TypeList();
            out.list = new TypeId[0];
            return out;
        }

        public void fillContext(DataSet data) {
            for (TypeId tmp : list) {
                data.addType(tmp);
            }
        }

        public void write(WriteContext context, RandomOutput out) {
            out.writeInt(list.length);
            for (TypeId tmp : list) {
                out.writeShort(context.getTypeIndex(tmp));
            }
        }

        public boolean isEmpty() {
            return list.length == 0;
        }

        @Override
        public String toString() {
            return Arrays.stream(list)
                    .map((p) -> p.toString())
                    .collect(Collectors.joining("", "(", ")"));
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof TypeList) {
                TypeList tlobj = (TypeList) obj;
                return Arrays.equals(list, tlobj.list);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(list);
        }
    }

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
