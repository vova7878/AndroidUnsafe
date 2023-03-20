package com.v7878.unsafe.bytecode;

import static com.v7878.unsafe.Utils.*;
import com.v7878.unsafe.io.*;
import java.util.*;

public class Dex extends AbstractList<ClassDef> implements Cloneable {

    private final List<ClassDef> class_defs;

    public Dex(ClassDef... class_defs) {
        if (class_defs == null) {
            class_defs = new ClassDef[0];
        }
        this.class_defs = new ArrayList<>(class_defs.length);
        addAll(Arrays.asList(class_defs));
    }

    private ClassDef check(ClassDef class_def) {
        return Objects.requireNonNull(class_def,
                "Dex can`t contain null class def");
    }

    @Override
    public final void add(int index, ClassDef class_def) {
        class_defs.add(check(class_def).clone());
    }

    @Override
    public final ClassDef set(int index, ClassDef class_def) {
        return class_defs.set(index, check(class_def).clone());
    }

    @Override
    public final ClassDef get(int index) {
        return class_defs.get(index);
    }

    @Override
    public final ClassDef remove(int index) {
        return class_defs.remove(index);
    }

    @Override
    public final int size() {
        return class_defs.size();
    }

    public static Dex read(RandomInput in) {
        FileMap map = FileMap.read(in);
        ReadContextImpl context = new ReadContextImpl();
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
        ClassDef[] class_defs = new ClassDef[map.class_defs_size];
        if (map.class_defs_size != 0) {
            RandomInput in2 = in.duplicate(map.class_defs_off);
            for (int i = 0; i < map.class_defs_size; i++) {
                class_defs[i] = ClassDef.read(in2, context);
            }
        }
        return new Dex(class_defs);
    }

    public void fillContext(DataSet data) {
        for (ClassDef tmp : class_defs) {
            data.addClassDef(tmp);
        }
    }

    public void write(RandomIO out) {
        assert_(out.position() == 0, IllegalArgumentException::new);

        DataSet data = new DataSet();
        fillContext(data);

        WriteContextImpl context = new WriteContextImpl(data);

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
        context.stringsStream().forEach((value) -> {
            StringId.write(value, context, out, data_out);
        });
        offset = (int) data_out.position();

        TypeList[] lists = data.getTypeLists();
        if (lists.length != 0) {
            Arrays.sort(lists, context.type_list_comparator);
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

        //TODO: class data
        offset = roundUp(offset, FileMap.MAP_ALIGNMENT);
        data_out.position(offset);
        map.writeMap(data_out);
        offset = (int) data_out.position();

        map.data_size = offset - map.data_off;

        int file_size = offset;

        out.position(map.type_ids_off);
        context.typesStream().forEach((value) -> {
            value.write(context, out);
        });

        out.position(map.field_ids_off);
        context.fieldsStream().forEach((value) -> {
            value.write(context, out);
        });

        out.position(map.proto_ids_off);
        context.protosStream().forEach((value) -> {
            value.write(context, out);
        });

        out.position(map.method_ids_off);
        context.methodsStream().forEach((value) -> {
            value.write(context, out);
        });

        out.position(map.class_defs_off);
        context.classDefsStream().forEach((value) -> {
            value.write(context, out);
        });

        out.position(map.method_handles_off);
        context.methodHandlesStream().forEach((value) -> {
            value.write(context, out);
        });

        map.writeHeader(out, file_size);
    }

    public byte[] compile() {
        ByteArrayIO out = new ByteArrayIO();
        write(out);
        return out.toByteArray();
    }

    @Override
    public Dex clone() {
        Dex out = new Dex();
        out.addAll(class_defs);
        return out;
    }
}
