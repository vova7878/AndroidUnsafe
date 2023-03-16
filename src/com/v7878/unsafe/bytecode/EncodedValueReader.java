package com.v7878.unsafe.bytecode;

import static com.v7878.unsafe.bytecode.DexConstants.*;
import com.v7878.unsafe.bytecode.EncodedValue.*;
import com.v7878.unsafe.io.RandomInput;

public class EncodedValueReader {

    private static final int MUST_READ = -1;

    protected final RandomInput in;
    private int type = MUST_READ;
    private int arg;

    public EncodedValueReader(RandomInput in) {
        this.in = in;
    }

    public EncodedValueReader(RandomInput in, int knownType) {
        this.in = in;
        this.type = knownType;
    }

    public int peek() {
        if (type == MUST_READ) {
            int argAndType = in.readByte() & 0xff;
            type = argAndType & 0x1f;
            arg = (argAndType & 0xe0) >> 5;
        }
        return type;
    }

    public ArrayValue readArray(Context context) {
        checkType(VALUE_ARRAY);
        type = MUST_READ;
        int size = in.readULeb128();
        ArrayValue out = new ArrayValue();
        out.value = new EncodedValue[size];
        for (int i = 0; i < size; i++) {
            out.value[i] = readValue(context);
        }
        return out;
    }

    public AnnotationValue readAnnotation(Context context) {
        checkType(VALUE_ANNOTATION);
        type = MUST_READ;
        AnnotationValue out = new AnnotationValue();
        out.value = EncodedAnnotation.read(in, context);
        return out;
    }

    public ByteValue readByte() {
        checkType(VALUE_BYTE);
        type = MUST_READ;
        ByteValue out = new ByteValue();
        out.value = (byte) ValueCoder.readSignedInt(in, arg);
        return out;
    }

    public ShortValue readShort() {
        checkType(VALUE_SHORT);
        type = MUST_READ;
        ShortValue out = new ShortValue();
        out.value = (short) ValueCoder.readSignedInt(in, arg);
        return out;
    }

    public CharValue readChar() {
        checkType(VALUE_CHAR);
        type = MUST_READ;
        CharValue out = new CharValue();
        out.value = (char) ValueCoder.readUnsignedInt(in, arg, false);
        return out;
    }

    public IntValue readInt() {
        checkType(VALUE_INT);
        type = MUST_READ;
        IntValue out = new IntValue();
        out.value = ValueCoder.readSignedInt(in, arg);
        return out;
    }

    public LongValue readLong() {
        checkType(VALUE_LONG);
        type = MUST_READ;
        LongValue out = new LongValue();
        out.value = ValueCoder.readSignedLong(in, arg);
        return out;
    }

    public FloatValue readFloat() {
        checkType(VALUE_FLOAT);
        type = MUST_READ;
        FloatValue out = new FloatValue();
        out.value = Float.intBitsToFloat(ValueCoder.readUnsignedInt(in, arg, true));
        return out;
    }

    public DoubleValue readDouble() {
        checkType(VALUE_DOUBLE);
        type = MUST_READ;
        DoubleValue out = new DoubleValue();
        out.value = Double.longBitsToDouble(ValueCoder.readUnsignedLong(in, arg, true));
        return out;
    }

    public MethodTypeValue readMethodType(Context context) {
        checkType(VALUE_METHOD_TYPE);
        type = MUST_READ;
        MethodTypeValue out = new MethodTypeValue();
        out.value = context.proto(ValueCoder.readUnsignedInt(in, arg, false));
        return out;
    }

    public MethodHandleValue readMethodHandle(Context context) {
        checkType(VALUE_METHOD_HANDLE);
        type = MUST_READ;
        MethodHandleValue out = new MethodHandleValue();
        out.value = context.method_handle(ValueCoder.readUnsignedInt(in, arg, false));
        return out;
    }

    public StringValue readString(Context context) {
        checkType(VALUE_STRING);
        type = MUST_READ;
        StringValue out = new StringValue();
        out.value = context.string(ValueCoder.readUnsignedInt(in, arg, false));
        return out;
    }

    public TypeValue readType(Context context) {
        checkType(VALUE_TYPE);
        type = MUST_READ;
        TypeValue out = new TypeValue();
        out.value = context.type(ValueCoder.readUnsignedInt(in, arg, false));
        return out;
    }

    public FieldValue readField(Context context) {
        checkType(VALUE_FIELD);
        type = MUST_READ;
        FieldValue out = new FieldValue();
        out.value = context.field(ValueCoder.readUnsignedInt(in, arg, false));
        return out;
    }

    public EnumValue readEnum(Context context) {
        checkType(VALUE_ENUM);
        type = MUST_READ;
        EnumValue out = new EnumValue();
        out.value = context.field(ValueCoder.readUnsignedInt(in, arg, false));
        return out;
    }

    public MethodValue readMethod(Context context) {
        checkType(VALUE_METHOD);
        type = MUST_READ;
        MethodValue out = new MethodValue();
        out.value = context.method(ValueCoder.readUnsignedInt(in, arg, false));
        return out;
    }

    public NullValue readNull() {
        checkType(VALUE_NULL);
        type = MUST_READ;
        return new NullValue();
    }

    public BooleanValue readBoolean() {
        checkType(VALUE_BOOLEAN);
        type = MUST_READ;
        BooleanValue out = new BooleanValue();
        out.value = arg != 0;
        return out;
    }

    public EncodedValue readValue(Context context) {
        switch (peek()) {
            case VALUE_BYTE:
                return readByte();
            case VALUE_SHORT:
                return readShort();
            case VALUE_CHAR:
                return readChar();
            case VALUE_INT:
                return readInt();
            case VALUE_LONG:
                return readLong();
            case VALUE_FLOAT:
                return readFloat();
            case VALUE_DOUBLE:
                return readDouble();
            case VALUE_METHOD_TYPE:
                return readMethodType(context);
            case VALUE_METHOD_HANDLE:
                return readMethodHandle(context);
            case VALUE_STRING:
                return readString(context);
            case VALUE_TYPE:
                return readType(context);
            case VALUE_FIELD:
                return readField(context);
            case VALUE_ENUM:
                return readEnum(context);
            case VALUE_METHOD:
                return readMethod(context);
            case VALUE_ARRAY:
                return readArray(context);
            case VALUE_ANNOTATION:
                return readAnnotation(context);
            case VALUE_NULL:
                return readNull();
            case VALUE_BOOLEAN:
                return readBoolean();
            default:
                throw new RuntimeException("Unexpected type: " + Integer.toHexString(type));
        }
    }

    private void checkType(int expected) {
        if (peek() != expected) {
            throw new IllegalStateException(
                    String.format("Expected %x but was %x", expected, peek()));
        }
    }
}
