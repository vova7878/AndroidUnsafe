package com.v7878.unsafe.bytecode;

import com.v7878.unsafe.io.*;
import java.util.Arrays;

public class Dex {

    public ClassDef[] class_defs;

    public static Dex read(RandomInput in) {
        Dex out = new Dex();
        FileMap map = FileMap.read(in);
        Context context = new Context();
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
        out.class_defs = new ClassDef[map.class_defs_size];
        if (map.class_defs_size != 0) {
            RandomInput in2 = in.duplicate(map.class_defs_off);
            for (int i = 0; i < map.class_defs_size; i++) {
                out.class_defs[i] = ClassDef.read(in2, context);
            }
        }
        System.out.println("class_defs " + Arrays.toString(out.class_defs));
        return out;
    }
}
