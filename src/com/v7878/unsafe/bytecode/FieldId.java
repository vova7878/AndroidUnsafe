package com.v7878.unsafe.bytecode;

import com.v7878.unsafe.io.*;
import java.util.*;

public class FieldId extends FieldOrMethodId implements Cloneable {

    public static final int SIZE = 0x08;

    public static final Comparator<FieldId> getComparator(WriteContext context) {
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

            return context.type_comparator()
                    .compare(a.type, b.type);
        };
    }

    private TypeId type;

    public FieldId(TypeId declaring_class, TypeId type, String name) {
        super(declaring_class, name);
        setType(type);
    }

    public final void setType(TypeId type) {
        this.type = Objects.requireNonNull(type,
                "type can`t be null").clone();
    }

    public final TypeId getType() {
        return type;
    }

    public static FieldId read(RandomInput in, ReadContext context) {
        return new FieldId(
                context.type(in.readUnsignedShort()),
                context.type(in.readUnsignedShort()),
                context.string(in.readInt())
        );
    }

    @Override
    public void fillContext(DataSet data) {
        data.addType(type);
        super.fillContext(data);
    }

    public void write(WriteContext context, RandomOutput out) {
        out.writeShort(context.getTypeIndex(getDeclaringClass()));
        out.writeShort(context.getTypeIndex(type));
        out.writeInt(context.getStringIndex(getName()));
    }

    @Override
    public String toString() {
        return getDeclaringClass() + "." + getName() + ":" + type;
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

    @Override
    public FieldId clone() {
        return new FieldId(getDeclaringClass(), type, getName());
    }
}
