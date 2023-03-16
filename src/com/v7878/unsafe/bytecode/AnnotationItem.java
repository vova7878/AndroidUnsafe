package com.v7878.unsafe.bytecode;

import com.v7878.unsafe.io.RandomInput;
import java.util.Objects;

public class AnnotationItem {

    public byte visibility;
    public EncodedAnnotation annotation;

    public static AnnotationItem read(RandomInput in, Context context) {
        AnnotationItem out = new AnnotationItem();
        out.visibility = in.readByte();
        out.annotation = EncodedAnnotation.read(in, context);
        return out;
    }

    public static AnnotationItem[] readSet(RandomInput in, Context context) {
        int size = in.readInt();
        AnnotationItem[] out = new AnnotationItem[size];
        for (int i = 0; i < size; i++) {
            out[i] = AnnotationItem.read(in.duplicate(in.readInt()), context);
        }
        return out;
    }

    public static AnnotationItem[][] readSetList(RandomInput in, Context context) {
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
}
