package com.v7878.unsafe.bytecode;

import com.v7878.unsafe.io.*;
import java.util.*;

public class FieldId extends FieldOrMethodId {

    public static final int SIZE = 0x08;

    public static final Comparator<FieldId> getComparator(WriteContext context) {
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

            return context.type_comparator
                    .compare(a.type, b.type);
        };
    }

    public TypeId type;

    public static FieldId read(RandomInput in, Context context) {
        FieldId out = new FieldId();
        out.declaring_class = context.type(in.readUnsignedShort());
        out.type = context.type(in.readUnsignedShort());
        out.name = context.string(in.readInt());
        return out;
    }

    @Override
    public void fillContext(DataSet data) {
        data.addType(type);
        super.fillContext(data);
    }

    public void write(WriteContext context, RandomOutput out) {
        out.writeShort(context.getTypeIndex(declaring_class));
        out.writeShort(context.getTypeIndex(type));
        out.writeInt(context.getStringIndex(name));
    }

    @Override
    public String toString() {
        return declaring_class + "." + name + ":" + type;
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        if (obj instanceof FieldId) {
            FieldId fobj = (FieldId) obj;
            return Objects.equals(type, fobj.type);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), type);
    }
}
