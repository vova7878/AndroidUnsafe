package com.v7878.unsafe.dex;

import static com.v7878.unsafe.dex.DexConstants.*;
import com.v7878.unsafe.io.*;
import java.lang.invoke.*;
import java.lang.reflect.*;
import java.util.*;

public interface EncodedValue extends PublicCloneable {

    public boolean isDefault();

    public int type();

    public Object value();

    public default void fillContext(DataSet data) {
    }

    public void write(WriteContext context, RandomOutput out);

    @Override
    public EncodedValue clone();

    public static EncodedValue defaultValue(TypeId type) {
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

    public static EncodedValue of(Object obj) {
        if (obj == null) {
            return new NullValue();
        }

        if (obj instanceof EncodedValue) {
            return (EncodedValue) obj;
        }

        if (obj instanceof Boolean) {
            return new BooleanValue((Boolean) obj);
        }

        if (obj instanceof Byte) {
            return new ByteValue((Byte) obj);
        }

        if (obj instanceof Short) {
            return new ShortValue((Short) obj);
        }

        if (obj instanceof Character) {
            return new CharValue((Character) obj);
        }

        if (obj instanceof Integer) {
            return new IntValue((Integer) obj);
        }

        if (obj instanceof Float) {
            return new FloatValue((Float) obj);
        }

        if (obj instanceof Long) {
            return new LongValue((Long) obj);
        }

        if (obj instanceof Double) {
            return new DoubleValue((Double) obj);
        }

        if (obj instanceof String) {
            return new StringValue((String) obj);
        }

        if (obj instanceof TypeId) {
            return new TypeValue((TypeId) obj);
        }
        if (obj instanceof Class) {
            return new TypeValue(TypeId.of((Class<?>) obj));
        }

        if (obj instanceof ProtoId) {
            return new MethodTypeValue((ProtoId) obj);
        }

        if (obj instanceof MethodId) {
            return new MethodValue((MethodId) obj);
        }
        if (obj instanceof Executable) {
            return new MethodValue(MethodId.of((Executable) obj));
        }

        if (obj instanceof FieldId) {
            return new FieldValue((FieldId) obj);
        }
        if (obj instanceof Field) {
            return new FieldValue(FieldId.of((Field) obj));
        }

        if (obj instanceof Enum) {
            return new EnumValue(FieldId.of((Enum) obj));
        }

        if (obj instanceof MethodHandleItem) {
            return new MethodHandleValue((MethodHandleItem) obj);
        }
        if (obj instanceof MethodHandle) {
            //TODO: implement by unsafe
            throw new UnsupportedOperationException("not implemented yet");
        }

        if (obj instanceof EncodedAnnotation) {
            return new AnnotationValue((EncodedAnnotation) obj);
        }
        if (obj.getClass().isAnnotation()) {
            //TODO: implement by reflection
            throw new UnsupportedOperationException("not implemented yet");
        }

        if (obj instanceof boolean[]) {
            ArrayValue out = new ArrayValue();
            for (boolean tmp : (boolean[]) obj) {
                out.add(new BooleanValue(tmp));
            }
            return out;
        }

        if (obj instanceof byte[]) {
            ArrayValue out = new ArrayValue();
            for (byte tmp : (byte[]) obj) {
                out.add(new ByteValue(tmp));
            }
            return out;
        }

        if (obj instanceof short[]) {
            ArrayValue out = new ArrayValue();
            for (short tmp : (short[]) obj) {
                out.add(new ShortValue(tmp));
            }
            return out;
        }

        if (obj instanceof char[]) {
            ArrayValue out = new ArrayValue();
            for (char tmp : (char[]) obj) {
                out.add(new CharValue(tmp));
            }
            return out;
        }

        if (obj instanceof int[]) {
            ArrayValue out = new ArrayValue();
            for (int tmp : (int[]) obj) {
                out.add(new IntValue(tmp));
            }
            return out;
        }

        if (obj instanceof float[]) {
            ArrayValue out = new ArrayValue();
            for (float tmp : (float[]) obj) {
                out.add(new FloatValue(tmp));
            }
            return out;
        }

        if (obj instanceof long[]) {
            ArrayValue out = new ArrayValue();
            for (long tmp : (long[]) obj) {
                out.add(new LongValue(tmp));
            }
            return out;
        }

        if (obj instanceof double[]) {
            ArrayValue out = new ArrayValue();
            for (double tmp : (double[]) obj) {
                out.add(new DoubleValue(tmp));
            }
            return out;
        }

        if (obj instanceof Object[]) {
            ArrayValue out = new ArrayValue();
            for (Object tmp : (Object[]) obj) {
                out.add(of(tmp));
            }
            return out;
        }

        throw new IllegalArgumentException("unable to convert " + obj + " to EncodedValue");
    }

    abstract static class SimpleValue implements EncodedValue {

        private final int type;

        public SimpleValue(int type) {
            this.type = type;
        }

        @Override
        public int type() {
            return type;
        }

        @Override
        public boolean isDefault() {
            return false;
        }

        @Override
        public abstract SimpleValue clone();

        @Override
        public int hashCode() {
            return Objects.hashCode(value());
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof SimpleValue) {
                EncodedValue evobj = (EncodedValue) obj;
                return type() == evobj.type()
                        && Objects.equals(value(), evobj.value());
            }
            return false;
        }

        @Override
        public String toString() {
            return value().toString();
        }
    }

