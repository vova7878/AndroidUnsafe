package com.v7878.unsafe.memory;

@FunctionalInterface
public interface Bindable<T> {

    public static final Bindable<String> CSTRING = a -> {
        Pointer ptr = a.pointer();
        return ptr.isNull() ? null : ptr.getCString();
    };
    public static final Bindable<Pointer> POINTER = a -> a.pointer();

    public T bind(Addressable a);
}
