package com.v7878.unsafe.bytecode;

import com.v7878.unsafe.io.RandomInput;
import java.util.Objects;

public final class AnnotationItem implements Cloneable {

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

    public static AnnotationItem[] readSet(RandomInput in, ReadContext context) {
        int size = in.readInt();
        AnnotationItem[] out = new AnnotationItem[size];
        for (int i = 0; i < size; i++) {
            out[i] = AnnotationItem.read(in.duplicate(in.readInt()), context);
        }
        return out;
    }

    public static AnnotationItem[][] readSetList(RandomInput in, ReadContext context) {
        int size = in.readInt();
        AnnotationItem[][] out = new AnnotationItem[size][];
        for (int i = 0; i < size; i++) {
            int annotations_off = in.readInt();
            if (annotations_off != 0) {
                out[i] = AnnotationItem.readSet(
                        in.duplicate(annotations_off), context);
            } else {
                out[i] = new AnnotationItem[0];
            }
        }
        return out;
    }

    public void fillContext(DataSet data) {
        annotation.fillContext(data);
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
