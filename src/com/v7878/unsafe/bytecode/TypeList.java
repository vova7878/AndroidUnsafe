package com.v7878.unsafe.bytecode;

import com.v7878.unsafe.io.*;
import java.util.*;
import java.util.stream.*;

public class TypeList extends PCList<TypeId> {

    public static final int ALIGNMENT = 4;

    public static final Comparator<TypeList> getComparator(WriteContext context) {
        return (a, b) -> {
            if (a == b) {
                return 0;
            }

            int size = Math.min(a.size(), b.size());

            for (int i = 0; i < size; i++) {
                TypeId a_type = a.get(i);
                TypeId b_type = b.get(i);

                int out = context.type_comparator().compare(a_type, b_type);

                if (out != 0) {
                    return out;
                }
            }

            if (a.size() < b.size()) {
                return -1;
            } else if (a.size() > b.size()) {
                return 1;
            } else {
                return 0;
            }
        };
    }

    public TypeList(TypeId... types) {
        super(types);
    }

    @Override
    protected TypeId check(TypeId type) {
        return Objects.requireNonNull(type,
                "TypeList can`t contain null type");
    }

    public static TypeList read(RandomInput in, ReadContext context) {
        TypeList out = new TypeList();
        int size = in.readInt();
        for (int i = 0; i < size; i++) {
            out.add(context.type(in.readUnsignedShort()));
        }
        return out;
    }

    public static TypeList empty() {
        return new TypeList();
    }

    public void fillContext(DataSet data) {
        for (TypeId tmp : this) {
            data.addType(tmp);
        }
    }

    public void write(WriteContext context, RandomOutput out) {
        out.writeInt(size());
        for (TypeId tmp : this) {
            out.writeShort(context.getTypeIndex(tmp));
        }
    }

    @Override
    public String toString() {
        return stream().map((p) -> p.toString())
                .collect(Collectors.joining("", "(", ")"));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TypeList) {
            return super.equals(obj);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public TypeList clone() {
        TypeList out = new TypeList();
        out.addAll(this);
        return out;
    }
}
