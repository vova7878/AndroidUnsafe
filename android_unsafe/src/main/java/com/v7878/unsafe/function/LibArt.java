package com.v7878.unsafe.function;

import static com.v7878.unsafe.AndroidUnsafe4.vmLibrary;

//TODO: finalize?
public class LibArt {
    //it's safe to close because libart is already open by system
    public static SymbolLookup open() {
        return NativeLibrary.load(vmLibrary());
    }
}
