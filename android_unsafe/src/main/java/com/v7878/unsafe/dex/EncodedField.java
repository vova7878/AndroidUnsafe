package com.v7878.unsafe.dex;

import static com.v7878.unsafe.Utils.assert_;

import com.v7878.unsafe.io.RandomInput;
import com.v7878.unsafe.io.RandomOutput;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;

public class EncodedField implements PublicCloneable {

    public static Comparator<EncodedField> getComparator(WriteContext context) {
        return (a, b) -> {
            if (a.equals(b)) {
                return 0;
            }

            int out = context.field_comparator()
                    .compare(a.field, b.field);
            if (out != 0) {
                return out;
            }

            // a != b, but a.field == b.field
            throw new IllegalStateException("can`t compare encoded fields " + a + " " + b);
        };
    }

    private FieldId field;
    private int access_flags;
    private AnnotationSet annotations;
    private EncodedValue value;

    public EncodedField(FieldId field, int access_flags,
                        AnnotationSet annotations, EncodedValue value) {
        setField(field);
        setAccessFlags(access_flags);
        setAnnotations(annotations);
        setValue(value);
    }

    public EncodedField(FieldId field, int access_flags,
                        AnnotationSet annotations) {
        this(field, access_flags, annotations, null);
    }

    public final void setField(FieldId field) {
        this.field = Objects.requireNonNull(field,
                "field can`t be null").clone();
    }

    public final FieldId getField() {
        return field;
    }

    public final void setAccessFlags(int access_flags) {
        this.access_flags = access_flags;
    }

    public final int getAccessFlags() {
        return access_flags;
    }

    public final void setAnnotations(AnnotationSet annotations) {
        this.annotations = annotations == null
                ? AnnotationSet.empty() : annotations.clone();
    }

    public final AnnotationSet getAnnotations() {
        return annotations;
    }

    public final void setValue(EncodedValue value) {
        this.value = value == null ? null : value.clone();
    }

    public final EncodedValue getValue() {
        return value == null ? EncodedValue.defaultValue(field.getType()) : value;
    }

    public final boolean hasValue() {
        return value != null;
    }

    public static EncodedField read(RandomInput in, FieldId field,
                                    Map<FieldId, AnnotationSet> annotated_fields) {
        return new EncodedField(field, in.readULeb128(),
                annotated_fields.get(field));
    }

    public static PCList<EncodedField> readArray(RandomInput in,
                                                 ReadContext context, int size,
                                                 Map<FieldId, AnnotationSet> annotated_fields) {
        PCList<EncodedField> out = PCList.empty();
        int fieldIndex = 0;
        for (int i = 0; i < size; i++) {
            fieldIndex += in.readULeb128();
            out.add(read(in, context.field(fieldIndex), annotated_fields));
        }
        return out;
    }

    public void collectData(DataCollector data) {
        data.add(field);
        if (!annotations.isEmpty()) {
            data.add(annotations);
        }
        if (value != null && !value.isDefault()) {
            data.fill(value);
        }
    }

    public void write(WriteContext context, RandomOutput out) {
        out.writeULeb128(access_flags);
    }

    private static void check(boolean static_fields, EncodedField encoded_field) {
        if (static_fields) {
            assert_((encoded_field.access_flags & Modifier.STATIC) != 0,
                    IllegalStateException::new, "field must be static");
        } else {
            assert_((encoded_field.access_flags & Modifier.STATIC) == 0,
                    IllegalStateException::new, "field must not be static");
            EncodedValue tmp = encoded_field.getValue();
            assert_(tmp.isDefault(), IllegalStateException::new,
                    "instance field can`t have value: " + tmp);
        }
    }

    public static void writeArray(boolean static_fields,
                                  WriteContext context, RandomOutput out,
                                  EncodedField[] encoded_fields) {
        Arrays.sort(encoded_fields, context.encoded_field_comparator());
        int fieldIndex = 0;
        for (EncodedField tmp : encoded_fields) {
            check(static_fields, tmp);
            int diff = context.getFieldIndex(tmp.field) - fieldIndex;
            fieldIndex += diff;
            out.writeULeb128(diff);
            tmp.write(context, out);
        }
    }

    public static void writeArray(boolean static_fields,
                                  WriteContext context, RandomOutput out,
                                  PCList<EncodedField> encoded_fields) {
        writeArray(static_fields, context, out, encoded_fields.toArray(new EncodedField[0]));
    }

    @Override
    public String toString() {
        String flags = Modifier.toString(access_flags);
        if (flags.length() != 0) {
            flags += " ";
        }
        String v = value == null ? "" : " = " + value;
        return flags + field + v;
    }

    @Override
    public PublicCloneable clone() {
        return new EncodedField(field, access_flags, annotations, value);
    }
}