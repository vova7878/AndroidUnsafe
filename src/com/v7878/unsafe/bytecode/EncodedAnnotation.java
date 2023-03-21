package com.v7878.unsafe.bytecode;

import com.v7878.unsafe.io.*;
import java.util.*;
import java.util.stream.*;

public class EncodedAnnotation implements PublicCloneable {

    private TypeId type;
    private AnnotationElement[] elements;

    public EncodedAnnotation(TypeId type, AnnotationElement[] elements) {
        setType(type);
        setElements(elements);
    }

    public final void setType(TypeId type) {
        this.type = Objects.requireNonNull(type,
                "type can`t be null").clone();
    }

    public final TypeId getType() {
        return type;
    }

    public final void setElements(AnnotationElement[] elements) {
        if (elements == null) {
            elements = new AnnotationElement[0];
        }
        this.elements = Arrays.stream(elements)
                .map(tmp -> Objects.requireNonNull(tmp,
                "annotation elements can`t contain null element"))
                .map(AnnotationElement::clone)
                .toArray(AnnotationElement[]::new);
    }

    public final AnnotationElement[] getElements() {
        return Arrays.copyOf(elements, elements.length);
    }

    public static EncodedAnnotation read(RandomInput in, ReadContext context) {
        TypeId type = context.type(in.readULeb128());
        int size = in.readULeb128();
        AnnotationElement[] elements = new AnnotationElement[size];
        for (int i = 0; i < size; i++) {
            elements[i] = AnnotationElement.read(in, context);
        }
        return new EncodedAnnotation(type, elements);
    }

    public void fillContext(DataSet data) {
        data.addType(type);
        for (AnnotationElement tmp : elements) {
            tmp.fillContext(data);
        }
    }

    public void write(WriteContext context, RandomOutput out) {
        out.writeULeb128(context.getTypeIndex(type));
        out.writeULeb128(elements.length);
        for (AnnotationElement tmp : elements) {
            tmp.write(context, out);
        }
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

    @Override
    public EncodedAnnotation clone() {
        return new EncodedAnnotation(type, elements);
    }
}
