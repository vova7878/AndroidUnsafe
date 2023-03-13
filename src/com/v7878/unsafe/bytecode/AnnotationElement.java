package com.v7878.unsafe.bytecode;

import com.v7878.unsafe.io.RandomInput;
import java.util.Objects;

public class AnnotationElement {

    public StringId name;
    public EncodedValue value;

    public static AnnotationElement read(RandomInput in, ReadContext rc) {
        AnnotationElement out = new AnnotationElement();
        out.name = rc.strings[in.readULeb128()];
        EncodedValueReader reader = new EncodedValueReader(in);
        out.value = reader.readValue(rc);
        return out;
    }

    @Override
    public String toString() {
        return "AnnotationElement{" + name + " = " + value + "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AnnotationElement) {
            AnnotationElement aeobj = (AnnotationElement) obj;
            return Objects.equals(name, aeobj.name)
                    && Objects.equals(value, aeobj.value);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value);
    }
}
