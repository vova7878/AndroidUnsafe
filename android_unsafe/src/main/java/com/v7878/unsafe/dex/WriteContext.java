package com.v7878.unsafe.dex;

import com.v7878.unsafe.dex.EncodedValue.ArrayValue;

import java.util.Comparator;

public interface WriteContext {

    Comparator<TypeId> type_comparator();

    Comparator<TypeList> type_list_comparator();

    Comparator<ProtoId> proto_comparator();

    Comparator<FieldId> field_comparator();

    Comparator<MethodId> method_comparator();

    Comparator<MethodHandleItem> method_handle_comparator();

    Comparator<AnnotationItem> annotation_comparator();

    Comparator<EncodedField> encoded_field_comparator();

    Comparator<EncodedMethod> encoded_method_comparator();

    int getStringIndex(String value);

    int getTypeIndex(TypeId value);

    int getProtoIndex(ProtoId value);

    int getFieldIndex(FieldId value);

    int getMethodIndex(MethodId value);

    int getMethodHandleIndex(MethodHandleItem value);

    int getTypeListOffset(TypeList value);

    int getAnnotationOffset(AnnotationItem value);

    int getAnnotationSetOffset(AnnotationSet value);

    int getAnnotationSetListOffset(AnnotationSetList value);

    int getClassDataOffset(ClassData value);

    int getAnnotationsDirectoryOffset(ClassDef value);

    int getArrayValueOffset(ArrayValue value);

    int getCodeItemOffset(CodeItem value);
}