    public static class BooleanValue extends SimpleValue {

        public boolean value;

        public BooleanValue() {
            super(VALUE_BOOLEAN);
        }

        public BooleanValue(boolean value) {
            this();
            this.value = value;
        }

        @Override
        public void write(WriteContext context, RandomOutput out) {
            out.writeByte(type() | ((value ? 1 : 0) << 5));
        }

        @Override
        public boolean isDefault() {
            return !value;
        }

        @Override
        public Object value() {
            return value;
        }

        @Override
        public BooleanValue clone() {
            return new BooleanValue(value);
        }
    }

    public static class ByteValue extends SimpleValue {

        public byte value;

        public ByteValue() {
            super(VALUE_BYTE);
        }

        public ByteValue(byte value) {
            this();
            this.value = value;
        }

        @Override
        public void write(WriteContext context, RandomOutput out) {
            ValueCoder.writeSignedIntegralValue(out, type(), value);
        }

        @Override
        public boolean isDefault() {
            return value == 0;
        }

        @Override
        public Object value() {
            return value;
        }

        @Override
        public ByteValue clone() {
            return new ByteValue(value);
        }
    }

    public static class ShortValue extends SimpleValue {

        public short value;

        public ShortValue() {
            super(VALUE_SHORT);
        }

        public ShortValue(short value) {
            this();
            this.value = value;
        }

        @Override
        public void write(WriteContext context, RandomOutput out) {
            ValueCoder.writeSignedIntegralValue(out, type(), value);
        }

        @Override
        public boolean isDefault() {
            return value == 0;
        }

        @Override
        public Object value() {
            return value;
        }

        @Override
        public ShortValue clone() {
            return new ShortValue(value);
        }
    }

    public static class CharValue extends SimpleValue {

        public char value;

        public CharValue() {
            super(VALUE_CHAR);
        }

        public CharValue(char value) {
            this();
            this.value = value;
        }

        @Override
        public void write(WriteContext context, RandomOutput out) {
            ValueCoder.writeUnsignedIntegralValue(out, type(), value);
        }

        @Override
        public boolean isDefault() {
            return value == 0;
        }

        @Override
        public Object value() {
            return value;
        }

        @Override
        public CharValue clone() {
            return new CharValue(value);
        }
    }

    public static class IntValue extends SimpleValue {

        public int value;

        public IntValue() {
            super(VALUE_INT);
        }

        public IntValue(int value) {
            this();
            this.value = value;
        }

        @Override
        public void write(WriteContext context, RandomOutput out) {
            ValueCoder.writeSignedIntegralValue(out, type(), value);
        }

        @Override
        public boolean isDefault() {
            return value == 0;
        }

        @Override
        public Object value() {
            return value;
        }

        @Override
        public IntValue clone() {
            return new IntValue(value);
        }
    }

    public static class LongValue extends SimpleValue {

        public long value;

        public LongValue() {
            super(VALUE_LONG);
        }

        public LongValue(long value) {
            this();
            this.value = value;
        }

        @Override
        public void write(WriteContext context, RandomOutput out) {
            ValueCoder.writeSignedIntegralValue(out, type(), value);
        }

        @Override
        public boolean isDefault() {
            return value == 0;
        }

        @Override
        public Object value() {
            return value;
        }

