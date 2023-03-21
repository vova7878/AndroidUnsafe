package com.v7878.unsafe.bytecode;

import com.v7878.unsafe.io.*;
import java.util.*;

public class AnnotationSetList extends PCList<AnnotationSet> {

    public AnnotationSetList(AnnotationSet... annotation_sets) {
        super(annotation_sets);
    }

    public static AnnotationSetList read(RandomInput in, ReadContext context) {
        AnnotationSetList out = new AnnotationSetList();
        int size = in.readInt();
        for (int i = 0; i < size; i++) {
            int annotations_off = in.readInt();
            if (annotations_off != 0) {
                out.add(AnnotationSet.read(
                        in.duplicate(annotations_off), context));
            } else {
                out.add(AnnotationSet.empty());
            }
        }
        return out;
    }

    public static AnnotationSetList empty() {
        return new AnnotationSetList();
    }

    public void fillContext(DataSet data) {
        for (AnnotationSet tmp : this) {
            if (!tmp.isEmpty()) {
                data.addAnnotationSet(tmp);
            }
        }
    }

    public void write(WriteContext context, RandomOutput out) {
        out.writeInt(size());
        for (AnnotationSet tmp : this) {
            if (tmp.isEmpty()) {
                out.writeInt(0);
            } else {
                out.writeInt(context.getAnnotationSetOffset(tmp));
            }
        }
    }

    @Override
    protected AnnotationSet check(AnnotationSet annotation_set) {
        return Objects.requireNonNull(annotation_set,
                "AnnotationSetList can`t contain null annotation_set");
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AnnotationSetList) {
            return super.equals(obj);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public AnnotationSetList clone() {
        AnnotationSetList out = new AnnotationSetList();
        out.addAll(this);
        return out;
    }
}
