package com.v7878.unsafe.dex;

import static com.v7878.unsafe.dex.DexConstants.*;
import com.v7878.unsafe.dex.EncodedValue.*;
import com.v7878.unsafe.io.RandomInput;

public class EncodedValueReader {

    private static final int MUST_READ = -1;

    public static int peek(RandomInput in, int type) {
        if (type == MUST_READ) {
            return in.readUnsignedByte();
        }
        return type;
    }

    public static BooleanValue readBoolean(int arg) {
        return new BooleanValue(arg != 0);
    }

    public static ByteValue readByte(RandomInput in, int arg) {
        ByteValue out = new ByteValue();
        out.value = (byte) ValueCoder.readSignedInt(in, arg);
        return out;
    }

    public static ShortValue readShort(RandomInput in, int arg) {
        ShortValue out = new ShortValue();
        out.value = (short) ValueCoder.readSignedInt(in, arg);
        return out;
    }

    public static CharValue readChar(RandomInput in, int arg) {
        CharValue out = new CharValue();
        out.value = (char) ValueCoder.readUnsignedInt(in, arg, false);
        return out;
    }

    public static IntValue readInt(RandomInput in, int arg) {
        IntValue out = new IntValue();
        out.value = ValueCoder.readSignedInt(in, arg);
        return out;
    }

    public static LongValue readLong(RandomInput in, int arg) {
        LongValue out = new LongValue();
        out.value = ValueCoder.readSignedLong(in, arg);
        return out;
    }

    public static FloatValue readFloat(RandomInput in, int arg) {
        FloatValue out = new FloatValue();
        out.value = Float.intBitsToFloat(
                ValueCoder.readUnsignedInt(in, arg, true));
        return out;
    }

    public static DoubleValue readDouble(RandomInput in, int arg) {
        DoubleValue out = new DoubleValue();
        out.value = Double.longBitsToDouble(
                ValueCoder.readUnsignedLong(in, arg, true));
        return out;
    }

    public static MethodTypeValue readMethodType(RandomInput in,
            int arg, ReadContext context) {
        ProtoId value = context.proto(
                ValueCoder.readUnsignedInt(in, arg, false));
        return new MethodTypeValue(value);
    }

    public static MethodHandleValue readMethodHandle(RandomInput in,
            int arg, ReadContext context) {
        MethodHandleItem value = context.method_handle(
                ValueCoder.readUnsignedInt(in, arg, false));
        return new MethodHandleValue(value);
    }

    public static StringValue readString(RandomInput in,
            int arg, ReadContext context) {
        String value = context.string(
                ValueCoder.readUnsignedInt(in, arg, false));
        return new StringValue(value);
    }

    public static TypeValue readType(RandomInput in,
            int arg, ReadContext context) {
        TypeId value = context.type(
                ValueCoder.readUnsignedInt(in, arg, false));
        return new TypeValue(value);
    }

    public static FieldValue readField(RandomInput in,
            int arg, ReadContext context) {
        FieldId value = context.field(
                ValueCoder.readUnsignedInt(in, arg, false));
        return new FieldValue(value);
    }

    public static EnumValue readEnum(RandomInput in,
            int arg, ReadContext context) {
        FieldId value = context.field(
                ValueCoder.readUnsignedInt(in, arg, false));
        return new EnumValue(value);
    }

    public static MethodValue readMethod(RandomInput in,
            int arg, ReadContext context) {
        MethodId value = context.method(
                ValueCoder.readUnsignedInt(in, arg, false));
        return new MethodValue(value);
    }

    public static ArrayValue readArray(RandomInput in,
            ReadContext context) {
        int size = in.readULeb128();
        EncodedValue[] value = new EncodedValue[size];
        for (int i = 0; i < size; i++) {
            value[i] = readValue(in, context);
        }
        return new ArrayValue(value);
    }

    public static AnnotationValue readAnnotation(RandomInput in,
            ReadContext context) {
        EncodedAnnotation value = EncodedAnnotation.read(in, context);
        return new AnnotationValue(value);
    }

    public static EncodedValue readValue(RandomInput in, ReadContext context) {
        return readValue(in, context, MUST_READ);
    }

    public static EncodedValue readValue(RandomInput in, ReadContext context, int type) {
        type = peek(in, type);
        int arg = (type & 0xe0) >> 5;
        type = type & 0x1f;
        switch (type) {
            case VALUE_BOOLEAN:
                return readBoolean(arg);
            case VALUE_BYTE:
                return readByte(in, arg);
            case VALUE_SHORT:
                return readShort(in, arg);
            case VALUE_CHAR:
                return readChar(in, arg);
            case VALUE_INT:
                return readInt(in, arg);
            case VALUE_LONG:
                return readLong(in, arg);
            case VALUE_FLOAT:
                return readFloat(in, arg);
            case VALUE_DOUBLE:
                return readDouble(in, arg);
            case VALUE_METHOD_TYPE:
                return readMethodType(in, arg, context);
            case VALUE_METHOD_HANDLE:
                return readMethodHandle(in, arg, context);
            case VALUE_STRING:
                return readString(in, arg, context);
            case VALUE_TYPE:
                return readType(in, arg, context);
            case VALUE_FIELD:
                return readField(in, arg, context);
            case VALUE_ENUM:
                return readEnum(in, arg, context);
            case VALUE_METHOD:
                return readMethod(in, arg, context);
            case VALUE_ARRAY:
                return readArray(in, context);
            case VALUE_ANNOTATION:
                return readAnnotation(in, context);
            case VALUE_NULL:
                return new NullValue();
            default:
                throw new RuntimeException("Unexpected type: " + Integer.toHexString(type));
        }
    }
}
