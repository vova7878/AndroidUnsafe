package com.v7878.unsafe.dex;

import com.v7878.unsafe.io.*;
import java.lang.reflect.Modifier;
import java.util.*;

public class EncodedMethod implements PublicCloneable {

    public static final Comparator<EncodedMethod> getComparator(WriteContext context) {
        return (a, b) -> {
            if (a.equals(b)) {
                return 0;
            }

            int out = context.method_comparator()
                    .compare(a.method, b.method);
            if (out != 0) {
                return out;
            }

            // a != b, but a.field == b.field
            throw new IllegalStateException("can`t compare encoded methods " + a + " " + b);
        };
    }

    private MethodId method;
    private int access_flags;
    private AnnotationSet annotations;
    private AnnotationSetList parameter_annotations;
    private CodeItem code;

    public EncodedMethod(MethodId method,
            int access_flags, AnnotationSet annotations,
            AnnotationSetList parameter_annotations, CodeItem code) {
        setMethod(method);
        setAccessFlags(access_flags);
        setAnnotations(annotations);
        setParameterAnnotations(parameter_annotations);
        setCode(code);
    }

    public final void setMethod(MethodId method) {
        this.method = Objects.requireNonNull(method,
                "mathod can`t be null").clone();
    }

    public final MethodId getMethod() {
        return method;
    }

    public final void setAccessFlags(int access_flags) {
        this.access_flags = access_flags;
    }

    public final int getAccessFlags() {
        return access_flags;
    }

    public final void setAnnotations(AnnotationSet annotations) {
        this.annotations = annotations == null
                ? AnnotationSet.empty() : annotations.clone();
    }

    public final AnnotationSet getAnnotations() {
        return annotations;
    }

    public final void setParameterAnnotations(
            AnnotationSetList parameter_annotations) {
        this.parameter_annotations = parameter_annotations == null
                ? AnnotationSetList.empty() : parameter_annotations.clone();
    }

    public final AnnotationSetList getParameterAnnotations() {
        return parameter_annotations;
    }

    public final void setCode(CodeItem code) {
        //TODO: clone
        this.code = code;
    }

    public final CodeItem getCode() {
        return code;
    }

    public static EncodedMethod read(RandomInput in, ReadContext context,
            MethodId method,
            Map<MethodId, AnnotationSet> annotated_methods,
            Map<MethodId, AnnotationSetList> annotated_parameters) {
        int access_flags = in.readULeb128();
        AnnotationSet annotations = annotated_methods.get(method);
        AnnotationSetList parameter_annotations
                = annotated_parameters.get(method);
        int code_off = in.readULeb128();
        CodeItem code = null;
        if (code_off != 0) {
            code = CodeItem.read(in.duplicate(code_off), context);
        }
        return new EncodedMethod(method, access_flags, annotations,
                parameter_annotations, code);
    }

    public static PCList<EncodedMethod> readArray(RandomInput in,
            ReadContext context, int size,
            Map<MethodId, AnnotationSet> annotated_methods,
            Map<MethodId, AnnotationSetList> annotated_parameters) {
        PCList<EncodedMethod> out = PCList.empty();
        int methodIndex = 0;
        for (int i = 0; i < size; i++) {
            methodIndex += in.readULeb128();
            out.add(read(in, context, context.method(methodIndex),
                    annotated_methods, annotated_parameters));
        }
        return out;
    }

    public void fillContext(DataSet data) {
        data.addMethod(method);
        if (!annotations.isEmpty()) {
            data.addAnnotationSet(annotations);
        }
        if (!parameter_annotations.isEmpty()) {
            data.addAnnotationSetList(parameter_annotations);
        }
        if (code != null) {
            code.fillContext(data);
        }
    }

    public void write(WriteContext context, RandomOutput out) {
        out.writeULeb128(access_flags);
        out.writeULeb128(0); // TODO: code
    }

    public static void writeArray(WriteContext context, RandomOutput out,
            EncodedMethod[] encoded_methods) {
        Arrays.sort(encoded_methods, context.encoded_method_comparator());
        int fieldIndex = 0;
        for (EncodedMethod tmp : encoded_methods) {
            int diff = context.getMethodIndex(tmp.method) - fieldIndex;
            fieldIndex += diff;
            out.writeULeb128(diff);
            tmp.write(context, out);
        }
    }

    public static void writeArray(WriteContext context, RandomOutput out,
            PCList<EncodedMethod> encoded_methods) {
        writeArray(context, out, encoded_methods
                .stream().toArray(EncodedMethod[]::new));
    }

    @Override
    public String toString() {
        String flags = Modifier.toString(access_flags);
        if (flags.length() != 0) {
            flags += " ";
        }
        return flags + method;
    }

    @Override
    public PublicCloneable clone() {
        return new EncodedMethod(method, access_flags, annotations,
                parameter_annotations, code);
    }
}