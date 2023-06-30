package com.v7878.unsafe.function;

//TODO: finalize?
public class LibArt {
    //it's safe to close because libart is already open by system
    public static SymbolLookup open() {
        //FIXME: libartd.so?
        return NativeLibrary.load("libart.so");
    }
}
