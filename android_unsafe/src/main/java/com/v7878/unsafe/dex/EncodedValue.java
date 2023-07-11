package com.v7878.unsafe.dex;

import static com.v7878.unsafe.dex.DexConstants.VALUE_ANNOTATION;
import static com.v7878.unsafe.dex.DexConstants.VALUE_ARRAY;
import static com.v7878.unsafe.dex.DexConstants.VALUE_BOOLEAN;
import static com.v7878.unsafe.dex.DexConstants.VALUE_BYTE;
import static com.v7878.unsafe.dex.DexConstants.VALUE_CHAR;
import static com.v7878.unsafe.dex.DexConstants.VALUE_DOUBLE;
import static com.v7878.unsafe.dex.DexConstants.VALUE_ENUM;
import static com.v7878.unsafe.dex.DexConstants.VALUE_FIELD;
import static com.v7878.unsafe.dex.DexConstants.VALUE_FLOAT;
import static com.v7878.unsafe.dex.DexConstants.VALUE_INT;
import static com.v7878.unsafe.dex.DexConstants.VALUE_LONG;
import static com.v7878.unsafe.dex.DexConstants.VALUE_METHOD;
import static com.v7878.unsafe.dex.DexConstants.VALUE_METHOD_HANDLE;
import static com.v7878.unsafe.dex.DexConstants.VALUE_METHOD_TYPE;
import static com.v7878.unsafe.dex.DexConstants.VALUE_NULL;
import static com.v7878.unsafe.dex.DexConstants.VALUE_SHORT;
import static com.v7878.unsafe.dex.DexConstants.VALUE_STRING;
import static com.v7878.unsafe.dex.DexConstants.VALUE_TYPE;

import com.v7878.unsafe.io.RandomOutput;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.util.Objects;

public interface EncodedValue extends PublicCloneable {

    boolean isDefault();

    int type();

    Object value();

    default void collectData(DataCollector data) {
    }

    void write(WriteContext context, RandomOutput out);

    @Override
    EncodedValue clone();

    static EncodedValue defaultValue(TypeId type) {
        switch (type.getShorty()) {
            case 'Z':
                return new BooleanValue();
            case 'B':
                return new ByteValue();
            case 'S':
                return new ShortValue();
            case 'C':
                return new CharValue();
            case 'I':
                return new IntValue();
            case 'J':
                return new LongValue();
            case 'F':
                return new FloatValue();
            case 'D':
                return new DoubleValue();
            case 'L':
                return new NullValue();
            default:
                throw new IllegalArgumentException();
        }
    }

    static EncodedValue of(Object obj) {
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
        if (obj instanceof MethodType) {
            return new MethodTypeValue(ProtoId.of((MethodType) obj));
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
            return new EnumValue(FieldId.of((Enum<?>) obj));
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

    abstract class SimpleValue implements EncodedValue {

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

    class BooleanValue extends SimpleValue {

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
        public Boolean value() {
            return value;
        }

        @Override
        public BooleanValue clone() {
            return new BooleanValue(value);
        }
    }

    class ByteValue extends SimpleValue {

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
        public Byte value() {
            return value;
        }

        @Override
        public ByteValue clone() {
            return new ByteValue(value);
        }
    }

    class ShortValue extends SimpleValue {

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
        public Short value() {
            return value;
        }

        @Override
        public ShortValue clone() {
            return new ShortValue(value);
        }
    }

    class CharValue extends SimpleValue {

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
        public Character value() {
            return value;
        }

        @Override
        public CharValue clone() {
            return new CharValue(value);
        }
    }

    class IntValue extends SimpleValue {

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
        public Integer value() {
            return value;
        }

        @Override
        public IntValue clone() {
            return new IntValue(value);
        }
    }

    class LongValue extends SimpleValue {

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
        public Long value() {
            return value;
        }

        @Override
        public LongValue clone() {
            return new LongValue(value);
        }
    }

    class FloatValue extends SimpleValue {

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
        public Float value() {
            return value;
        }

        @Override
        public FloatValue clone() {
            return new FloatValue(value);
        }
    }

    class DoubleValue extends SimpleValue {

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
        public Double value() {
            return value;
        }

        @Override
        public DoubleValue clone() {
            return new DoubleValue(value);
        }
    }

    class NullValue extends SimpleValue {

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

    class MethodTypeValue extends SimpleValue {

        private ProtoId value;

        public MethodTypeValue(ProtoId value) {
            super(VALUE_METHOD_TYPE);
            setValue(value);
        }

        @Override
        public void collectData(DataCollector data) {
            data.add(value);
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

    class MethodHandleValue extends SimpleValue {

        private MethodHandleItem value;

        public MethodHandleValue(MethodHandleItem value) {
            super(VALUE_METHOD_HANDLE);
            setValue(value);
        }

        @Override
        public void collectData(DataCollector data) {
            data.add(value);
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

    class StringValue extends SimpleValue {

        private String value;

        public StringValue(String value) {
            super(VALUE_STRING);
            setValue(value);
        }

        @Override
        public void collectData(DataCollector data) {
            data.add(value);
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

    class TypeValue extends SimpleValue {

        private TypeId value;

        public TypeValue(TypeId value) {
            super(VALUE_TYPE);
            setValue(value);
        }

        @Override
        public void collectData(DataCollector data) {
            data.add(value);
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

    class FieldValue extends SimpleValue {

        private FieldId value;

        public FieldValue(FieldId value) {
            super(VALUE_FIELD);
            setValue(value);
        }

        @Override
        public void collectData(DataCollector data) {
            data.add(value);
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

    class MethodValue extends SimpleValue {

        private MethodId value;

        public MethodValue(MethodId value) {
            super(VALUE_METHOD);
            setValue(value);
        }

        @Override
        public void collectData(DataCollector data) {
            data.add(value);
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

    class EnumValue extends SimpleValue {

        private FieldId value;

        public EnumValue(FieldId value) {
            super(VALUE_ENUM);
            setValue(value);
        }

        @Override
        public void collectData(DataCollector data) {
            data.add(value);
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

    class ArrayValue extends PCList<EncodedValue> implements EncodedValue {

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
        public void collectData(DataCollector data) {
            for (EncodedValue tmp : this) {
                data.fill(tmp);
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
        public ArrayValue clone() {
            ArrayValue out = new ArrayValue();
            out.addAll(this);
            return out;
        }
    }

    class AnnotationValue extends SimpleValue {

        private EncodedAnnotation value;

        public AnnotationValue(EncodedAnnotation value) {
            super(VALUE_ANNOTATION);
            setValue(value);
        }

        @Override
        public void collectData(DataCollector data) {
            data.fill(value);
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
