package com.v7878.unsafe.dex;

// Temporary object. Needed to read or write
class ReadContextImpl implements ReadContext {

    private String[] strings;
    private TypeId[] types;
    private ProtoId[] protos;
    private FieldId[] fields;
    private MethodId[] methods;
    private MethodHandleItem[] method_handles;
    private CallSiteId[] call_sites;

    private DexOptions options;

    public ReadContextImpl(DexOptions options) {
        this.options = options;
    }

    @Override
    public DexOptions getOptions() {
        return options;
    }

    @Override
    public String string(int index) {
        return strings[index];
    }

    @Override
    public TypeId type(int index) {
        return types[index];
    }

    @Override
    public ProtoId proto(int index) {
        return protos[index];
    }

    @Override
    public FieldId field(int index) {
        return fields[index];
    }

    @Override
    public MethodId method(int index) {
        return methods[index];
    }

    @Override
    public MethodHandleItem method_handle(int index) {
        return method_handles[index];
    }

    @Override
    public CallSiteId call_site(int index) {
        return call_sites[index];
    }

    public void setStrings(String[] strings) {
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
