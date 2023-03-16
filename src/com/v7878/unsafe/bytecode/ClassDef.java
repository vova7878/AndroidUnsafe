package com.v7878.unsafe.bytecode;

import static com.v7878.unsafe.bytecode.DexConstants.*;
import com.v7878.unsafe.io.RandomInput;

public class ClassDef {

    public TypeId clazz;
    public int access_flags;
    public TypeId superclass;
    public TypeId[] interfaces;
    public StringId source_file;
    public AnnotationItem[] class_annotations;
    public ClassData class_data;

    public static ClassDef read(RandomInput in, Context context) {
        ClassDef out = new ClassDef();
        out.clazz = context.type(in.readInt());
        out.access_flags = in.readInt();
        int superclass_idx = in.readInt();
        out.superclass = superclass_idx == NO_INDEX ? null : context.type(superclass_idx);
        int interfaces_off = in.readInt();
        out.interfaces = new TypeId[0];
        if (interfaces_off != 0) {
            out.interfaces = TypeId.readTypeList(in.duplicate(interfaces_off), context);
        }
        int source_file_idx = in.readInt();
        out.source_file = source_file_idx == NO_INDEX ? null : context.string(source_file_idx);
        int annotations_off = in.readInt();
        AnnotationsDirectory annotations;
        if (annotations_off != 0) {
            RandomInput in2 = in.duplicate(annotations_off);
            annotations = AnnotationsDirectory.read(in2, context);
        } else {
            annotations = AnnotationsDirectory.empty();
        }
        out.class_annotations = annotations.class_annotations;
        int class_data_off = in.readInt();
        EncodedValue[] static_values = new EncodedValue[0];
        int static_values_off = in.readInt();
        if (static_values_off != 0) {
            RandomInput in2 = in.duplicate(static_values_off);
            EncodedValueReader reader = new EncodedValueReader(in2, VALUE_ARRAY);
            static_values = reader.readArray(context).value;
        }
        if (class_data_off != 0) {
            out.class_data = ClassData.read(in.duplicate(class_data_off),
                    context, static_values, annotations);
        }
        return out;
    }
}
