package com.v7878.unsafe.dex;

import static com.v7878.unsafe.Utils.assert_;
import static com.v7878.unsafe.Utils.roundUp;

import com.v7878.unsafe.Checks;
import com.v7878.unsafe.dex.EncodedValue.ArrayValue;
import com.v7878.unsafe.io.ByteArrayIO;
import com.v7878.unsafe.io.RandomIO;
import com.v7878.unsafe.io.RandomInput;
import com.v7878.unsafe.io.RandomOutput;

import java.util.Map;
import java.util.Objects;

public class Dex extends PCList<ClassDef> {

    public Dex(ClassDef... class_defs) {
        super(class_defs);
    }

    @Override
    protected ClassDef check(ClassDef class_def) {
        return Objects.requireNonNull(class_def,
                "Dex can`t contain null class def");
    }

    public static Dex read(RandomInput in) {
        return read(in, null);
    }


    public static Dex read(RandomInput in, int[] class_def_ids) {
        return read(in, DexOptions.defaultOptions(), class_def_ids);
    }

    public static Dex read(RandomInput in, DexOptions options, int[] class_def_ids) {
        FileMap map = FileMap.read(in, options);

        if (class_def_ids == null) {
            class_def_ids = new int[map.class_defs_size];
            for (int i = 0; i < map.class_defs_size; i++) {
                class_def_ids[i] = i;
            }
        } else {
            //TODO: check unique
            for (int id : class_def_ids) {
                Checks.checkIndex(id, map.class_defs_size);
            }
        }

        if (class_def_ids.length == 0) {
            return new Dex();
        }

        ReadContextImpl context = new ReadContextImpl(options);
        String[] strings = new String[map.string_ids_size];
        if (map.string_ids_size != 0) {
            RandomInput in2 = in.duplicate(map.string_ids_off);
            for (int i = 0; i < map.string_ids_size; i++) {
                strings[i] = StringId.read(in2);
            }
        }
        context.setStrings(strings);
        TypeId[] types = new TypeId[map.type_ids_size];
        if (map.type_ids_size != 0) {
            RandomInput in2 = in.duplicate(map.type_ids_off);
            for (int i = 0; i < map.type_ids_size; i++) {
                types[i] = TypeId.read(in2, context);
            }
        }
        context.setTypes(types);
        ProtoId[] protos = new ProtoId[map.proto_ids_size];
        if (map.proto_ids_size != 0) {
            RandomInput in2 = in.duplicate(map.proto_ids_off);
            for (int i = 0; i < map.proto_ids_size; i++) {
                protos[i] = ProtoId.read(in2, context);
            }
        }
        context.setProtos(protos);
        FieldId[] fields = new FieldId[map.field_ids_size];
        if (map.field_ids_size != 0) {
            RandomInput in2 = in.duplicate(map.field_ids_off);
            for (int i = 0; i < map.field_ids_size; i++) {
                fields[i] = FieldId.read(in2, context);
            }
        }
        context.setFields(fields);
        MethodId[] methods = new MethodId[map.method_ids_size];
        if (map.method_ids_size != 0) {
            RandomInput in2 = in.duplicate(map.method_ids_off);
            for (int i = 0; i < map.method_ids_size; i++) {
                methods[i] = MethodId.read(in2, context);
            }
        }
        context.setMethods(methods);
        MethodHandleItem[] method_handles = new MethodHandleItem[map.method_handles_size];
        if (map.method_handles_size != 0) {
            RandomInput in2 = in.duplicate(map.method_handles_off);
            for (int i = 0; i < map.method_handles_size; i++) {
                method_handles[i] = MethodHandleItem.read(in2, context);
            }
        }
        context.setMethodHandles(method_handles);
        CallSiteId[] call_sites = new CallSiteId[map.call_site_ids_size];
        if (map.call_site_ids_size != 0) {
            RandomInput in2 = in.duplicate(map.call_site_ids_off);
            for (int i = 0; i < map.call_site_ids_size; i++) {
                call_sites[i] = CallSiteId.read(in2, context);
            }
        }
        context.setCallSites(call_sites);
        ClassDef[] class_defs = new ClassDef[class_def_ids.length];
        for (int i = 0; i < class_def_ids.length; i++) {
            int offset = map.class_defs_off + ClassDef.SIZE * class_def_ids[i];
            RandomInput in2 = in.duplicate(offset);
            class_defs[i] = ClassDef.read(in2, context);
        }
        return new Dex(class_defs);
    }

