package com.v7878.unsafe.bytecode;

import static com.v7878.unsafe.Utils.*;
import static com.v7878.unsafe.bytecode.DexConstants.*;
import com.v7878.unsafe.io.RandomInput;

public class FileMap {

    public int string_ids_size;
    public int string_ids_off;
    public int type_ids_size;
    public int type_ids_off;
    public int proto_ids_size;
    public int proto_ids_off;
    public int field_ids_size;
    public int field_ids_off;
    public int method_ids_size;
    public int method_ids_off;
    public int class_defs_size;
    public int class_defs_off;
    public int call_site_ids_size;
    public int call_site_ids_off;
    public int method_handles_size;
    public int method_handles_off;

    public static FileMap read(RandomInput in) {
        FileMap out = new FileMap();
        String magic = new String(in.readByteArray(8));
        assert_(magic.startsWith("dex\n"), IllegalArgumentException::new, "invalid magic: " + magic);
        System.out.println(magic);
        in.skipBytes(4); //checksum
        in.skipBytes(20); //signature
        in.skipBytes(4); //file_size
        int header_size = in.readInt();
        assert_(header_size == 0x70, IllegalArgumentException::new, "invalid header size: " + header_size);
        assert_(in.readInt() == ENDIAN_CONSTANT, IllegalArgumentException::new); //endian_tag
        in.skipBytes(4); //link_size
        in.skipBytes(4); //link_off
        int map_off = in.readInt();
        out.string_ids_size = in.readInt();
        out.string_ids_off = in.readInt();
        out.type_ids_size = in.readInt();
        out.type_ids_off = in.readInt();
        out.proto_ids_size = in.readInt();
        out.proto_ids_off = in.readInt();
        out.field_ids_size = in.readInt();
        out.field_ids_off = in.readInt();
        out.method_ids_size = in.readInt();
        out.method_ids_off = in.readInt();
        out.class_defs_size = in.readInt();
        out.class_defs_off = in.readInt();
        in.skipBytes(4); //data_size
        in.skipBytes(4); //data_off
        RandomInput in2 = in.duplicate(map_off);
        int map_size = in2.readInt();
        for (int i = 0; i < map_size; i++) {
            int type = in2.readUnsignedShort();
            in2.skipBytes(2); //unused
            int size = in2.readInt();
            int offset = in2.readInt();
            //TODO
            System.out.println("type: " + Integer.toHexString(type)
                    + " off: " + offset + " size: " + size);
            if (type == TYPE_CALL_SITE_ID_ITEM) {
                out.call_site_ids_size = size;
                out.call_site_ids_off = offset;
            } else if (type == TYPE_METHOD_HANDLE_ITEM) {
                out.method_handles_size = size;
                out.method_handles_off = offset;
            }
        }
        return out;
    }
}
