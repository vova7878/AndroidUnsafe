package com.v7878.unsafe.bytecode;

import com.v7878.unsafe.io.RandomInput;
import java.lang.reflect.Modifier;
import java.util.Map;

public class EncodedField {

    public FieldId field;
    public int access_flags;
    public AnnotationItem[] annotations;

    public static EncodedField read(RandomInput in, FieldId field,
            Map<FieldId, AnnotationItem[]> annotated_fields) {
        EncodedField out = new EncodedField();
        out.field = field;
        out.access_flags = in.readULeb128();
        out.annotations = annotated_fields.getOrDefault(field, new AnnotationItem[0]);
        return out;
    }

    public static EncodedField[] readArray(RandomInput in, ReadContext rc,
            int size, Map<FieldId, AnnotationItem[]> annotated_fields) {
        EncodedField[] out = new EncodedField[size];
        int fieldIndex = 0;
        for (int i = 0; i < size; i++) {
            fieldIndex += in.readULeb128();
            out[i] = read(in, rc.fields[fieldIndex], annotated_fields);
        }
        return out;
    }

    @Override
    public String toString() {
        String flags = Modifier.toString(access_flags);
        if (flags.length() != 0) {
            flags += " ";
        }
        return flags + field;
    }
}
