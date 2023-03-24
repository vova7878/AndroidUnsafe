package com.v7878.unsafe.dex;

import static com.v7878.unsafe.Utils.*;
import com.v7878.unsafe.dex.EncodedValue.*;
import java.util.*;

// Temporary object. Needed to read or write
public class DataSet {

    private final Set<String> strings;
    private final Set<TypeId> types;
    private final Set<ProtoId> protos;
    private final Set<FieldId> fields;
    private final Set<MethodId> methods;
    private final Set<MethodHandleItem> method_handles;
    private final Set<CallSiteId> call_sites;

    private final List<ClassDef> class_defs;
    private final List<ClassData> class_data_items;

    private final Map<ClassDef, AnnotationsDirectory> annotations_directories;

    private final Set<TypeList> type_lists;
    private final Set<AnnotationItem> annotations;
    private final Set<AnnotationSet> annotation_sets;
    private final Set<AnnotationSetList> annotation_set_lists;
    private final Set<ArrayValue> array_values;

    public DataSet() {
        strings = new HashSet<>();
        types = new HashSet<>();
        protos = new HashSet<>();
        fields = new HashSet<>();
        methods = new HashSet<>();
        method_handles = new HashSet<>();
        call_sites = new HashSet<>();

        class_defs = new ArrayList<>();
        class_data_items = new ArrayList<>();

        annotations_directories = new HashMap<>();

        type_lists = new HashSet<>();
        annotations = new HashSet<>();
        annotation_sets = new HashSet<>();
        annotation_set_lists = new HashSet<>();
        array_values = new HashSet<>();
    }

    public void addString(String value) {
        strings.add(value);
    }

    public void addType(TypeId value) {
        value.fillContext(this);
        types.add(value);
    }

    public void addProto(ProtoId value) {
        value.fillContext(this);
        protos.add(value);
    }

    public void addField(FieldId value) {
        value.fillContext(this);
        fields.add(value);
    }

    public void addMethod(MethodId value) {
        value.fillContext(this);
        methods.add(value);
    }

    public void addMethodHandle(MethodHandleItem value) {
        value.fillContext(this);
        method_handles.add(value);
    }

    public void addCallSite(CallSiteId value) {
        value.fillContext(this);
        call_sites.add(value);
    }

    public void addClassDef(ClassDef value) {
        value.fillContext(this);
        class_defs.add(value);
    }

    public void addClassData(ClassData value) {
        assert_(!value.isEmpty(), IllegalStateException::new,
                "class_data is empty");
        value.fillContext(this);
        class_data_items.add(value);
    }

    public void addTypeList(TypeList value) {
        value.fillContext(this);
        type_lists.add(value);
    }

    public void addAnnotation(AnnotationItem value) {
        value.fillContext(this);
        annotations.add(value);
    }

    public void addAnnotationSet(AnnotationSet value) {
        assert_(!value.isEmpty(), IllegalStateException::new,
                "annotation_set is empty");
        value.fillContext(this);
        annotation_sets.add(value);
    }

    public void addAnnotationSetList(AnnotationSetList value) {
        assert_(!value.isEmpty(), IllegalStateException::new,
                "annotation_set_list is empty");
        value.fillContext(this);
        annotation_set_lists.add(value);
    }

    public void addAnnotationsDirectory(ClassDef clazz,
            AnnotationsDirectory value) {
        if (annotations_directories.putIfAbsent(clazz, value) != null) {
            throw new IllegalStateException(
                    "annotations_directories contain duplicates");
        }
    }

    public void addArrayValue(ArrayValue value) {
        array_values.add(value);
    }

    public String[] getStrings() {
        return strings.stream().toArray(String[]::new);
    }

    public TypeId[] getTypes() {
        return types.stream().toArray(TypeId[]::new);
    }

    public ProtoId[] getProtos() {
        return protos.stream().toArray(ProtoId[]::new);
    }

    public FieldId[] getFields() {
        return fields.stream().toArray(FieldId[]::new);
    }

    public MethodId[] getMethods() {
        return methods.stream().toArray(MethodId[]::new);
    }

    public MethodHandleItem[] getMethodHandles() {
        return method_handles.stream().toArray(MethodHandleItem[]::new);
    }

    public CallSiteId[] getCallSites() {
        return call_sites.stream().toArray(CallSiteId[]::new);
    }

    public ClassDef[] getClassDefs() {
        return ClassDef.sort(class_defs);
    }

    public ClassData[] getClassDataItems() {
        return class_data_items.stream().toArray(ClassData[]::new);
    }

    public TypeList[] getTypeLists() {
        return type_lists.stream().toArray(TypeList[]::new);
    }

    public AnnotationItem[] getAnnotations() {
        return annotations.stream().toArray(AnnotationItem[]::new);
    }

    public AnnotationSet[] getAnnotationSets() {
        return annotation_sets.stream().toArray(AnnotationSet[]::new);
    }

    public AnnotationSetList[] getAnnotationSetLists() {
        return annotation_set_lists.stream().toArray(AnnotationSetList[]::new);
    }

    public Map<ClassDef, AnnotationsDirectory> getAnnotationsDirectories() {
        return annotations_directories;
    }

    public ArrayValue[] getArrayValues() {
        return array_values.stream().toArray(ArrayValue[]::new);
    }
}
