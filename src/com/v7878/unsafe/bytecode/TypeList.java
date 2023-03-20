package com.v7878.unsafe.bytecode;

import com.v7878.unsafe.io.*;
import java.util.*;
import java.util.stream.*;

public class TypeList extends AbstractList<TypeId> implements Cloneable {

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

    private final List<TypeId> types;

    public TypeList(TypeId... types) {
        if (types == null) {
            types = new TypeId[0];
        }
        this.types = new ArrayList<>(types.length);
        addAll(Arrays.asList(types));
    }

    private TypeId check(TypeId type) {
        return Objects.requireNonNull(type,
                "TypeList can`t contain null type");
    }

    @Override
    public final void add(int index, TypeId type) {
        types.add(check(type).clone());
    }

    @Override
    public final TypeId set(int index, TypeId type) {
        return types.set(index, check(type).clone());
    }

    @Override
    public final TypeId get(int index) {
        return types.get(index);
    }

    @Override
    public final TypeId remove(int index) {
        return types.remove(index);
    }

    @Override
    public final int size() {
        return types.size();
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
        for (TypeId tmp : types) {
            data.addType(tmp);
        }
    }

    public void write(WriteContext context, RandomOutput out) {
        out.writeInt(size());
        for (TypeId tmp : types) {
            out.writeShort(context.getTypeIndex(tmp));
        }
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public String toString() {
        return types.stream()
                .map((p) -> p.toString())
                .collect(Collectors.joining("", "(", ")"));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TypeList) {
            TypeList tlobj = (TypeList) obj;
            return Objects.equals(types, tlobj.types);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(types);
    }

    @Override
    public TypeList clone() {
        TypeList out = new TypeList();
        out.addAll(types);
        return out;
    }
}
