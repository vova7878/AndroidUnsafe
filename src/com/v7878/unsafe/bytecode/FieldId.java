package com.v7878.unsafe.bytecode;

import com.v7878.unsafe.io.RandomInput;
import java.util.Objects;

public class FieldId extends FieldOrMethodId {

    public TypeId type;

    public static FieldId read(RandomInput in, ReadContext rc) {
        FieldId out = new FieldId();
        out.declaring_class = rc.types[in.readUnsignedShort()];
        out.type = rc.types[in.readUnsignedShort()];
        out.name = rc.strings[in.readInt()];
        return out;
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
