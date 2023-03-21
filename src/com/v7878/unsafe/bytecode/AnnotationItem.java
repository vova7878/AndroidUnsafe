package com.v7878.unsafe.bytecode;

import com.v7878.unsafe.io.*;
import java.util.*;

public final class AnnotationItem implements PublicCloneable {

    public static final Comparator<AnnotationItem> getComparator(WriteContext context) {
        return (a, b) -> {
            if (a.equals(b)) {
                return 0;
            }

            int out = context.type_comparator()
                    .compare(a.annotation.getType(), b.annotation.getType());
            if (out != 0) {
                return out;
            }

            // a != b, but a.type == b.type
            throw new IllegalStateException("can`t compare annotations " + a + " " + b);
        };
    }

    private byte visibility;
    private EncodedAnnotation annotation;

    public AnnotationItem(byte visibility, EncodedAnnotation annotation) {
        setVisibility(visibility);
        setAnnotation(annotation);
    }

    //TODO: check
    public void setVisibility(byte visibility) {
        this.visibility = visibility;
    }

    public byte getVisibility() {
        return visibility;
    }

    public void setAnnotation(EncodedAnnotation annotation) {
        this.annotation = Objects.requireNonNull(annotation,
                "annotation can`t be null").clone();
    }

    public EncodedAnnotation getAnnotation() {
        return annotation;
    }

    public static AnnotationItem read(RandomInput in, ReadContext context) {
        return new AnnotationItem(in.readByte(),
                EncodedAnnotation.read(in, context));
    }

    public void fillContext(DataSet data) {
        annotation.fillContext(data);
    }

    public void write(WriteContextImpl context, RandomOutput out) {
        out.writeByte(visibility);
        annotation.write(context, out);
    }

    @Override
    public String toString() {
        return "AnnotationItem{" + "visibility = " + visibility + "; " + annotation + "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AnnotationItem) {
            AnnotationItem aiobj = (AnnotationItem) obj;
            return Objects.equals(visibility, aiobj.visibility)
                    && Objects.equals(annotation, aiobj.annotation);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(visibility, annotation);
    }

    @Override
    public AnnotationItem clone() {
        return new AnnotationItem(visibility, annotation);
    }
}
