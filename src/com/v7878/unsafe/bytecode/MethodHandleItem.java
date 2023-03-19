package com.v7878.unsafe.bytecode;

import static com.v7878.unsafe.bytecode.DexConstants.*;
import com.v7878.unsafe.io.*;
import java.util.*;

public class MethodHandleItem {

    public static final int SIZE = 0x08;

    public static final Comparator<MethodHandleItem> getComparator(WriteContext context) {
        return (a, b) -> {
            if (a.equals(b)) {
                return 0;
            }

            int out = Integer.compare(a.type, b.type);
            if (out != 0) {
                return out;
            }

            // now a.type == b.type
            if (isMethodType(a.type)) {
                return context.method_comparator
                        .compare((MethodId) a.field_or_method,
                                (MethodId) b.field_or_method);
            } else {
                return context.field_comparator
                        .compare((FieldId) a.field_or_method,
                                (FieldId) b.field_or_method);
            }
        };
    }

    public int type;
    public FieldOrMethodId field_or_method;

    public static MethodHandleItem read(RandomInput in, Context context) {
        MethodHandleItem out = new MethodHandleItem();
        out.type = in.readUnsignedShort();
        in.skipBytes(2); //unused
        int field_or_method_id = in.readUnsignedShort();
        in.skipBytes(2); //unused
        out.field_or_method = isMethodType(out.type)
                ? context.method(field_or_method_id)
                : context.field(field_or_method_id);
        return out;
    }

    public void write(WriteContext context, RandomOutput out) {
        out.writeShort(type);
        out.writeShort(0);
        out.writeShort(isMethodType(type)
                ? context.getMethodIndex((MethodId) field_or_method)
                : context.getFieldIndex((FieldId) field_or_method));
        out.writeShort(0);
    }

    public void fillContext(DataSet data) {
        if (isMethodType(type)) {
            data.addMethod((MethodId) field_or_method);
        } else {
            data.addField((FieldId) field_or_method);
        }
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
