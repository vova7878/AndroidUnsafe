package com.v7878.unsafe.dex;

public interface ReadContext {

    public String string(int index);

    public TypeId type(int index);

    public ProtoId proto(int index);

    public FieldId field(int index);

    public MethodId method(int index);

    public MethodHandleItem method_handle(int index);

    public CallSiteId call_site(int index);
}
