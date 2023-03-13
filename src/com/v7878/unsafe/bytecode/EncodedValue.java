package com.v7878.unsafe.bytecode;

import static com.v7878.unsafe.bytecode.DexConstants.*;
import java.util.Arrays;
import java.util.Objects;

public abstract class EncodedValue {

    public final int type;

    private EncodedValue(int type) {
        this.type = type;
    }

    public abstract boolean isDefault();

    public abstract Object getValue();

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof EncodedValue) {
            EncodedValue evobj = (EncodedValue) obj;
            return type == evobj.type && (type == VALUE_NULL
                    || Objects.deepEquals(getValue(), evobj.getValue()));
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(new Object[]{type, getValue()});
    }

    public static EncodedValue getDefaultValue(TypeId type) {
        String name = type.descriptor.data;
        switch (name) {
            case "V":
                throw new IllegalArgumentException();
            case "Z":
                return new BooleanValue();
            case "B":
                return new ByteValue();
            case "S":
                return new ShortValue();
            case "C":
                return new CharValue();
            case "I":
                return new IntValue();
            case "J":
                return new LongValue();
            case "F":
                return new FloatValue();
            case "D":
                return new DoubleValue();
            default:
                return new NullValue();
        }
    }

    public static class BooleanValue extends EncodedValue {

        public boolean value;

        public BooleanValue() {
            super(VALUE_BOOLEAN);
        }

        @Override
        public boolean isDefault() {
            return !value;
        }

        @Override
        public String toString() {
            return Boolean.toString(value);
        }

        @Override
        public Object getValue() {
            return value;
        }
    }

    public static class ByteValue extends EncodedValue {

        public byte value;

        public ByteValue() {
            super(VALUE_BYTE);
        }

        @Override
        public boolean isDefault() {
            return value == 0;
        }

        @Override
        public String toString() {
            return Byte.toString(value);
        }

        @Override
        public Object getValue() {
            return value;
        }
    }

    public static class ShortValue extends EncodedValue {

        public short value;

        public ShortValue() {
            super(VALUE_SHORT);
        }

        @Override
        public boolean isDefault() {
            return value == 0;
        }

        @Override
        public String toString() {
            return Short.toString(value);
        }

        @Override
        public Object getValue() {
            return value;
        }
    }

    public static class CharValue extends EncodedValue {

        public char value;

        public CharValue() {
            super(VALUE_CHAR);
        }

        @Override
        public boolean isDefault() {
            return value == 0;
        }

        @Override
        public String toString() {
            return Character.toString(value);
        }

        @Override
        public Object getValue() {
            return value;
        }
    }

    public static class IntValue extends EncodedValue {

        public int value;

        public IntValue() {
            super(VALUE_INT);
        }

        @Override
        public boolean isDefault() {
            return value == 0;
        }

        @Override
        public String toString() {
            return Integer.toString(value);
        }

        @Override
        public Object getValue() {
            return value;
        }
    }

    public static class LongValue extends EncodedValue {

        public long value;

        public LongValue() {
            super(VALUE_LONG);
        }

        @Override
        public boolean isDefault() {
            return value == 0;
        }

        @Override
        public String toString() {
            return Long.toString(value);
        }

        @Override
        public Object getValue() {
            return value;
        }
    }

    public static class FloatValue extends EncodedValue {

        public float value;

        public FloatValue() {
            super(VALUE_FLOAT);
        }

        @Override
        public boolean isDefault() {
            return value == 0f;
        }

        @Override
        public String toString() {
            return Float.toString(value);
        }

        @Override
        public Object getValue() {
            return value;
        }
    }

    public static class DoubleValue extends EncodedValue {

        public double value;

        public DoubleValue() {
            super(VALUE_DOUBLE);
        }

        @Override
        public boolean isDefault() {
            return value == 0d;
        }

        @Override
        public String toString() {
            return Double.toString(value);
        }

        @Override
        public Object getValue() {
            return value;
        }
    }

    public static class NullValue extends EncodedValue {

        public NullValue() {
            super(VALUE_NULL);
        }

        @Override
        public boolean isDefault() {
            return true;
        }

        @Override
        public String toString() {
            return "null";
        }

        @Override
        public Object getValue() {
            return null;
        }
    }

    public static class MethodTypeValue extends EncodedValue {

        public ProtoId value;

        public MethodTypeValue() {
            super(VALUE_METHOD_TYPE);
        }

        @Override
        public boolean isDefault() {
            return value == null;
        }

        @Override
        public String toString() {
            return Objects.toString(value);
        }

        @Override
        public Object getValue() {
            return value;
        }
    }

    public static class MethodHandleValue extends EncodedValue {

        public MethodHandleItem value;

        public MethodHandleValue() {
            super(VALUE_METHOD_HANDLE);
        }

        @Override
        public boolean isDefault() {
            return value == null;
        }

        @Override
        public String toString() {
            return Objects.toString(value);
        }

        @Override
        public Object getValue() {
            return value;
        }
    }

    public static class StringValue extends EncodedValue {

        public StringId value;

        public StringValue() {
            super(VALUE_STRING);
        }

        @Override
        public boolean isDefault() {
            return value == null;
        }

        @Override
        public String toString() {
            return Objects.toString(value);
        }

        @Override
        public Object getValue() {
            return value;
        }
    }

    public static class TypeValue extends EncodedValue {

        public TypeId value;

        public TypeValue() {
            super(VALUE_TYPE);
        }

        @Override
        public boolean isDefault() {
            return value == null;
        }

        @Override
        public String toString() {
            return Objects.toString(value);
        }

        @Override
        public Object getValue() {
            return value;
        }
    }

    public static class FieldValue extends EncodedValue {

        public FieldId value;

        public FieldValue() {
            super(VALUE_FIELD);
        }

        @Override
        public boolean isDefault() {
            return value == null;
        }

        @Override
        public String toString() {
            return Objects.toString(value);
        }

        @Override
        public Object getValue() {
            return value;
        }
    }

    public static class MethodValue extends EncodedValue {

        public MethodId value;

        public MethodValue() {
            super(VALUE_METHOD);
        }

        @Override
        public boolean isDefault() {
            return value == null;
        }

        @Override
        public String toString() {
            return Objects.toString(value);
        }

        @Override
        public Object getValue() {
            return value;
        }
    }

    public static class EnumValue extends EncodedValue {

        public FieldId value;

        public EnumValue() {
            super(VALUE_ENUM);
        }

        @Override
        public boolean isDefault() {
            return value == null;
        }

        @Override
        public String toString() {
            return Objects.toString(value);
        }

        @Override
        public Object getValue() {
            return value;
        }
    }

    public static class ArrayValue extends EncodedValue {

        public EncodedValue[] value;

        public ArrayValue() {
            super(VALUE_ARRAY);
        }

        @Override
        public boolean isDefault() {
            return value == null;
        }

        @Override
        public String toString() {
            return Arrays.toString(value);
        }

        @Override
        public Object getValue() {
            return value;
        }
    }

    public static class AnnotationValue extends EncodedValue {

        public EncodedAnnotation value;

        public AnnotationValue() {
            super(VALUE_ANNOTATION);
        }

        @Override
        public boolean isDefault() {
            return value == null;
        }

        @Override
        public String toString() {
            return Objects.toString(value);
        }

        @Override
        public Object getValue() {
            return value;
        }
    }
}
