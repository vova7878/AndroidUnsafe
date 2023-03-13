package com.v7878.unsafe.bytecode;

import java.util.Objects;

public abstract class FieldOrMethodId {

    public TypeId declaring_class;
    public StringId name;

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FieldOrMethodId) {
            FieldOrMethodId fmobj = (FieldOrMethodId) obj;
            return Objects.equals(declaring_class, fmobj.declaring_class)
                    && Objects.equals(name, fmobj.name);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(declaring_class, name);
    }
}
