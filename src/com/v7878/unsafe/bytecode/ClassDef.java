package com.v7878.unsafe.bytecode;

import static com.v7878.unsafe.Utils.*;
import static com.v7878.unsafe.bytecode.DexConstants.*;
import com.v7878.unsafe.bytecode.TypeId.*;
import com.v7878.unsafe.io.*;
import java.util.*;

public class ClassDef {

    public static final int SIZE = 0x20;

    public TypeId clazz;
    public int access_flags;
    public TypeId superclass;
    public TypeList interfaces;
    public String source_file;
    public AnnotationItem[] class_annotations;
    public ClassData class_data;

    private static void add(Map<TypeId, ClassDef> map,
            Set<ClassDef> added, ArrayList<ClassDef> out, TypeId type) {
        ClassDef value = map.get(type);
        if (value == null) {
            return;
        }
        if (added.contains(value)) {
            return;
        }
        if (value.superclass != null) {
            add(map, added, out, value.superclass);
        }
        for (TypeId tmp : value.interfaces.list) {
            add(map, added, out, tmp);
        }
        out.add(value);
        added.add(value);
    }

    public static ClassDef[] sort(Set<ClassDef> class_defs) {
        Map<TypeId, ClassDef> map = new HashMap<>();
        class_defs.stream().forEach((value) -> {
            if (map.putIfAbsent(value.clazz, value) != null) {
                throw new IllegalStateException(
                        "class defs contain duplicates: " + value.clazz);
            }
        });

        Set<ClassDef> added = new HashSet<>();
        ArrayList<ClassDef> out = new ArrayList<>(class_defs.size());

        class_defs.stream().forEach((value) -> {
            add(map, added, out, value.clazz);
        });

        assert_(out.size() == class_defs.size(), IllegalStateException::new,
                "sorted.length(" + out.size() + ") != input.length(" + class_defs.size() + ")");
        return out.stream().toArray(ClassDef[]::new);
    }

    public static ClassDef read(RandomInput in, Context context) {
        ClassDef out = new ClassDef();
        out.clazz = context.type(in.readInt());
        out.access_flags = in.readInt();
        int superclass_idx = in.readInt();
        out.superclass = superclass_idx == NO_INDEX ? null : context.type(superclass_idx);
        int interfaces_off = in.readInt();
        if (interfaces_off != 0) {
            out.interfaces = TypeList.read(in.duplicate(interfaces_off), context);
        } else {
            out.interfaces = TypeList.empty();
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

    public void fillContext(DataSet data) {
        data.addString(source_file);
        data.addType(clazz);
        data.addType(superclass);
        if (!interfaces.isEmpty()) {
            data.addTypeList(interfaces);
        }
        for (AnnotationItem tmp : class_annotations) {
            tmp.fillContext(data);
        }
        if (class_data != null) {
            class_data.fillContext(data);
        }
    }

    public void write(WriteContext context, RandomOutput out) {
        out.writeInt(context.getTypeIndex(clazz));
        out.writeInt(access_flags);
        out.writeInt(superclass == null ? NO_INDEX : context.getTypeIndex(superclass));
        out.writeInt(interfaces.isEmpty() ? 0 : context.getTypeListOffset(interfaces));
        out.writeInt(source_file == null ? NO_INDEX : context.getStringIndex(source_file));
        out.writeInt(0);  // TODO: annotations
        out.writeInt(0);  // TODO: class_data
        out.writeInt(0);  // TODO: static_values
    }
}
