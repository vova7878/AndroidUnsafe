package com.v7878.unsafe.function;

import com.v7878.unsafe.memory.*;
import java.io.Closeable;
import java.lang.invoke.MethodHandle;
import java.util.*;

@FunctionalInterface
public interface SymbolLookup extends Closeable {

    public Optional<Pointer> find(String name);

    public default Pointer lookup(String name) {
        Optional<Pointer> addr = find(name);
        if (!addr.isPresent()) {
            throw new IllegalArgumentException(
                    NativeLibrary.dlerror("Can`t find symbol " + name));
        }
        return addr.get();
    }

    @Override
    public default void close() {
    }

    public default MethodHandle lookupHandle(String name,
            FunctionDescriptor function) {
        Pointer symbol = lookup(name);
        return Linker.downcallHandle(symbol, function);
    }

    public static SymbolLookup defaultLookup() {
        return (name) -> {
            Pointer tmp = NativeLibrary.dlsym(null, name);
            return Optional.ofNullable(tmp);
        };
    }

    public static SymbolLookup handleLookup(Addressable handle) {
        Objects.requireNonNull(handle);
        return (name) -> {
            Pointer tmp = NativeLibrary.dlsym(handle, name);
            return Optional.ofNullable(tmp);
        };
    }

    public static NativeLibrary libraryLookup(String path) {
        return NativeLibrary.load(path);
    }

    /*public static SymbolLookup loaderLookup(ClassLoader loader) {
    }*/
}
