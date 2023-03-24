package com.v7878.unsafe.dex;

import com.v7878.unsafe.io.*;
import java.lang.reflect.*;
import java.util.*;

public class MethodId extends FieldOrMethodId {

    public static final int SIZE = 0x08;

    public static final Comparator<MethodId> getComparator(WriteContext context) {
        return (a, b) -> {
            if (a.equals(b)) {
                return 0;
            }

            int out = context.type_comparator()
                    .compare(a.getDeclaringClass(), b.getDeclaringClass());
            if (out != 0) {
                return out;
            }

            out = StringId.COMPARATOR
                    .compare(a.getName(), b.getName());
            if (out != 0) {
                return out;
            }

            return context.proto_comparator()
                    .compare(a.proto, b.proto);
        };
    }

    private static String getName(Executable ex) {
        if (ex instanceof Constructor) {
            return (ex.getModifiers() & Modifier.STATIC) == 0 ? "<init>" : "<clinit>";
        }
        return ex.getName();
    }

    public static MethodId of(Executable ex) {
        Objects.requireNonNull(ex, "trying to get MethodId of null");
        return new MethodId(TypeId.of(ex.getDeclaringClass()),
                ProtoId.of(ex), getName(ex));
    }

    private ProtoId proto;

    public MethodId(TypeId declaring_class, ProtoId proto, String name) {
        super(declaring_class, name);
        setProto(proto);
    }

    public final void setProto(ProtoId proto) {
        this.proto = Objects.requireNonNull(proto,
                "proto can`t be null").clone();
    }

    public final ProtoId getProto() {
        return proto;
    }

    public static MethodId read(RandomInput in, ReadContext context) {
        return new MethodId(
                context.type(in.readUnsignedShort()),
                context.proto(in.readUnsignedShort()),
                context.string(in.readInt())
        );
    }

    @Override
    public void fillContext(DataSet data) {
        data.addProto(proto);
        super.fillContext(data);
    }

    public void write(WriteContext context, RandomOutput out) {
        out.writeShort(context.getTypeIndex(getDeclaringClass()));
        out.writeShort(context.getProtoIndex(proto));
        out.writeInt(context.getStringIndex(getName()));
    }

    @Override
    public String toString() {
        return getDeclaringClass() + "." + getName() + proto;
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        if (obj instanceof MethodId) {
            MethodId mobj = (MethodId) obj;
            return Objects.equals(proto, mobj.proto);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), proto);
    }

    @Override
    public MethodId clone() {
        return new MethodId(getDeclaringClass(), proto, getName());
    }
}
