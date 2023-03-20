package com.v7878.unsafe.bytecode;

import static com.v7878.unsafe.bytecode.DexConstants.*;
import java.util.Arrays;
import java.util.Objects;

public abstract class EncodedValue implements Cloneable {

    public final int type;

    private EncodedValue(int type) {
        this.type = type;
    }

    public abstract boolean isDefault();

    public abstract Object getValue();

    public void fillContext(DataSet data) {
    }

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

    @Override
    public abstract EncodedValue clone();

    public static EncodedValue getDefaultValue(TypeId type) {
        String name = type.getDescriptor();
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

    public static class BooleanValue extends EncodedValue implements Cloneable {

        public boolean value;

        public BooleanValue() {
            super(VALUE_BOOLEAN);
        }

        public BooleanValue(boolean value) {
            this();
            this.value = value;
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

        @Override
        public BooleanValue clone() {
            return new BooleanValue(value);
        }
    }

    public static class ByteValue extends EncodedValue implements Cloneable {

        public byte value;

        public ByteValue() {
            super(VALUE_BYTE);
        }

        public ByteValue(byte value) {
            this();
            this.value = value;
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

        @Override
        public ByteValue clone() {
            return new ByteValue(value);
        }
    }

    public static class ShortValue extends EncodedValue implements Cloneable {

        public short value;

        public ShortValue() {
            super(VALUE_SHORT);
        }

        public ShortValue(short value) {
            this();
            this.value = value;
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

        @Override
        public ShortValue clone() {
            return new ShortValue(value);
        }
    }

    public static class CharValue extends EncodedValue implements Cloneable {

        public char value;

        public CharValue() {
            super(VALUE_CHAR);
        }

        public CharValue(char value) {
            this();
            this.value = value;
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

        @Override
        public CharValue clone() {
            return new CharValue(value);
        }
    }

    public static class IntValue extends EncodedValue implements Cloneable {

        public int value;

        public IntValue() {
            super(VALUE_INT);
        }

        public IntValue(int value) {
            this();
            this.value = value;
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

        @Override
        public IntValue clone() {
            return new IntValue(value);
        }
    }

    public static class LongValue extends EncodedValue implements Cloneable {

        public long value;

        public LongValue() {
            super(VALUE_LONG);
        }

        public LongValue(long value) {
            this();
            this.value = value;
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

        @Override
        public LongValue clone() {
            return new LongValue(value);
        }
    }

    public static class FloatValue extends EncodedValue implements Cloneable {

        public float value;

        public FloatValue() {
            super(VALUE_FLOAT);
        }

        public FloatValue(float value) {
            this();
            this.value = value;
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

        @Override
        public FloatValue clone() {
            return new FloatValue(value);
        }
    }

    public static class DoubleValue extends EncodedValue implements Cloneable {

        public double value;

        public DoubleValue() {
            super(VALUE_DOUBLE);
        }

        public DoubleValue(double value) {
            this();
            this.value = value;
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

        @Override
        public DoubleValue clone() {
            return new DoubleValue(value);
        }
    }

    public static class NullValue extends EncodedValue implements Cloneable {

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

        @Override
        public NullValue clone() {
            return new NullValue();
        }
    }

    public static class MethodTypeValue extends EncodedValue implements Cloneable {

        private ProtoId value;

        public MethodTypeValue() {
            super(VALUE_METHOD_TYPE);
        }

        public MethodTypeValue(ProtoId value) {
            this();
            setValue(value);
        }

        @Override
        public void fillContext(DataSet data) {
            if (value != null) {
                data.addProto(value);
            }
        }

        @Override
        public boolean isDefault() {
            return value == null;
        }

        @Override
        public String toString() {
            return Objects.toString(value);
        }

        public final void setValue(ProtoId value) {
            this.value = value == null ? null : value.clone();
        }

        @Override
        public final ProtoId getValue() {
            return value;
        }

        @Override
        public MethodTypeValue clone() {
            return new MethodTypeValue(value);
        }
    }

    public static class MethodHandleValue extends EncodedValue implements Cloneable {

        private MethodHandleItem value;

        public MethodHandleValue() {
            super(VALUE_METHOD_HANDLE);
        }

        public MethodHandleValue(MethodHandleItem value) {
            this();
            setValue(value);
        }

        @Override
        public void fillContext(DataSet data) {
            if (value != null) {
                data.addMethodHandle(value);
            }
        }

        @Override
        public boolean isDefault() {
            return value == null;
        }

        @Override
        public String toString() {
            return Objects.toString(value);
        }

        public final void setValue(MethodHandleItem value) {
            this.value = value == null ? null : value.clone();
        }

        @Override
        public final MethodHandleItem getValue() {
            return value;
        }

        @Override
        public MethodHandleValue clone() {
            return new MethodHandleValue(value);
        }
    }

    public static class StringValue extends EncodedValue implements Cloneable {

        private String value;

        public StringValue() {
            super(VALUE_STRING);
        }

        public StringValue(String value) {
            this();
            setValue(value);
        }

        @Override
        public void fillContext(DataSet data) {
            if (value != null) {
                data.addString(value);
            }
        }

        @Override
        public boolean isDefault() {
            return value == null;
        }

        @Override
        public String toString() {
            return Objects.toString(value);
        }

        public final void setValue(String value) {
            this.value = value;
        }

        @Override
        public final String getValue() {
            return value;
        }

        @Override
        public StringValue clone() {
            return new StringValue(value);
        }
    }

    public static class TypeValue extends EncodedValue implements Cloneable {

        private TypeId value;

        public TypeValue() {
            super(VALUE_TYPE);
        }

        public TypeValue(TypeId value) {
            this();
            setValue(value);
        }

        @Override
        public void fillContext(DataSet data) {
            if (value != null) {
                data.addType(value);
            }
        }

        @Override
        public boolean isDefault() {
            return value == null;
        }

        @Override
        public String toString() {
            return Objects.toString(value);
        }

        public final void setValue(TypeId value) {
            this.value = value == null ? null : value.clone();
        }

        @Override
        public final TypeId getValue() {
            return value;
        }

        @Override
        public TypeValue clone() {
            return new TypeValue(value);
        }
    }

    public static class FieldValue extends EncodedValue implements Cloneable {

        private FieldId value;

        public FieldValue() {
            super(VALUE_FIELD);
        }

        public FieldValue(FieldId value) {
            this();
            setValue(value);
        }

        @Override
        public void fillContext(DataSet data) {
            if (value != null) {
                data.addField(value);
            }
        }

        @Override
        public boolean isDefault() {
            return value == null;
        }

        @Override
        public String toString() {
            return Objects.toString(value);
        }

        public final void setValue(FieldId value) {
            this.value = value == null ? null : value.clone();
        }

        @Override
        public final FieldId getValue() {
            return value;
        }

        @Override
        public FieldValue clone() {
            return new FieldValue(value);
        }
    }

    public static class MethodValue extends EncodedValue implements Cloneable {

        private MethodId value;

        public MethodValue() {
            super(VALUE_METHOD);
        }

        public MethodValue(MethodId value) {
            this();
            setValue(value);
        }

        @Override
        public void fillContext(DataSet data) {
            if (value != null) {
                data.addMethod(value);
            }
        }

        @Override
        public boolean isDefault() {
            return value == null;
        }

        @Override
        public String toString() {
            return Objects.toString(value);
        }

        public final void setValue(MethodId value) {
            this.value = value == null ? null : value.clone();
        }

        @Override
        public final MethodId getValue() {
            return value;
        }

        @Override
        public MethodValue clone() {
            return new MethodValue(value);
        }
    }

    public static class EnumValue extends EncodedValue implements Cloneable {

        private FieldId value;

        public EnumValue() {
            super(VALUE_ENUM);
        }

        public EnumValue(FieldId value) {
            this();
            setValue(value);
        }

        @Override
        public void fillContext(DataSet data) {
            if (value != null) {
                data.addField(value);
            }
        }

        @Override
        public boolean isDefault() {
            return value == null;
        }

        @Override
        public String toString() {
            return Objects.toString(value);
        }

        public final void setValue(FieldId value) {
            this.value = value == null ? null : value.clone();
        }

        @Override
        public final FieldId getValue() {
            return value;
        }

        @Override
        public EnumValue clone() {
            return new EnumValue(value);
        }
    }

    public static class ArrayValue extends EncodedValue implements Cloneable {

        private EncodedValue[] value;

        public ArrayValue() {
            super(VALUE_ARRAY);
        }

        public ArrayValue(EncodedValue[] value) {
            this();
            setValue(value);
        }

        @Override
        public void fillContext(DataSet data) {
            if (value != null) {
                for (EncodedValue tmp : value) {
                    tmp.fillContext(data);
                }
            }
        }

        @Override
        public boolean isDefault() {
            return value == null;
        }

        @Override
        public String toString() {
            return Arrays.toString(value);
        }

        public final void setValue(EncodedValue[] value) {
            this.value = value == null ? null
                    : Arrays.stream(value)
                            .map(EncodedValue::clone)
                            .toArray(EncodedValue[]::new);
        }

        @Override
        public final EncodedValue[] getValue() {
            return value == null ? null
                    : Arrays.copyOf(value, value.length);
        }

        @Override
        public ArrayValue clone() {
            return new ArrayValue(value);
        }
    }

    public static class AnnotationValue extends EncodedValue implements Cloneable {

        private EncodedAnnotation value;

        public AnnotationValue() {
            super(VALUE_ANNOTATION);
        }

        public AnnotationValue(EncodedAnnotation value) {
            this();
            setValue(value);
        }

        @Override
        public void fillContext(DataSet data) {
            if (value != null) {
                value.fillContext(data);
            }
        }

        @Override
        public boolean isDefault() {
            return value == null;
        }

        @Override
        public String toString() {
            return Objects.toString(value);
        }

        public final void setValue(EncodedAnnotation value) {
            this.value = value == null ? null : value.clone();
        }

        @Override
        public final EncodedAnnotation getValue() {
            return value;
        }

        @Override
        public AnnotationValue clone() {
            return new AnnotationValue(value);
        }
    }
}
