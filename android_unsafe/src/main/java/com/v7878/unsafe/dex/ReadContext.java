package com.v7878.unsafe.dex;

public interface ReadContext {

    DexOptions getOptions();

    String string(int index);

    TypeId type(int index);

    ProtoId proto(int index);

    FieldId field(int index);

    MethodId method(int index);

    MethodHandleItem method_handle(int index);

    CallSiteId call_site(int index);
}
