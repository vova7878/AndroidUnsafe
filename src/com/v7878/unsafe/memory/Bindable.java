package com.v7878.unsafe.memory;

@FunctionalInterface
public interface Bindable<T> {

    public static final Bindable<String> CSTRING = a -> a.pointer().getCString();

    public T bind(Addressable a);
}
