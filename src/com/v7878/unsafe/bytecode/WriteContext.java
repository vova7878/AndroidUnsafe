package com.v7878.unsafe.bytecode;

import java.util.*;

public class WriteContext {

    private final String[] strings;
    private final TypeId[] types;
    private final ProtoId[] protos;
    private final FieldId[] fields;
    private final MethodId[] methods;
    private final ClassDef[] class_defs;
    private final CallSiteId[] call_sites;
    private final MethodHandleItem[] method_handles;

    public WriteContext(DataSet data) {
        strings = data.getStrings();

        //TODO: right?
        Arrays.sort(strings, String.CASE_INSENSITIVE_ORDER);

        //TODO: sort
        types = data.getTypes();
        protos = data.getProtos();
        fields = data.getFields();
        methods = data.getMethods();
        class_defs = data.getClassDefs();
        call_sites = data.getCallSites();
        method_handles = data.getMethodHandles();
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
}
