package com.v7878.unsafe.bytecode;

import java.util.*;

public class DataSet {

    private final Set<String> strings;
    private final Set<TypeId> types;
    private final Set<ProtoId> protos;
    private final Set<FieldId> fields;
    private final Set<MethodId> methods;
    private final Set<MethodHandleItem> method_handles;
    private final Set<CallSiteId> call_sites;
    private final Set<ClassDef> class_defs;

    public DataSet() {
        strings = new HashSet<>();
        types = new HashSet<>();
        protos = new HashSet<>();
        fields = new HashSet<>();
        methods = new HashSet<>();
        method_handles = new HashSet<>();
        call_sites = new HashSet<>();
        class_defs = new HashSet<>();
    }

    public void addString(String string) {
        strings.add(string);
    }

    public void addType(TypeId type) {
        type.fillContext(this);
        types.add(type);
    }

    public void addProto(ProtoId proto) {
        proto.fillContext(this);
        protos.add(proto);
    }

    public void addField(FieldId field) {
        field.fillContext(this);
        fields.add(field);
    }

    public void addMethod(MethodId method) {
        method.fillContext(this);
        methods.add(method);
    }

    public void addMethodHandle(MethodHandleItem method_handle) {
        method_handle.fillContext(this);
        method_handles.add(method_handle);
    }

    public void addCallSite(CallSiteId call_site) {
        call_site.fillContext(this);
        call_sites.add(call_site);
    }

    public void addClassDef(ClassDef class_def) {
        class_def.fillContext(this);
        class_defs.add(class_def);
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
        return class_defs.stream().toArray(ClassDef[]::new);
    }
}
