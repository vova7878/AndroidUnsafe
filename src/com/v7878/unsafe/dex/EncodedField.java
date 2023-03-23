package com.v7878.unsafe.dex;

import com.v7878.unsafe.io.*;
import java.lang.reflect.Modifier;
import java.util.*;

public class EncodedField implements PublicCloneable {

    public static final Comparator<EncodedField> getComparator(WriteContext context) {
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

    public EncodedField(FieldId field, int access_flags,
            AnnotationSet annotations) {
        setField(field);
        setAccessFlags(access_flags);
        setAnnotations(annotations);
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

    public void fillContext(DataSet data) {
        data.addField(field);
        if (!annotations.isEmpty()) {
            data.addAnnotationSet(annotations);
        }
    }

    public void write(WriteContext context, RandomOutput out) {
        out.writeULeb128(access_flags);
    }

    public static void writeArray(WriteContext context, RandomOutput out,
            EncodedField[] encoded_fields) {
        Arrays.sort(encoded_fields, context.encoded_field_comparator());
        int fieldIndex = 0;
        for (EncodedField tmp : encoded_fields) {
            int diff = context.getFieldIndex(tmp.field) - fieldIndex;
            fieldIndex += diff;
            out.writeULeb128(diff);
            tmp.write(context, out);
        }
    }

    public static void writeArray(WriteContext context, RandomOutput out,
            PCList<EncodedField> encoded_fields) {
        writeArray(context, out, encoded_fields
                .stream().toArray(EncodedField[]::new));
    }

    @Override
    public String toString() {
        String flags = Modifier.toString(access_flags);
        if (flags.length() != 0) {
            flags += " ";
        }
        return flags + field;
    }

    @Override
    public PublicCloneable clone() {
        return new EncodedField(field, access_flags, annotations);
    }
}
