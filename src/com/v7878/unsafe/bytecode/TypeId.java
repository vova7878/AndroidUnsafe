package com.v7878.unsafe.bytecode;

import com.v7878.unsafe.io.*;
import java.util.*;

public class TypeId implements Cloneable {

    public static final int SIZE = 0x04;

    public static final Comparator<TypeId> getComparator(WriteContext context) {
        return (a, b) -> {
            if (a.equals(b)) {
                return 0;
            }
            return Integer.compare(context.getStringIndex(a.descriptor),
                    context.getStringIndex(b.descriptor));
        };
    }

    private String descriptor;

    public TypeId(String descriptor) {
        setDescriptor(descriptor);
    }

    public final void setDescriptor(String descriptor) {
        this.descriptor = Objects.requireNonNull(
                descriptor, "type descriptor can`t be null");
    }

    public final String getDescriptor() {
        return descriptor;
    }

    public final char getShorty() {
        char c = descriptor.charAt(0);
        return c == '[' ? 'L' : c;
    }

    public static TypeId read(RandomInput in, ReadContext context) {
        return new TypeId(context.string(in.readInt()));
    }

    public void fillContext(DataSet data) {
        data.addString(descriptor);
    }

    public void write(WriteContext context, RandomOutput out) {
        out.writeInt(context.getStringIndex(descriptor));
    }

    @Override
    public String toString() {
        return descriptor;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TypeId) {
            TypeId tobj = (TypeId) obj;
            return Objects.equals(descriptor, tobj.descriptor);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(descriptor);
    }

    @Override
    public TypeId clone() {
        return new TypeId(descriptor);
    }
}
