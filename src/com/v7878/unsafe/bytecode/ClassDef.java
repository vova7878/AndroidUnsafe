package com.v7878.unsafe.bytecode;

import static com.v7878.unsafe.Utils.*;
import static com.v7878.unsafe.bytecode.DexConstants.*;
import com.v7878.unsafe.io.*;
import java.util.*;

public class ClassDef implements Cloneable {

    public static final int SIZE = 0x20;

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
        for (TypeId tmp : value.interfaces) {
            add(map, added, out, tmp);
        }
        out.add(value);
        added.add(value);
    }

    // TODO: cycle test
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

    private TypeId clazz;
    private int access_flags;
    private TypeId superclass;
    private TypeList interfaces;
    private String source_file;
    private AnnotationItem[] class_annotations;
    private ClassData class_data;

    public ClassDef(TypeId clazz) {
        setType(clazz);
        setAccessFlags(0);
        setSuperClass(null);
        setInterfaces(null);
        setSourceFile(null);
        setClassAnnotations(null);
        setClassData(null);
    }

    public ClassDef(TypeId clazz, int access_flags, TypeId superclass,
            TypeList interfaces, String source_file,
            AnnotationItem[] class_annotations, ClassData class_data) {
        setType(clazz);
        setAccessFlags(access_flags);
        setSuperClass(superclass);
        setInterfaces(interfaces);
        setSourceFile(source_file);
        setClassAnnotations(class_annotations);
        setClassData(class_data);
    }

    public final void setType(TypeId clazz) {
        this.clazz = Objects.requireNonNull(clazz,
                "type can`n be null").clone();
    }

    public final TypeId getType() {
        return clazz;
    }

    public final void setAccessFlags(int access_flags) {
        this.access_flags = access_flags;
    }

    public final int getAccessFlags() {
        return access_flags;
    }

    public final void setSuperClass(TypeId superclass) {
        this.superclass = superclass == null ? null : superclass.clone();
    }

    public final TypeId getSuperClass() {
        return superclass;
    }

    public final void setInterfaces(TypeList interfaces) {
        this.interfaces = interfaces == null
                ? TypeList.empty() : interfaces.clone();
    }

    public final TypeList getInterfaces() {
        return interfaces;
    }

    public final void setSourceFile(String source_file) {
        this.source_file = source_file;
    }

    public final String getSourceFile() {
        return source_file;
    }

    public final void setClassAnnotations(AnnotationItem[] class_annotations) {
        if (class_annotations == null) {
            class_annotations = new AnnotationItem[0];
        }
        this.class_annotations = Arrays.stream(class_annotations)
                .map(tmp -> Objects.requireNonNull(tmp,
                "class annotations can`t contain null annotation"))
                .map(AnnotationItem::clone)
                .toArray(AnnotationItem[]::new);
    }

    public final AnnotationItem[] getClassAnnotations() {
        return Arrays.copyOf(class_annotations, class_annotations.length);
    }

    //TODO: clone
    public final void setClassData(ClassData class_data) {
        this.class_data = class_data;
    }

    public final ClassData getClassData() {
        return class_data;
    }

    public static ClassDef read(RandomInput in, ReadContext context) {
        TypeId clazz = context.type(in.readInt());
        int access_flags = in.readInt();
        int superclass_idx = in.readInt();
        TypeId superclass = null;
        if (superclass_idx != NO_INDEX) {
            superclass = context.type(superclass_idx);
        }
        int interfaces_off = in.readInt();
        TypeList interfaces = TypeList.empty();
        if (interfaces_off != 0) {
            interfaces = TypeList.read(in.duplicate(interfaces_off), context);
        }
        int source_file_idx = in.readInt();
        String source_file = null;
        if (source_file_idx != NO_INDEX) {
            source_file = context.string(source_file_idx);
        }
        int annotations_off = in.readInt();
        AnnotationsDirectory annotations = AnnotationsDirectory.empty();
        if (annotations_off != 0) {
            RandomInput in2 = in.duplicate(annotations_off);
            annotations = AnnotationsDirectory.read(in2, context);
        }
        AnnotationItem[] class_annotations = annotations.class_annotations;
        int class_data_off = in.readInt();
        EncodedValue[] static_values = new EncodedValue[0];
        int static_values_off = in.readInt();
        if (static_values_off != 0) {
            RandomInput in2 = in.duplicate(static_values_off);
            static_values = (EncodedValue[]) EncodedValueReader
                    .readValue(in2, context, VALUE_ARRAY).getValue();
        }
        ClassData class_data = null;
        if (class_data_off != 0) {
            class_data = ClassData.read(in.duplicate(class_data_off),
                    context, static_values, annotations);
        }
        return new ClassDef(clazz, access_flags, superclass, interfaces,
                source_file, class_annotations, class_data);
    }

    public void fillContext(DataSet data) {
        data.addType(clazz);
        if (superclass != null) {
            data.addType(superclass);
        }
        if (!interfaces.isEmpty()) {
            data.addTypeList(interfaces);
        }
        if (source_file != null) {
            data.addString(source_file);
        }
        for (AnnotationItem tmp : class_annotations) {
            tmp.fillContext(data);
        }
        // TODO: add class_data
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

    @Override
    public ClassDef clone() {
        return new ClassDef(clazz, access_flags, superclass, interfaces,
                source_file, class_annotations, class_data);
    }
}
