package com.v7878.unsafe.bytecode;

import com.v7878.unsafe.io.RandomInput;
import java.util.Objects;

public class MethodId extends FieldOrMethodId {

    public ProtoId proto;

    public static MethodId read(RandomInput in, Context context) {
        MethodId out = new MethodId();
        out.declaring_class = context.type(in.readUnsignedShort());
        out.proto = context.proto(in.readUnsignedShort());
        out.name = context.string(in.readInt());
        return out;
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