        @Override
        public LongValue clone() {
            return new LongValue(value);
        }
    }

    public static class FloatValue extends SimpleValue {

        public float value;

        public FloatValue() {
            super(VALUE_FLOAT);
        }

        public FloatValue(float value) {
            this();
            this.value = value;
        }

        @Override
        public void write(WriteContext context, RandomOutput out) {
            ValueCoder.writeRightZeroExtendedValue(out, type(),
                    ((long) Float.floatToRawIntBits(value)) << 32);
        }

        @Override
        public boolean isDefault() {
            return value == 0f;
        }

        @Override
        public Object value() {
            return value;
        }

        @Override
        public FloatValue clone() {
            return new FloatValue(value);
        }
    }

    public static class DoubleValue extends SimpleValue {

        public double value;

        public DoubleValue() {
            super(VALUE_DOUBLE);
        }

        public DoubleValue(double value) {
            this();
            this.value = value;
        }

        @Override
        public void write(WriteContext context, RandomOutput out) {
            ValueCoder.writeRightZeroExtendedValue(out, type(),
                    Double.doubleToRawLongBits(value)
            );
        }

        @Override
        public boolean isDefault() {
            return value == 0d;
        }

        @Override
        public Object value() {
            return value;
        }

        @Override
        public DoubleValue clone() {
            return new DoubleValue(value);
        }
    }

    public static class NullValue extends SimpleValue {

        public NullValue() {
            super(VALUE_NULL);
        }

        @Override
        public void write(WriteContext context, RandomOutput out) {
            out.writeByte(type());
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
        public Object value() {
            return null;
        }

        @Override
        public NullValue clone() {
            return new NullValue();
        }
    }

    public static class MethodTypeValue extends SimpleValue {

        private ProtoId value;

        public MethodTypeValue(ProtoId value) {
            super(VALUE_METHOD_TYPE);
            setValue(value);
        }

        @Override
        public void fillContext(DataSet data) {
            data.addProto(value);
        }

        @Override
        public void write(WriteContext context, RandomOutput out) {
            ValueCoder.writeUnsignedIntegralValue(out, type(),
                    context.getProtoIndex(value)
            );
        }

        public final void setValue(ProtoId value) {
            this.value = Objects.requireNonNull(value,
                    "value can`t be null").clone();
        }

        @Override
        public final ProtoId value() {
            return value;
        }

        @Override
        public MethodTypeValue clone() {
            return new MethodTypeValue(value);
        }
    }

    public static class MethodHandleValue extends SimpleValue {

        private MethodHandleItem value;

        public MethodHandleValue(MethodHandleItem value) {
            super(VALUE_METHOD_HANDLE);
            setValue(value);
        }

        @Override
        public void fillContext(DataSet data) {
            data.addMethodHandle(value);
        }

        @Override
        public void write(WriteContext context, RandomOutput out) {
            ValueCoder.writeUnsignedIntegralValue(out, type(),
                    context.getMethodHandleIndex(value));
        }

        public final void setValue(MethodHandleItem value) {
            this.value = Objects.requireNonNull(value,
                    "value can`t be null").clone();
        }

        @Override
        public final MethodHandleItem value() {
            return value;
        }

        @Override
        public MethodHandleValue clone() {
            return new MethodHandleValue(value);
        }
    }

    public static class StringValue extends SimpleValue {

        private String value;

        public StringValue(String value) {
            super(VALUE_STRING);
            setValue(value);
        }

        @Override
        public void fillContext(DataSet data) {
            data.addString(value);
        }

        @Override
        public void write(WriteContext context, RandomOutput out) {
            ValueCoder.writeUnsignedIntegralValue(out, type(),
                    context.getStringIndex(value));
        }

        public final void setValue(String value) {
            this.value = Objects.requireNonNull(value,
                    "value can`t be null");
        }

        @Override
        public final String value() {
            return value;
        }

        @Override
        public StringValue clone() {
            return new StringValue(value);
        }
    }

    public static class TypeValue extends SimpleValue {

        private TypeId value;

        public TypeValue(TypeId value) {
            super(VALUE_TYPE);
            setValue(value);
        }

        @Override
        public void fillContext(DataSet data) {
            data.addType(value);
        }

        @Override
        public void write(WriteContext context, RandomOutput out) {
            ValueCoder.writeUnsignedIntegralValue(out, type(),
                    context.getTypeIndex(value));
        }

