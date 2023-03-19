package com.v7878.unsafe.bytecode;

import com.v7878.unsafe.io.*;
import java.util.*;

public class MethodId extends FieldOrMethodId {

    public static final int SIZE = 0x08;

    public static final Comparator<MethodId> getComparator(WriteContext context) {
        return (a, b) -> {
            if (a.equals(b)) {
                return 0;
            }

            int out = context.type_comparator
                    .compare(a.declaring_class, b.declaring_class);
            if (out != 0) {
                return out;
            }

            out = StringId.COMPARATOR
                    .compare(a.name, b.name);
            if (out != 0) {
                return out;
            }

            return context.proto_comparator
                    .compare(a.proto, b.proto);
        };
    }

    public ProtoId proto;

    public static MethodId read(RandomInput in, Context context) {
        MethodId out = new MethodId();
        out.declaring_class = context.type(in.readUnsignedShort());
        out.proto = context.proto(in.readUnsignedShort());
        out.name = context.string(in.readInt());
        return out;
    }

    @Override
    public void fillContext(DataSet data) {
        data.addProto(proto);
        super.fillContext(data);
    }

    public void write(WriteContext context, RandomOutput out) {
        out.writeShort(context.getTypeIndex(declaring_class));
        out.writeShort(context.getProtoIndex(proto));
        out.writeInt(context.getStringIndex(name));
    }

    @Override
    public String toString() {
        return declaring_class + "." + name + proto;
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
}