    public void collectData(DataCollector data) {
        for (ClassDef tmp : this) {
            data.add(tmp);
        }
    }

    public void write(RandomIO out) {
        write(out, DexOptions.defaultOptions());
    }

    public void write(RandomIO out, DexOptions options) {
        assert_(out.position() == 0, IllegalArgumentException::new);

        DataSet data = new DataSet();
        collectData(data);

        WriteContextImpl context = new WriteContextImpl(data, options);

        FileMap map = new FileMap();

        int offset = FileMap.HEADER_SIZE;

        map.string_ids_off = offset;
        map.string_ids_size = context.getStringsCount();
        offset += map.string_ids_size * StringId.SIZE;

        map.type_ids_off = offset;
        map.type_ids_size = context.getTypesCount();
        offset += map.type_ids_size * TypeId.SIZE;

        map.proto_ids_off = offset;
        map.proto_ids_size = context.getProtosCount();
        offset += map.proto_ids_size * ProtoId.SIZE;

        map.field_ids_off = offset;
        map.field_ids_size = context.getFieldsCount();
        offset += map.field_ids_size * FieldId.SIZE;

        map.method_ids_off = offset;
        map.method_ids_size = context.getMethodsCount();
        offset += map.method_ids_size * MethodId.SIZE;

        map.class_defs_off = offset;
        map.class_defs_size = context.getClassDefsCount();
        offset += map.class_defs_size * ClassDef.SIZE;

        //TODO
        /*map.call_site_ids_off = offset;
        map.call_site_ids_size = context.getCallSitesCount();
        offset += map.call_site_ids_size * CallSiteId.SIZE;*/
        map.method_handles_off = offset;
        map.method_handles_size = context.getMethodHandlesCount();
        offset += map.method_handles_size * MethodHandleItem.SIZE;

        // writing
        //TODO: all sections
        map.data_off = offset;

        RandomOutput data_out = out.duplicate();

        map.string_data_items_off = offset;
        map.string_data_items_size = map.string_ids_size;
        out.position(map.string_ids_off);
        data_out.position(map.string_data_items_off);
        context.stringsStream().forEach((value) -> StringId.write(value, context, out, data_out));
        offset = (int) data_out.position();

        TypeList[] lists = data.getTypeLists();
        if (lists.length != 0) {
            offset = roundUp(offset, TypeList.ALIGNMENT);
            map.type_lists_off = offset;
            map.type_lists_size = lists.length;
            for (TypeList tmp : lists) {
                offset = roundUp(offset, TypeList.ALIGNMENT);
                data_out.position(offset);
                tmp.write(context, data_out);
                context.addTypeList(tmp, offset);
                offset = (int) data_out.position();
            }
        }

        AnnotationItem[] annotations = data.getAnnotations();
        if (annotations.length != 0) {
            map.annotations_off = offset;
            map.annotations_size = annotations.length;
            for (AnnotationItem tmp : annotations) {
                data_out.position(offset);
                tmp.write(context, data_out);
                context.addAnnotation(tmp, offset);
                offset = (int) data_out.position();
            }
        }

        AnnotationSet[] annotation_sets = data.getAnnotationSets();
        if (annotation_sets.length != 0) {
            offset = roundUp(offset, AnnotationSet.ALIGNMENT);
            map.annotation_sets_off = offset;
            map.annotation_sets_size = annotation_sets.length;
            for (AnnotationSet tmp : annotation_sets) {
                offset = roundUp(offset, AnnotationSet.ALIGNMENT);
                data_out.position(offset);
                tmp.write(context, data_out);
                context.addAnnotationSet(tmp, offset);
                offset = (int) data_out.position();
            }
        }

        AnnotationSetList[] annotation_set_lists = data.getAnnotationSetLists();
        if (annotation_set_lists.length != 0) {
            offset = roundUp(offset, AnnotationSet.ALIGNMENT);
            map.annotation_set_refs_off = offset;
            map.annotation_set_refs_size = annotation_set_lists.length;
            for (AnnotationSetList tmp : annotation_set_lists) {
                offset = roundUp(offset, AnnotationSet.ALIGNMENT);
                data_out.position(offset);
                tmp.write(context, data_out);
                context.addAnnotationSetList(tmp, offset);
                offset = (int) data_out.position();
            }
        }

        CodeItem[] code_items = data.getCodeItems();
        if (code_items.length != 0) {
            offset = roundUp(offset, CodeItem.ALIGNMENT);
            map.code_items_off = offset;
            map.code_items_size = code_items.length;
            for (CodeItem tmp : code_items) {
                offset = roundUp(offset, CodeItem.ALIGNMENT);
                data_out.position(offset);
                tmp.write(context, data_out);
                context.addCodeItem(tmp, offset);
                offset = (int) data_out.position();
            }
        }

        ClassData[] class_data_items = data.getClassDataItems();
        if (class_data_items.length != 0) {
            map.class_data_items_off = offset;
            map.class_data_items_size = class_data_items.length;
            for (ClassData tmp : class_data_items) {
                data_out.position(offset);
                tmp.write(context, data_out);
                context.addClassData(tmp, offset);
                offset = (int) data_out.position();
            }
        }

        //TODO: delete duplicates
        Map<ClassDef, AnnotationsDirectory> annotations_directories
                = data.getAnnotationsDirectories();
        if (!annotations_directories.isEmpty()) {
            offset = roundUp(offset, AnnotationsDirectory.ALIGNMENT);
            map.annotations_directories_off = offset;
            map.annotations_directories_size = 0;
            for (Map.Entry<ClassDef, AnnotationsDirectory> tmp
                    : annotations_directories.entrySet()) {
                AnnotationsDirectory ad = tmp.getValue();
                if (!ad.isEmpty()) {
                    map.annotations_directories_size++;
                    offset = roundUp(offset, AnnotationsDirectory.ALIGNMENT);
                    data_out.position(offset);
                    ad.write(context, data_out);
                    context.addAnnotationsDirectory(tmp.getKey(), offset);
                    offset = (int) data_out.position();
                } else {
                    context.addAnnotationsDirectory(tmp.getKey(), 0);
                }
            }
        }

        ArrayValue[] array_values = data.getArrayValues();
        if (array_values.length != 0) {
            map.encoded_arrays_off = offset;
            map.encoded_arrays_size = array_values.length;
            for (ArrayValue tmp : array_values) {
                data_out.position(offset);
                tmp.writeData(context, data_out);
                context.addArrayValue(tmp, offset);
                offset = (int) data_out.position();
            }
        }

        offset = roundUp(offset, FileMap.MAP_ALIGNMENT);
        data_out.position(offset);
        map.writeMap(data_out);
        offset = (int) data_out.position();

        map.data_size = offset - map.data_off;

        int file_size = offset;

        out.position(map.type_ids_off);
        context.typesStream().forEach((value) -> value.write(context, out));

        out.position(map.field_ids_off);
        context.fieldsStream().forEach((value) -> value.write(context, out));

        out.position(map.proto_ids_off);
        context.protosStream().forEach((value) -> value.write(context, out));

        out.position(map.method_ids_off);
        context.methodsStream().forEach((value) -> value.write(context, out));

        out.position(map.class_defs_off);
        context.classDefsStream().forEach((value) -> value.write(context, out));

        out.position(map.method_handles_off);
        context.methodHandlesStream().forEach((value) -> value.write(context, out));

        map.writeHeader(out, options, file_size);
    }

    public ClassDef findClassDef(TypeId type) {
        for (ClassDef tmp : this) {
            if (tmp.getType().equals(type)) {
                return tmp;
            }
        }
        return null;
    }

    public byte[] compile(DexOptions options) {
        ByteArrayIO out = new ByteArrayIO();
        write(out, options);
        return out.toByteArray();
    }

    public byte[] compile() {
        return compile(DexOptions.defaultOptions());
    }

    @Override
    public Dex clone() {
        Dex out = new Dex();
        out.addAll(this);
        return out;
    }
}
