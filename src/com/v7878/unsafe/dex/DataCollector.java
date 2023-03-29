package com.v7878.unsafe.dex;

import com.v7878.unsafe.dex.EncodedValue.*;
import com.v7878.unsafe.dex.bytecode.Instruction;

public interface DataCollector {

    public void add(String value);

    public void add(TypeId value);

    public void add(ProtoId value);

    public void add(FieldId value);

    public void add(MethodId value);

    public void add(MethodHandleItem value);

    public void add(CallSiteId value);

    public void add(ClassDef value);

    public void add(ClassData value);

    public void add(TypeList value);

    public void add(AnnotationItem value);

    public void add(AnnotationSet value);

    public void add(AnnotationSetList value);

    public void add(ClassDef clazz, AnnotationsDirectory value);

    public void add(ArrayValue value);

    public void add(CodeItem value);

    public void fill(ArrayValue value);

    public void fill(CatchHandlerElement value);

    public void fill(CatchHandler value);

    public void fill(Instruction value);

    public void fill(TryItem value);

    public void fill(EncodedField value);

    public void fill(EncodedMethod value);

    public void fill(EncodedAnnotation value);

    public void fill(EncodedValue value);
}
