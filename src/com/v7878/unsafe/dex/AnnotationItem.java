package com.v7878.unsafe.dex;

import static com.v7878.unsafe.dex.DexConstants.*;
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

            // TODO: repeatable?
            // a != b, but a.type == b.type
            throw new IllegalStateException("can`t compare annotations " + a + " " + b);
        };
    }

    public static AnnotationItem FastNative() {
        return new AnnotationItem(VISIBILITY_BUILD, TypeId.of(
                "dalvik.annotation.optimization.FastNative"));
    }

    public static AnnotationItem CriticalNative() {
        return new AnnotationItem(VISIBILITY_BUILD, TypeId.of(
                "dalvik.annotation.optimization.CriticalNative"));
    }

    public static AnnotationItem AnnotationDefault(EncodedAnnotation annotation) {
        return new AnnotationItem(VISIBILITY_SYSTEM, TypeId.of(
                "dalvik.annotation.AnnotationDefault"),
                new AnnotationElement("value", EncodedValue.of(annotation)));
    }

    public static AnnotationItem EnclosingClass(TypeId clazz) {
        return new AnnotationItem(VISIBILITY_SYSTEM, TypeId.of(
                "dalvik.annotation.EnclosingClass"),
                new AnnotationElement("value", EncodedValue.of(clazz)));
    }

    public static AnnotationItem EnclosingMethod(MethodId method) {
        return new AnnotationItem(VISIBILITY_SYSTEM, TypeId.of(
                "dalvik.annotation.EnclosingMethod"),
                new AnnotationElement("value", EncodedValue.of(method)));
    }

    public static AnnotationItem InnerClass(String name, int access_flags) {
        return new AnnotationItem(VISIBILITY_SYSTEM, TypeId.of(
                "dalvik.annotation.InnerClass"),
                new AnnotationElement("name", EncodedValue.of(name)),
                new AnnotationElement("accessFlags", EncodedValue.of(access_flags)));
    }

    public static AnnotationItem MemberClasses(TypeId... classes) {
        return new AnnotationItem(VISIBILITY_SYSTEM, TypeId.of(
                "dalvik.annotation.MemberClasses"), new AnnotationElement(
                        "value", EncodedValue.of(classes)));
    }

    public static AnnotationItem MethodParameters(String[] names, int[] access_flags) {
        return new AnnotationItem(VISIBILITY_SYSTEM, TypeId.of(
                "dalvik.annotation.MethodParameters"),
                new AnnotationElement("names", EncodedValue.of(names)),
                new AnnotationElement("accessFlags", EncodedValue.of(access_flags)));
    }

    public static AnnotationItem Signature(String... value) {
        return new AnnotationItem(VISIBILITY_SYSTEM, TypeId.of(
                "dalvik.annotation.Signature"), new AnnotationElement(
                        "value", EncodedValue.of(value)));
    }

    public static AnnotationItem Throws(TypeId... exceptions) {
        return new AnnotationItem(VISIBILITY_SYSTEM, TypeId.of(
                "dalvik.annotation.Throws"), new AnnotationElement(
                        "value", EncodedValue.of(exceptions)));
    }

    private int visibility;
    private EncodedAnnotation annotation;

    public AnnotationItem(int visibility, EncodedAnnotation annotation) {
        setVisibility(visibility);
        setAnnotation(annotation);
    }

    public AnnotationItem(int visibility, TypeId type, AnnotationElement... elements) {
        setVisibility(visibility);
        setAnnotation(new EncodedAnnotation(type, elements));
    }

    //TODO: check
    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }

    public int getVisibility() {
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
        return new AnnotationItem(in.readUnsignedByte(),
                EncodedAnnotation.read(in, context));
    }

    public void collectData(DataCollector data) {
        data.fill(annotation);
    }

    public void write(WriteContext context, RandomOutput out) {
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
