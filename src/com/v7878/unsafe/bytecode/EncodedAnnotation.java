package com.v7878.unsafe.bytecode;

import com.v7878.unsafe.io.RandomInput;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class EncodedAnnotation {

    public TypeId type;
    public AnnotationElement[] elements;

    public static EncodedAnnotation read(RandomInput in, ReadContext rc) {
        EncodedAnnotation out = new EncodedAnnotation();
        out.type = rc.types[in.readULeb128()];
        int size = in.readULeb128();
        out.elements = new AnnotationElement[size];
        for (int i = 0; i < size; i++) {
            out.elements[i] = AnnotationElement.read(in, rc);
        }
        return out;
    }

    @Override
    public String toString() {
        String elems = Arrays.stream(elements)
                .map((p) -> p.toString())
                .collect(Collectors.joining(", "));
        return "EncodedAnnotation{" + type + "; " + elems + "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof EncodedAnnotation) {
            EncodedAnnotation eaobj = (EncodedAnnotation) obj;
            return Objects.equals(type, eaobj.type)
                    && Arrays.equals(elements, eaobj.elements);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, Arrays.hashCode(elements));
    }
}
