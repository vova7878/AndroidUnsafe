package com.v7878.unsafe.dex;

import static com.v7878.unsafe.Utils.assert_;
import static com.v7878.unsafe.dex.DexConstants.METHOD_HANDLE_TYPE_INVOKE_STATIC;
import static com.v7878.unsafe.dex.DexConstants.METHOD_HANDLE_TYPE_MAX;
import static com.v7878.unsafe.dex.DexConstants.METHOD_HANDLE_TYPE_MIN;

import com.v7878.unsafe.io.RandomInput;
import com.v7878.unsafe.io.RandomOutput;

import java.util.Comparator;
import java.util.Objects;

public class MethodHandleItem implements PublicCloneable {

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
                return context.method_comparator()
                        .compare((MethodId) a.field_or_method,
                                (MethodId) b.field_or_method);
            } else {
                return context.field_comparator()
                        .compare((FieldId) a.field_or_method,
                                (FieldId) b.field_or_method);
            }
        };
    }

    private int type;
    private FieldOrMethodId field_or_method;

    public MethodHandleItem(int type, FieldOrMethodId field_or_method) {
        setType(type);
        setFieldOrMethod(field_or_method);
    }

    public final void setType(int type) {
        assert_(type >= METHOD_HANDLE_TYPE_MIN && type <= METHOD_HANDLE_TYPE_MAX,
                IllegalArgumentException::new, "illegal method handle type: " + type);
        this.type = type;
    }

    public final int getType() {
        return type;
    }

    public final void setFieldOrMethod(FieldOrMethodId field_or_method) {
        this.field_or_method = Objects.requireNonNull(field_or_method,
                "field_or_method can`t be null").clone();
    }

    public final FieldOrMethodId getFieldOrMethod() {
        return field_or_method;
    }

    public static MethodHandleItem read(RandomInput in, ReadContext context) {
        int type = in.readUnsignedShort();
        in.skipBytes(2); //unused
        int field_or_method_id = in.readUnsignedShort();
        in.skipBytes(2); //unused
        FieldOrMethodId field_or_method = isMethodType(type)
                ? context.method(field_or_method_id)
                : context.field(field_or_method_id);
        return new MethodHandleItem(type, field_or_method);
    }

    public void write(WriteContext context, RandomOutput out) {
        out.writeShort(type);
        out.writeShort(0);
        out.writeShort(isMethodType(type)
                ? context.getMethodIndex((MethodId) field_or_method)
                : context.getFieldIndex((FieldId) field_or_method));
        out.writeShort(0);
    }

    public void collectData(DataCollector data) {
        if (isMethodType(type)) {
            data.add((MethodId) field_or_method);
        } else {
            data.add((FieldId) field_or_method);
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

    @Override
    public MethodHandleItem clone() {
        return new MethodHandleItem(type, field_or_method);
    }
}
