package com.v7878.unsafe.dex;

import java.util.Comparator;

public interface WriteContext {

    public Comparator<TypeId> type_comparator();

    public Comparator<TypeList> type_list_comparator();

    public Comparator<ProtoId> proto_comparator();

    public Comparator<FieldId> field_comparator();

    public Comparator<MethodId> method_comparator();

    public Comparator<MethodHandleItem> method_handle_comparator();

    public Comparator<AnnotationItem> annotation_comparator();

    public Comparator<EncodedField> encoded_field_comparator();

    public Comparator<EncodedMethod> encoded_method_comparator();

    public int getStringIndex(String value);

    public int getTypeIndex(TypeId value);

    public int getProtoIndex(ProtoId value);

    public int getFieldIndex(FieldId value);

    public int getMethodIndex(MethodId value);

    public int getMethodHandleIndex(MethodHandleItem value);

    public int getTypeListOffset(TypeList value);

    public int getAnnotationOffset(AnnotationItem value);

    public int getAnnotationSetOffset(AnnotationSet value);

    public int getAnnotationSetListOffset(AnnotationSetList value);

    public int getClassDataOffset(ClassData value);

    public int getAnnotationsDirectoryOffset(ClassDef value);
}
