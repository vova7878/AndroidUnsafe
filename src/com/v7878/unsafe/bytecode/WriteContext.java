package com.v7878.unsafe.bytecode;

import static com.v7878.unsafe.Utils.*;
import com.v7878.unsafe.bytecode.TypeId.*;
import java.util.*;
import java.util.stream.*;

public class WriteContext {

    public final Comparator<TypeId> type_comparator
            = TypeId.getComparator(this);
    public final Comparator<TypeList> type_list_comparator
            = TypeList.getComparator(this);
    public final Comparator<ProtoId> proto_comparator
            = ProtoId.getComparator(this);
    public final Comparator<FieldId> field_comparator
            = FieldId.getComparator(this);
    public final Comparator<MethodId> method_comparator
            = MethodId.getComparator(this);

    public final Comparator<MethodHandleItem> method_handle_comparator
            = MethodHandleItem.getComparator(this);

    private final String[] strings;
    private final TypeId[] types;
    private final ProtoId[] protos;
    private final FieldId[] fields;
    private final MethodId[] methods;
    private final ClassDef[] class_defs;
    private final CallSiteId[] call_sites;
    private final MethodHandleItem[] method_handles;
    private final Map<TypeList, Integer> type_lists;

    public WriteContext(DataSet data) {
        strings = data.getStrings();
        Arrays.sort(strings, StringId.COMPARATOR);
        types = data.getTypes();
        Arrays.sort(types, type_comparator);
        protos = data.getProtos();
        Arrays.sort(protos, proto_comparator);
        fields = data.getFields();
        Arrays.sort(fields, field_comparator);
        methods = data.getMethods();
        Arrays.sort(methods, method_comparator);

        method_handles = data.getMethodHandles();
        Arrays.sort(method_handles, method_handle_comparator);

        type_lists = new HashMap<>();

        //TODO: sort
        class_defs = data.getClassDefs();
        call_sites = data.getCallSites();
    }

    public void addTypeList(TypeList type_list, int offset) {
        type_lists.put(type_list, offset);
    }

    public Stream<String> stringsStream() {
        return Arrays.stream(strings);
    }

    public Stream<TypeId> typesStream() {
        return Arrays.stream(types);
    }

    public Stream<ProtoId> protosStream() {
        return Arrays.stream(protos);
    }

    public Stream<FieldId> fieldsStream() {
        return Arrays.stream(fields);
    }

    public Stream<MethodId> methodsStream() {
        return Arrays.stream(methods);
    }

    public Stream<MethodHandleItem> methodHandlesStream() {
        return Arrays.stream(method_handles);
    }

    public int getStringsCount() {
        return strings.length;
    }

    public int getTypesCount() {
        return types.length;
    }

    public int getProtosCount() {
        return protos.length;
    }

    public int getFieldsCount() {
        return fields.length;
    }

    public int getMethodsCount() {
        return methods.length;
    }

    public int getClassDefsCount() {
        return class_defs.length;
    }

    public int getCallSitesCount() {
        return call_sites.length;
    }

    public int getMethodHandlesCount() {
        return method_handles.length;
    }

    public int getStringIndex(String value) {
        int out = Arrays.binarySearch(strings, value, StringId.COMPARATOR);
        assert_(out >= 0, IllegalArgumentException::new,
                "unable to find string \"" + value + "\"");
        return out;
    }

    public int getTypeIndex(TypeId value) {
        int out = Arrays.binarySearch(types, value, type_comparator);
        assert_(out >= 0, IllegalArgumentException::new,
                "unable to find type \"" + value + "\"");
        return out;
    }

    public int getProtoIndex(ProtoId value) {
        int out = Arrays.binarySearch(protos, value, proto_comparator);
        assert_(out >= 0, IllegalArgumentException::new,
                "unable to find proto \"" + value + "\"");
        return out;
    }

    public int getFieldIndex(FieldId value) {
        int out = Arrays.binarySearch(fields, value, field_comparator);
        assert_(out >= 0, IllegalArgumentException::new,
                "unable to find field \"" + value + "\"");
        return out;
    }

    public int getMethodIndex(MethodId value) {
        int out = Arrays.binarySearch(methods, value, method_comparator);
        assert_(out >= 0, IllegalArgumentException::new,
                "unable to find method \"" + value + "\"");
        return out;
    }

    public int getMethodHandleIndex(MethodHandleItem value) {
        int out = Arrays.binarySearch(method_handles, value, method_handle_comparator);
        assert_(out >= 0, IllegalArgumentException::new,
                "unable to find method handle \"" + value + "\"");
        return out;
    }

    public int getTypeListOffset(TypeList value) {
        Integer out = type_lists.get(value);
        assert_(out != null, IllegalArgumentException::new,
                "unable to find type list \"" + value + "\"");
        return out;
    }
}
