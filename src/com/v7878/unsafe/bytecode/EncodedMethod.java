package com.v7878.unsafe.bytecode;

import com.v7878.unsafe.io.RandomInput;
import java.lang.reflect.Modifier;
import java.util.Map;

public class EncodedMethod {

    public MethodId method;
    public int access_flags;
    public AnnotationItem[] annotations;
    public AnnotationItem[][] parameter_annotations;
    public CodeItem code;

    public static EncodedMethod read(RandomInput in, ReadContext rc,
            MethodId method,
            Map<MethodId, AnnotationItem[]> annotated_methods,
            Map<MethodId, AnnotationItem[][]> annotated_parameters) {
        EncodedMethod out = new EncodedMethod();
        out.method = method;
        out.access_flags = in.readULeb128();
        out.annotations = annotated_methods
                .getOrDefault(method, new AnnotationItem[0]);
        out.parameter_annotations = annotated_parameters
                .getOrDefault(method, new AnnotationItem[0][0]);
        int code_off = in.readULeb128();
        if (code_off != 0) {
            out.code = CodeItem.read(in.duplicate(code_off), rc);
        }
        return out;
    }

    public static EncodedMethod[] readArray(RandomInput in,
            ReadContext rc, int size,
            Map<MethodId, AnnotationItem[]> annotated_methods,
            Map<MethodId, AnnotationItem[][]> annotated_parameters) {
        EncodedMethod[] out = new EncodedMethod[size];
        int methodIndex = 0;
        for (int i = 0; i < size; i++) {
            methodIndex += in.readULeb128();
            out[i] = read(in, rc, rc.methods[methodIndex],
                    annotated_methods, annotated_parameters);
        }
        return out;
    }

    @Override
    public String toString() {
        String flags = Modifier.toString(access_flags);
        if (flags.length() != 0) {
            flags += " ";
        }
        return flags + method;
    }
}
