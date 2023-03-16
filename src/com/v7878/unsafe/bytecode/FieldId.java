package com.v7878.unsafe.bytecode;

import com.v7878.unsafe.io.RandomInput;
import java.util.Objects;

public class FieldId extends FieldOrMethodId {

    public TypeId type;

    public static FieldId read(RandomInput in, Context context) {
        FieldId out = new FieldId();
        out.declaring_class = context.type(in.readUnsignedShort());
        out.type = context.type(in.readUnsignedShort());
        out.name = context.string(in.readInt());
        return out;
    }

    public void fillContext(DataSet data) {
        data.addType(type);
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
