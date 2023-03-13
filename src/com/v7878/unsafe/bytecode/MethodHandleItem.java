package com.v7878.unsafe.bytecode;

import static com.v7878.unsafe.bytecode.DexConstants.*;
import com.v7878.unsafe.io.RandomInput;
import java.util.Objects;

public class MethodHandleItem {

    public int type;
    public FieldOrMethodId field_or_method;

    public static MethodHandleItem read(RandomInput in, ReadContext rc) {
        MethodHandleItem out = new MethodHandleItem();
        out.type = in.readUnsignedShort();
        in.skipBytes(2); //unused
        int field_or_method_id = in.readUnsignedShort();
        in.skipBytes(2); //unused
        out.field_or_method = isMethodType(out.type)
                ? rc.methods[field_or_method_id]
                : rc.fields[field_or_method_id];
        return out;
    }

    public static boolean isMethodType(int type) {
        return type >= METHOD_HANDLE_TYPE_INVOKE_STATIC;
    }

    @Override
    public String toString() {
        return "MethodHandle{" + "type = " + type + "; " + field_or_method + "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MethodHandleItem) {
            MethodHandleItem mhobj = (MethodHandleItem) obj;
            return Objects.equals(type, mhobj.type)
                    && Objects.equals(field_or_method, mhobj.field_or_method);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, field_or_method);
    }
}
