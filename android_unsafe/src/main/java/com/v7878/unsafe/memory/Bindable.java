package com.v7878.unsafe.memory;

@FunctionalInterface
public interface Bindable<T> {

    Bindable<String> CSTRING = a -> {
        Pointer ptr = a.pointer();
        return ptr.isNull() ? null : ptr.getCString();
    };
    Bindable<Pointer> POINTER = Addressable::pointer;

    T bind(Addressable a);
}