        public final void setValue(TypeId value) {
            this.value = Objects.requireNonNull(value,
                    "value can`t be null").clone();
        }

        @Override
        public final TypeId value() {
            return value;
        }

        @Override
        public TypeValue clone() {
            return new TypeValue(value);
        }
    }

    public static class FieldValue extends SimpleValue {

        private FieldId value;

        public FieldValue(FieldId value) {
            super(VALUE_FIELD);
            setValue(value);
        }

        @Override
        public void fillContext(DataSet data) {
            data.addField(value);
        }

        @Override
        public void write(WriteContext context, RandomOutput out) {
            ValueCoder.writeUnsignedIntegralValue(out, type(),
                    context.getFieldIndex(value));
        }

        public final void setValue(FieldId value) {
            this.value = Objects.requireNonNull(value,
                    "value can`t be null").clone();
        }

        @Override
        public final FieldId value() {
            return value;
        }

        @Override
        public FieldValue clone() {
            return new FieldValue(value);
        }
    }

    public static class MethodValue extends SimpleValue {

        private MethodId value;

        public MethodValue(MethodId value) {
            super(VALUE_METHOD);
            setValue(value);
        }

        @Override
        public void fillContext(DataSet data) {
            data.addMethod(value);
        }

        @Override
        public void write(WriteContext context, RandomOutput out) {
            ValueCoder.writeUnsignedIntegralValue(out, type(),
                    context.getMethodIndex(value));
        }

        public final void setValue(MethodId value) {
            this.value = Objects.requireNonNull(value,
                    "value can`t be null").clone();
        }

        @Override
        public final MethodId value() {
            return value;
        }

        @Override
        public MethodValue clone() {
            return new MethodValue(value);
        }
    }

    public static class EnumValue extends SimpleValue {

        private FieldId value;

        public EnumValue(FieldId value) {
            super(VALUE_ENUM);
            setValue(value);
        }

        @Override
        public void fillContext(DataSet data) {
            data.addField(value);
        }

        @Override
        public void write(WriteContext context, RandomOutput out) {
            ValueCoder.writeUnsignedIntegralValue(out, type(),
                    context.getFieldIndex(value));
        }

        public final void setValue(FieldId value) {
            this.value = Objects.requireNonNull(value,
                    "value can`t be null").clone();
        }

        @Override
        public final FieldId value() {
            return value;
        }

        @Override
        public EnumValue clone() {
            return new EnumValue(value);
        }
    }

    public static class ArrayValue extends PCList<EncodedValue>
            implements EncodedValue {

        public ArrayValue(EncodedValue... value) {
            super(value);
        }

        @Override
        public int type() {
            return VALUE_ARRAY;
        }

        @Override
        public boolean isDefault() {
            return false;
        }

        @Override
        public void fillContext(DataSet data) {
            for (EncodedValue tmp : this) {
                tmp.fillContext(data);
            }
        }

        @Override
        public void write(WriteContext context, RandomOutput out) {
            out.writeByte(type());
            writeData(context, out);
        }

        public void writeData(WriteContext context, RandomOutput out) {
            out.writeULeb128(size());
            for (EncodedValue tmp : this) {
                tmp.write(context, out);
            }
        }

        @Override
        public final ArrayValue value() {
            return this;
        }

        public boolean containsOnlyDefaults() {
            for (EncodedValue tmp : this) {
                if (!tmp.isDefault()) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof ArrayValue) {
                return super.equals(obj);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }

        @Override
        public ArrayValue clone() {
            ArrayValue out = new ArrayValue();
            out.addAll(this);
            return out;
        }
    }

    public static class AnnotationValue extends SimpleValue {

        private EncodedAnnotation value;

        public AnnotationValue(EncodedAnnotation value) {
            super(VALUE_ANNOTATION);
            setValue(value);
        }

        @Override
        public void fillContext(DataSet data) {
            value.fillContext(data);
        }

        @Override
        public void write(WriteContext context, RandomOutput out) {
            out.writeByte(type());
            value.write(context, out);
        }

        public final void setValue(EncodedAnnotation value) {
            this.value = Objects.requireNonNull(value,
                    "value can`t be null").clone();
        }

        @Override
        public final EncodedAnnotation value() {
            return value;
        }

        @Override
        public AnnotationValue clone() {
            return new AnnotationValue(value);
        }
    }
}
