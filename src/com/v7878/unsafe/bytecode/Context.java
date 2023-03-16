package com.v7878.unsafe.bytecode;

public class Context {

    private StringId[] strings;
    private TypeId[] types;
    private ProtoId[] protos;
    private FieldId[] fields;
    private MethodId[] methods;
    private MethodHandleItem[] method_handles;
    private CallSiteId[] call_sites;

    public StringId string(int index) {
        return strings[index];
    }

    public TypeId type(int index) {
        return types[index];
    }

    public ProtoId proto(int index) {
        return protos[index];
    }

    public FieldId field(int index) {
        return fields[index];
    }

    public MethodId method(int index) {
        return methods[index];
    }

    public MethodHandleItem method_handle(int index) {
        return method_handles[index];
    }

    public CallSiteId call_site(int index) {
        return call_sites[index];
    }

    public void setStrings(StringId[] strings) {
        this.strings = strings;
    }

    public void setTypes(TypeId[] types) {
        this.types = types;
    }

    public void setProtos(ProtoId[] protos) {
        this.protos = protos;
    }

    public void setFields(FieldId[] fields) {
        this.fields = fields;
    }

    public void setMethods(MethodId[] methods) {
        this.methods = methods;
    }

    public void setMethodHandles(MethodHandleItem[] method_handles) {
        this.method_handles = method_handles;
    }

    public void setCallSites(CallSiteId[] call_sites) {
        this.call_sites = call_sites;
    }
}
