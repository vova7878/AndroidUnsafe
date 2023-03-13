package com.v7878.unsafe.bytecode;

import com.v7878.unsafe.io.RandomInput;
import java.util.Objects;

public class MethodId extends FieldOrMethodId {

    public ProtoId proto;

    public static MethodId read(RandomInput in, ReadContext rc) {
        MethodId out = new MethodId();
        out.declaring_class = rc.types[in.readUnsignedShort()];
        out.proto = rc.protos[in.readUnsignedShort()];
        out.name = rc.strings[in.readInt()];
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
