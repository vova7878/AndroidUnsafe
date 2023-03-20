package com.v7878.unsafe.bytecode;

import com.v7878.unsafe.io.*;
import java.util.*;

public class ProtoId implements Cloneable {

    public static final int SIZE = 0x0c;

    public static final Comparator<ProtoId> getComparator(WriteContext context) {
        return (a, b) -> {
            if (a.equals(b)) {
                return 0;
            }

            int out = context.type_comparator()
                    .compare(a.return_type, b.return_type);
            if (out != 0) {
                return out;
            }

            return context.type_list_comparator()
                    .compare(a.parameters, b.parameters);
        };
    }

    private TypeId return_type;
    private TypeList parameters;

    public ProtoId(TypeId return_type, TypeList parameters) {
        setReturnType(return_type);
        setParameters(parameters);
    }

    public final void setReturnType(TypeId return_type) {
        this.return_type = Objects.requireNonNull(return_type,
                "return_type can`t be null").clone();
    }

    public final TypeId getReturnType() {
        return return_type;
    }

    public final void setParameters(TypeList parameters) {
        this.parameters = parameters == null
                ? TypeList.empty() : parameters.clone();
    }

    public final TypeList getParameters() {
        return parameters;
    }

    public static ProtoId read(RandomInput in, ReadContext context) {
        in.readInt(); // shorty
        TypeId return_type = context.type(in.readInt());
        int parameters_off = in.readInt();
        TypeList parameters = TypeList.empty();
        if (parameters_off != 0) {
            parameters = TypeList.read(in.duplicate(parameters_off), context);
        }
        return new ProtoId(return_type, parameters);
    }

    public String getShorty() {
        StringBuilder out = new StringBuilder();
        out.append(return_type.getShorty());
        for (TypeId tmp : parameters) {
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

    @Override
    public ProtoId clone() {
        return new ProtoId(return_type, parameters);
    }
}
