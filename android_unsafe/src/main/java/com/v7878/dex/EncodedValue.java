package com.v7878.dex;

import com.v7878.dex.io.RandomOutput;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.Objects;

public interface EncodedValue extends PublicCloneable {

    Comparator<EncodedValue> COMPARATOR = (a, b) -> {
        EncodedValueType type;
        if ((type = a.type()) != b.type()) {
            return a.type().value - b.type().value;
        }

        //noinspection unchecked
        return ((Comparator<EncodedValue>) type.comparator).compare(a, b);
    };

    enum EncodedValueType {
        BYTE(0x00, ByteValue.COMPARATOR),
        SHORT(0x02, ShortValue.COMPARATOR),
        CHAR(0x03, CharValue.COMPARATOR),
        INT(0x04, IntValue.COMPARATOR),
        LONG(0x06, LongValue.COMPARATOR),
        FLOAT(0x10, FloatValue.COMPARATOR),
        DOUBLE(0x11, DoubleValue.COMPARATOR),
        METHOD_TYPE(0x15, MethodTypeValue.COMPARATOR),
        METHOD_HANDLE(0x16, MethodHandleValue.COMPARATOR),
        STRING(0x17, StringValue.COMPARATOR),
        TYPE(0x18, TypeValue.COMPARATOR),
        FIELD(0x19, FieldValue.COMPARATOR),
        METHOD(0x1a, MethodValue.COMPARATOR),
        ENUM(0x1b, EnumValue.COMPARATOR),
        ARRAY(0x1c, ArrayValue.COMPARATOR),
        ANNOTATION(0x1d, AnnotationValue.COMPARATOR),
        NULL(0x1e, NullValue.COMPARATOR),
        BOOLEAN(0x1f, BooleanValue.COMPARATOR);

        public final int value;
        public final Comparator<?> comparator;

        EncodedValueType(int value, Comparator<?> comparator) {
            this.value = value;
            this.comparator = comparator;
        }

        public static EncodedValueType of(int int_type) {
            for (EncodedValueType type : values()) {
                if (int_type == type.value) {
                    return type;
                }
            }
            throw new IllegalStateException("Unexpected type: " + Integer.toHexString(int_type));
        }
    }

    default void collectData(DataCollector data) {
    }

    void write(WriteContext context, RandomOutput out);

    boolean isDefault();

    EncodedValueType type();

    Object value();

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

        private final EncodedValueType type;

        public SimpleValue(EncodedValueType type) {
            if (type == null) {
                throw new AssertionError();
            }
            this.type = type;
        }

        @Override
        public EncodedValueType type() {
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

        public static final Comparator<BooleanValue> COMPARATOR =
                (a, b) -> Boolean.compare(a.value, b.value);

        public boolean value;

        public BooleanValue() {
            super(EncodedValueType.BOOLEAN);
        }

        public BooleanValue(boolean value) {
            this();
            this.value = value;
        }

        @Override
        public void write(WriteContext context, RandomOutput out) {
            out.writeByte(type().value | ((value ? 1 : 0) << 5));
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

        public static final Comparator<ByteValue> COMPARATOR =
                (a, b) -> Byte.compare(a.value, b.value);

        public byte value;

        public ByteValue() {
            super(EncodedValueType.BYTE);
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

        public static final Comparator<ShortValue> COMPARATOR =
                (a, b) -> Short.compare(a.value, b.value);

        public short value;

        public ShortValue() {
            super(EncodedValueType.SHORT);
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

        public static final Comparator<CharValue> COMPARATOR =
                (a, b) -> Character.compare(a.value, b.value);

        public char value;

        public CharValue() {
            super(EncodedValueType.CHAR);
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

        public static final Comparator<IntValue> COMPARATOR =
                (a, b) -> Integer.compare(a.value, b.value);

        public int value;

        public IntValue() {
            super(EncodedValueType.INT);
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

        public static final Comparator<LongValue> COMPARATOR =
                (a, b) -> Long.compare(a.value, b.value);

        public long value;

        public LongValue() {
            super(EncodedValueType.LONG);
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

        public static final Comparator<FloatValue> COMPARATOR =
                (a, b) -> Float.compare(a.value, b.value);

        public float value;

        public FloatValue() {
            super(EncodedValueType.FLOAT);
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

        public static final Comparator<DoubleValue> COMPARATOR =
                (a, b) -> Double.compare(a.value, b.value);

        public double value;

        public DoubleValue() {
            super(EncodedValueType.DOUBLE);
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

        public static final Comparator<NullValue> COMPARATOR = (a, b) -> 0;

        public NullValue() {
            super(EncodedValueType.NULL);
        }

        @Override
        public void write(WriteContext context, RandomOutput out) {
            out.writeByte(type().value);
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

        public static final Comparator<MethodTypeValue> COMPARATOR =
                (a, b) -> ProtoId.COMPARATOR.compare(a.value, b.value);

        private ProtoId value;

        public MethodTypeValue(ProtoId value) {
            super(EncodedValueType.METHOD_TYPE);
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

        public static final Comparator<MethodHandleValue> COMPARATOR =
                (a, b) -> MethodHandleItem.COMPARATOR.compare(a.value, b.value);

        private MethodHandleItem value;

        public MethodHandleValue(MethodHandleItem value) {
            super(EncodedValueType.METHOD_HANDLE);
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

        public static final Comparator<StringValue> COMPARATOR =
                (a, b) -> StringId.COMPARATOR.compare(a.value, b.value);

        private String value;

        public StringValue(String value) {
            super(EncodedValueType.STRING);
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

        public static final Comparator<TypeValue> COMPARATOR =
                (a, b) -> TypeId.COMPARATOR.compare(a.value, b.value);

        private TypeId value;

        public TypeValue(TypeId value) {
            super(EncodedValueType.TYPE);
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

        public static final Comparator<FieldValue> COMPARATOR =
                (a, b) -> FieldId.COMPARATOR.compare(a.value, b.value);

        private FieldId value;

        public FieldValue(FieldId value) {
            super(EncodedValueType.FIELD);
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

        public static final Comparator<MethodValue> COMPARATOR =
                (a, b) -> MethodId.COMPARATOR.compare(a.value, b.value);

        private MethodId value;

        public MethodValue(MethodId value) {
            super(EncodedValueType.METHOD);
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

        public static final Comparator<EnumValue> COMPARATOR =
                (a, b) -> FieldId.COMPARATOR.compare(a.value, b.value);

        private FieldId value;

        public EnumValue(FieldId value) {
            super(EncodedValueType.ENUM);
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

        public static final Comparator<ArrayValue> COMPARATOR
                = getComparator(EncodedValue.COMPARATOR);

        public ArrayValue(EncodedValue... value) {
            super(value);
        }

        @Override
        public EncodedValueType type() {
            return EncodedValueType.ARRAY;
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
            out.writeByte(type().value);
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

        public static final Comparator<AnnotationValue> COMPARATOR =
                (a, b) -> EncodedAnnotation.COMPARATOR.compare(a.value, b.value);

        private EncodedAnnotation value;

        public AnnotationValue(EncodedAnnotation value) {
            super(EncodedValueType.ANNOTATION);
            setValue(value);
        }

        @Override
        public void collectData(DataCollector data) {
            data.fill(value);
        }

        @Override
        public void write(WriteContext context, RandomOutput out) {
            out.writeByte(type().value);
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
