package com.v7878.unsafe.bytecode;

import android.util.Pair;
import com.v7878.unsafe.io.RandomInput;

public class ClassData {

    public Pair<EncodedField, EncodedValue>[] static_fields;
    public EncodedField[] instance_fields;
    public EncodedMethod[] direct_methods;
    public EncodedMethod[] virtual_methods;

    public static ClassData read(RandomInput in, Context context,
            EncodedValue[] static_values,
            AnnotationsDirectory annotations) {
        ClassData out = new ClassData();
        int static_fields_size = in.readULeb128();
        int instance_fields_size = in.readULeb128();
        int direct_methods_size = in.readULeb128();
        int virtual_methods_size = in.readULeb128();
        EncodedField[] static_fields = EncodedField.readArray(in, context,
                static_fields_size, annotations.annotated_fields);
        out.static_fields = new Pair[static_fields_size];
        for (int i = 0; i < static_fields_size; i++) {
            if (i < static_values.length) {
                out.static_fields[i]
                        = new Pair<>(static_fields[i], static_values[i]);
            } else {
                out.static_fields[i] = new Pair<>(static_fields[i],
                        EncodedValue.getDefaultValue(static_fields[i].field.type));
            }
        }
        out.instance_fields = EncodedField.readArray(in, context,
                instance_fields_size, annotations.annotated_fields);
        out.direct_methods = EncodedMethod.readArray(in, context,
                direct_methods_size, annotations.annotated_methods,
                annotations.annotated_parameters);
        out.virtual_methods = EncodedMethod.readArray(in, context,
                virtual_methods_size, annotations.annotated_methods,
                annotations.annotated_parameters);
        return out;
    }
}
