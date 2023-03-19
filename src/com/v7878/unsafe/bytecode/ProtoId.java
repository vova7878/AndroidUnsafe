package com.v7878.unsafe.bytecode;

import com.v7878.unsafe.bytecode.TypeId.*;
import com.v7878.unsafe.io.*;
import java.util.*;

public class ProtoId {

    public static final int SIZE = 0x0c;

    public static final Comparator<ProtoId> getComparator(WriteContext context) {
        return (a, b) -> {
            if (a.equals(b)) {
                return 0;
            }

            int out = context.type_comparator
                    .compare(a.return_type, b.return_type);
            if (out != 0) {
                return out;
            }

            return context.type_list_comparator
                    .compare(a.parameters, b.parameters);
        };
    }

    public TypeId return_type;
    public TypeList parameters;

    public static ProtoId read(RandomInput in, Context context) {
        ProtoId out = new ProtoId();
        in.readInt(); // shorty
        out.return_type = context.type(in.readInt());
        int parameters_off = in.readInt();
        if (parameters_off != 0) {
            out.parameters = TypeList.read(in.duplicate(parameters_off), context);
        } else {
            out.parameters = TypeList.empty();
        }
        return out;
    }

    public String getShorty() {
        StringBuilder out = new StringBuilder();
        out.append(return_type.getShorty());
        for (TypeId tmp : parameters.list) {
            out.append(tmp.getShorty());
        }
        return out.toString();
    }

    public void fillContext(DataSet data) {
        data.addString(getShorty());
        data.addType(return_type);
        if (!parameters.isEmpty()) {
            data.addTypeList(parameters);
        }
    }

    public void write(WriteContext context, RandomOutput out) {
        out.writeInt(context.getStringIndex(getShorty()));
        out.writeInt(context.getTypeIndex(return_type));
        out.writeInt(parameters.isEmpty() ? 0
                : context.getTypeListOffset(parameters));
    }

    @Override
    public String toString() {
        return "" + parameters + return_type;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ProtoId) {
            ProtoId pobj = (ProtoId) obj;
            return Objects.equals(return_type, pobj.return_type)
                    && Objects.equals(parameters, pobj.parameters);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(return_type, parameters);
    }
}
