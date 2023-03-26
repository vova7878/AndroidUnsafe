package com.v7878.unsafe.dex;

import com.v7878.unsafe.io.*;
import java.lang.reflect.*;
import java.util.*;

public class ProtoId implements PublicCloneable {

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

    public static ProtoId of(Executable ex) {
        Objects.requireNonNull(ex, "trying to get ProtoId of null");
        Class<?> return_type = ex instanceof Method
                ? ((Method) ex).getReturnType() : void.class;
        return new ProtoId(TypeId.of(return_type),
                Arrays.stream(ex.getParameterTypes())
                        .map(TypeId::of).toArray(TypeId[]::new));
    }

    private TypeId return_type;
    private TypeList parameters;

    public ProtoId(TypeId return_type, TypeList parameters) {
        setReturnType(return_type);
        setParameters(parameters);
    }

    public ProtoId(TypeId return_type, TypeId... parameters) {
        this(return_type, new TypeList(parameters));
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
        TypeList parameters = null;
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

    public void collectData(DataCollector data) {
        data.add(getShorty());
        data.add(return_type);
        if (!parameters.isEmpty()) {
            data.add(parameters);
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
