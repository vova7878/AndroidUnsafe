package com.v7878.unsafe.bytecode;

import com.v7878.unsafe.io.*;
import java.util.Arrays;

public class Dex {

    public ClassDef[] class_defs;

    public static Dex read(RandomInput in) {
        Dex out = new Dex();
        FileMap map = FileMap.read(in);
        ReadContext rc = new ReadContext();
        rc.strings = new StringId[map.string_ids_size];
        if (map.string_ids_size != 0) {
            RandomInput in2 = in.duplicate(map.string_ids_off);
            for (int i = 0; i < map.string_ids_size; i++) {
                rc.strings[i] = StringId.read(in2);
            }
        }
        System.out.println("strings " + Arrays.toString(rc.strings));
        rc.types = new TypeId[map.type_ids_size];
        if (map.type_ids_size != 0) {
            RandomInput in2 = in.duplicate(map.type_ids_off);
            for (int i = 0; i < map.type_ids_size; i++) {
                rc.types[i] = TypeId.read(in2, rc);
            }
        }
        System.out.println("types " + Arrays.toString(rc.types));
        rc.protos = new ProtoId[map.proto_ids_size];
        if (map.proto_ids_size != 0) {
            RandomInput in2 = in.duplicate(map.proto_ids_off);
            for (int i = 0; i < map.proto_ids_size; i++) {
                rc.protos[i] = ProtoId.read(in2, rc);
            }
        }
        System.out.println("protos " + Arrays.toString(rc.protos));
        rc.fields = new FieldId[map.field_ids_size];
        if (map.field_ids_size != 0) {
            RandomInput in2 = in.duplicate(map.field_ids_off);
            for (int i = 0; i < map.field_ids_size; i++) {
                rc.fields[i] = FieldId.read(in2, rc);
            }
        }
        System.out.println("fields " + Arrays.toString(rc.fields));
        rc.methods = new MethodId[map.method_ids_size];
        if (map.method_ids_size != 0) {
            RandomInput in2 = in.duplicate(map.method_ids_off);
            for (int i = 0; i < map.method_ids_size; i++) {
                rc.methods[i] = MethodId.read(in2, rc);
            }
        }
        System.out.println("methods " + Arrays.toString(rc.methods));
        rc.method_handles = new MethodHandleItem[map.method_handles_size];
        if (map.method_handles_size != 0) {
            RandomInput in2 = in.duplicate(map.method_handles_off);
            for (int i = 0; i < map.method_handles_size; i++) {
                rc.method_handles[i] = MethodHandleItem.read(in2, rc);
            }
        }
        System.out.println("method_handles " + Arrays.toString(rc.method_handles));
        rc.call_sites = new CallSiteId[map.call_site_ids_size];
        if (map.call_site_ids_size != 0) {
            RandomInput in2 = in.duplicate(map.call_site_ids_off);
            for (int i = 0; i < map.call_site_ids_size; i++) {
                rc.call_sites[i] = CallSiteId.read(in2, rc);
            }
        }
        System.out.println("call_sites " + Arrays.toString(rc.call_sites));
        out.class_defs = new ClassDef[map.class_defs_size];
        if (map.class_defs_size != 0) {
            RandomInput in2 = in.duplicate(map.class_defs_off);
            for (int i = 0; i < map.class_defs_size; i++) {
                out.class_defs[i] = ClassDef.read(in2, rc);
            }
        }
        System.out.println("class_defs " + Arrays.toString(out.class_defs));
        return out;
    }
}
