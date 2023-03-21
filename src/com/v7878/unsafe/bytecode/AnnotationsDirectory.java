package com.v7878.unsafe.bytecode;

import com.v7878.unsafe.io.RandomInput;
import java.util.HashMap;
import java.util.Map;

public class AnnotationsDirectory {

    public AnnotationSet class_annotations;
    public Map<FieldId, AnnotationSet> annotated_fields;
    public Map<MethodId, AnnotationSet> annotated_methods;
    public Map<MethodId, AnnotationSetList> annotated_parameters;

    public static AnnotationsDirectory read(RandomInput in, ReadContext context) {
        AnnotationsDirectory out = new AnnotationsDirectory();
        int class_annotations_off = in.readInt();
        if (class_annotations_off != 0) {
            RandomInput in2 = in.duplicate(class_annotations_off);
            out.class_annotations = AnnotationSet.read(in2, context);
        } else {
            out.class_annotations = AnnotationSet.empty();
        }
        int annotated_fields_size = in.readInt();
        int annotated_methods_size = in.readInt();
        int annotated_parameters_size = in.readInt();
        out.annotated_fields = new HashMap<>(annotated_fields_size);
        for (int i = 0; i < annotated_fields_size; i++) {
            FieldId field = context.field(in.readInt());
            int field_annotations_off = in.readInt();
            RandomInput in2 = in.duplicate(field_annotations_off);
            AnnotationSet field_annotations = AnnotationSet.read(in2, context);
            out.annotated_fields.put(field, field_annotations);
        }
        out.annotated_methods = new HashMap<>(annotated_methods_size);
        for (int i = 0; i < annotated_methods_size; i++) {
            MethodId method = context.method(in.readInt());
            int method_annotations_off = in.readInt();
            RandomInput in2 = in.duplicate(method_annotations_off);
            AnnotationSet method_annotations = AnnotationSet.read(in2, context);
            out.annotated_methods.put(method, method_annotations);
        }
        out.annotated_parameters = new HashMap<>(annotated_parameters_size);
        for (int i = 0; i < annotated_parameters_size; i++) {
            MethodId method = context.method(in.readInt());
            int parameters_annotations_off = in.readInt();
            RandomInput in2 = in.duplicate(parameters_annotations_off);
            AnnotationSetList parameters_annotations = AnnotationSetList.read(in2, context);
            out.annotated_parameters.put(method, parameters_annotations);
        }
        return out;
    }

    public static AnnotationsDirectory empty() {
        AnnotationsDirectory out = new AnnotationsDirectory();
        out.class_annotations = AnnotationSet.empty();
        out.annotated_fields = new HashMap<>(0);
        out.annotated_methods = new HashMap<>(0);
        out.annotated_parameters = new HashMap<>(0);
        return out;
    }
}
