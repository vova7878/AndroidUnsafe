package com.v7878.unsafe.function;

import com.v7878.unsafe.AndroidUnsafe6.VMStack;
import com.v7878.unsafe.memory.Addressable;
import com.v7878.unsafe.memory.Pointer;

import java.io.Closeable;
import java.lang.invoke.MethodHandle;
import java.util.Objects;
import java.util.Optional;

@FunctionalInterface
public interface SymbolLookup extends Closeable {

    Optional<Pointer> find(String name);

    default Pointer lookup(String name) {
        Optional<Pointer> addr = find(name);
        if (!addr.isPresent()) {
            throw new IllegalArgumentException(
                    NativeLibrary.dlerror("Can`t find symbol " + name));
        }
        return addr.get();
    }

    @Override
    default void close() {
    }

    default MethodHandle lookupHandle(String name,
                                      FunctionDescriptor function) {
        Pointer symbol = lookup(name);
        return Linker.downcallHandle(symbol, function);
    }

    static SymbolLookup defaultLookup() {
        return (name) -> {
            Pointer tmp = NativeLibrary.dlsym(null, name);
            return Optional.ofNullable(tmp);
        };
    }

    static SymbolLookup handleLookup(Addressable handle) {
        Objects.requireNonNull(handle);
        return (name) -> {
            Pointer tmp = NativeLibrary.dlsym(handle, name);
            return Optional.ofNullable(tmp);
        };
    }

    static NativeLibrary libraryLookup(String path) {
        return NativeLibrary.load(path);
    }

    static NativeLibrary loaderLookup(ClassLoader loader, String libname) {
        return NativeLibrary.loadLibrary(loader, libname);
    }

    static NativeLibrary loaderLookup(String libname) {
        return loaderLookup(VMStack.INSTANCE.getStackClass1().getClassLoader(), libname);
    }
}
