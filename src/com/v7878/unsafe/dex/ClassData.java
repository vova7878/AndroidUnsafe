package com.v7878.unsafe.dex;

import com.v7878.unsafe.dex.EncodedValue.*;
import com.v7878.unsafe.io.*;
import java.util.function.Consumer;

public class ClassData implements PublicCloneable {

    private PCList<EncodedField> static_fields;
    private PCList<EncodedField> instance_fields;
    private PCList<EncodedMethod> direct_methods;
    private PCList<EncodedMethod> virtual_methods;

    public ClassData(PCList<EncodedField> static_fields,
            PCList<EncodedField> instance_fields,
            PCList<EncodedMethod> direct_methods,
            PCList<EncodedMethod> virtual_methods) {
        setStaticFields(static_fields);
        setInstanceFields(instance_fields);
        setDirectMethods(direct_methods);
        setVirtualMethods(virtual_methods);
    }

    public final void setStaticFields(PCList<EncodedField> static_fields) {
        this.static_fields = static_fields == null
                ? PCList.empty() : static_fields.clone();
    }

    public final PCList<EncodedField> getStaticFields() {
        return static_fields;
    }

    public final void setInstanceFields(PCList<EncodedField> instance_fields) {
        this.instance_fields = instance_fields == null
                ? PCList.empty() : instance_fields.clone();
    }

    public final PCList<EncodedField> getInstanceFields() {
        return instance_fields;
    }

    public final void setDirectMethods(PCList<EncodedMethod> direct_methods) {
        this.direct_methods = direct_methods == null
                ? PCList.empty() : direct_methods.clone();
    }

    public final PCList<EncodedMethod> getDirectMethods() {
        return direct_methods;
    }

    public final void setVirtualMethods(PCList<EncodedMethod> virtual_methods) {
        this.virtual_methods = virtual_methods == null
                ? PCList.empty() : virtual_methods.clone();
    }

    public final PCList<EncodedMethod> getVirtualMethods() {
        return virtual_methods;
    }

    public boolean isEmpty() {
        return static_fields.isEmpty() && instance_fields.isEmpty()
                && direct_methods.isEmpty() && virtual_methods.isEmpty();
    }

    public static ClassData empty() {
        return new ClassData(null, null, null, null);
    }

    public static ClassData read(RandomInput in, ReadContext context,
            ArrayValue static_values, AnnotationsDirectory annotations) {
        ClassData out = empty();
        int static_fields_size = in.readULeb128();
        int instance_fields_size = in.readULeb128();
        int direct_methods_size = in.readULeb128();
        int virtual_methods_size = in.readULeb128();
        out.static_fields = EncodedField.readArray(in, context,
                static_fields_size, annotations.annotated_fields);
        for (int i = 0; i < static_values.size(); i++) {
            out.static_fields.get(i).setValue(static_values.get(i));
        }
        out.instance_fields = EncodedField.readArray(in, context,
                instance_fields_size, annotations.annotated_fields);
        out.direct_methods = EncodedMethod.readArray(in, context,
                direct_methods_size, annotations.annotated_methods,
                annotations.annotated_parameters);
        out.virtual_methods = EncodedMethod.readArray(in, context,
                virtual_methods_size, annotations.annotated_methods,
                annotations.annotated_parameters);
        return out;
    }

    public void collectData(DataCollector data) {
        for (EncodedField tmp : static_fields) {
            data.fill(tmp);
        }
        for (EncodedField tmp : instance_fields) {
            data.fill(tmp);
        }
        for (EncodedMethod tmp : direct_methods) {
            data.fill(tmp);
        }
        for (EncodedMethod tmp : virtual_methods) {
            data.fill(tmp);
        }
    }

    public void write(WriteContext context, RandomOutput out) {
        out.writeULeb128(static_fields.size());
        out.writeULeb128(instance_fields.size());
        out.writeULeb128(direct_methods.size());
        out.writeULeb128(virtual_methods.size());

        EncodedField.writeArray(true, context, out, static_fields);
        EncodedField.writeArray(false, context, out, instance_fields);

        //TODO: check direct
        EncodedMethod.writeArray(context, out, direct_methods);
        EncodedMethod.writeArray(context, out, virtual_methods);
    }

    @Override
    public ClassData clone() {
        return new ClassData(static_fields, instance_fields,
                direct_methods, virtual_methods);
    }

    public void fillAnnotations(AnnotationsDirectory all_annotations) {
        Consumer<EncodedField> fill_field = (field) -> {
            AnnotationSet fannotations = field.getAnnotations();
            if (!fannotations.isEmpty()) {
                all_annotations.addFieldAnnotations(field.getField(), fannotations);
            }
        };
        static_fields.stream().forEach(fill_field);
        instance_fields.stream().forEach(fill_field);

        Consumer<EncodedMethod> fill_method = (method) -> {
            AnnotationSet mannotations = method.getAnnotations();
            if (!mannotations.isEmpty()) {
                all_annotations.addMethodAnnotations(method.getMethod(), mannotations);
            }
            AnnotationSetList pannotations = method.getParameterAnnotations();
            if (!pannotations.isEmpty()) {
                all_annotations.addMethodParameterAnnotations(method.getMethod(), pannotations);
            }
        };
        direct_methods.stream().forEach(fill_method);
        virtual_methods.stream().forEach(fill_method);
    }
}
