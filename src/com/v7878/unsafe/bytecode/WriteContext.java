package com.v7878.unsafe.bytecode;

import static com.v7878.unsafe.Utils.*;
import java.util.*;
import java.util.stream.*;

public class WriteContext {

    private final String[] strings;
    private final TypeId[] types;
    private final ProtoId[] protos;
    private final FieldId[] fields;
    private final MethodId[] methods;
    private final ClassDef[] class_defs;
    private final CallSiteId[] call_sites;
    private final MethodHandleItem[] method_handles;

    private final Comparator<TypeId> type_comparator
            = TypeId.getComparator(this);

    public WriteContext(DataSet data) {
        strings = data.getStrings();
        Arrays.sort(strings, StringId.COMPARATOR);
        types = data.getTypes();
        Arrays.sort(types, type_comparator);

        //TODO: sort
        protos = data.getProtos();
        fields = data.getFields();
        methods = data.getMethods();
        class_defs = data.getClassDefs();
        call_sites = data.getCallSites();
        method_handles = data.getMethodHandles();
    }

    public Stream<String> stringsStream() {
        return Arrays.stream(strings);
    }

    public Stream<TypeId> typesStream() {
        return Arrays.stream(types);
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
}
