package com.v7878.unsafe.dex;

import static com.v7878.unsafe.Utils.*;
import com.v7878.unsafe.io.*;
import java.util.*;

public class CatchHandlerElement implements PublicCloneable {

    private TypeId type;
    private int address;

    public CatchHandlerElement(TypeId type, int address) {
        setType(type);
        setAddress(address);
    }

    public final void setType(TypeId type) {
        this.type = Objects.requireNonNull(type,
                "catch handler type can`t be null").clone();
    }

    public final TypeId getType() {
        return type;
    }

    public final void setAddress(int address) {
        assert_(address >= 0, IllegalArgumentException::new,
                "instruction address can`t be negative");
        this.address = address;
    }

    public final int getAddress() {
        return address;
    }

    public static CatchHandlerElement read(RandomInput in,
            ReadContext context, int[] offsets) {
        return new CatchHandlerElement(context.type(in.readULeb128()),
                CodeItem.getInstructionIndex(offsets, in.readULeb128()));
    }

    public void fillContext(DataSet data) {
        data.addType(type);
    }

    @Override
    public String toString() {
        return type + " " + address;
    }

    @Override
    public CatchHandlerElement clone() {
        return new CatchHandlerElement(type, address);
    }
}
