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

    //extra data for write
    public int type_lists_size;
    public int type_lists_off;
    public int annotation_set_refs_size;
    public int annotation_set_refs_off;
    public int annotation_sets_size;
    public int annotation_sets_off;
    public int class_data_items_size;
    public int class_data_items_off;
    public int code_items_size;
    public int code_items_off;
    public int string_data_items_size;
    public int string_data_items_off;
    public int debug_info_items_size;
    public int debug_info_items_off;
    public int annotations_size;
    public int annotations_off;
    public int encoded_arrays_size;
    public int encoded_arrays_off;
    public int annotations_directories_size;
    public int annotations_directories_off;
    public int hiddenapi_class_data_items_size;
    public int hiddenapi_class_data_items_off;

    public static FileMap read(RandomInput in) {
        FileMap out = new FileMap();
        String magic = new String(in.readByteArray(8));
        assert_(magic.startsWith("dex\n"),
                IllegalArgumentException::new, "invalid magic: " + magic);
        System.out.println(magic);
        in.skipBytes(4); //checksum
        in.skipBytes(20); //signature
        in.skipBytes(4); //file_size
        int header_size = in.readInt();
        assert_(header_size == HEADER_SIZE, IllegalArgumentException::new,
                "invalid header size: " + header_size);
        int endian_tag = in.readInt();
        assert_(endian_tag == ENDIAN_CONSTANT,
                IllegalArgumentException::new,
                "invalid endian_tag: " + Integer.toHexString(endian_tag));
        in.skipBytes(4); //link_size
        in.skipBytes(4); //link_off
        int map_list_off = in.readInt();
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
        RandomInput in2 = in.duplicate(map_list_off);
        int map_size = in2.readInt();
        for (int i = 0; i < map_size; i++) {
            int type = in2.readUnsignedShort();
            in2.skipBytes(2); //unused
            int size = in2.readInt();
            assert_(size > 0, IllegalStateException::new);
            int offset = in2.readInt();
            System.out.println("type: " + Integer.toHexString(type)
                    + " off: " + offset + " size: " + size);
            switch (type) {
                case TYPE_HEADER_ITEM:
                    assert_(size == 1, IllegalStateException::new);
                    assert_(offset == 0, IllegalStateException::new);
                    break;
                case TYPE_STRING_ID_ITEM:
                    assert_(size == out.string_ids_size, IllegalStateException::new);
                    assert_(offset == out.string_ids_off, IllegalStateException::new);
                    break;
                case TYPE_TYPE_ID_ITEM:
                    assert_(size == out.type_ids_size, IllegalStateException::new);
                    assert_(offset == out.type_ids_off, IllegalStateException::new);
                    break;
                case TYPE_PROTO_ID_ITEM:
                    assert_(size == out.proto_ids_size, IllegalStateException::new);
                    assert_(offset == out.proto_ids_off, IllegalStateException::new);
                    break;
                case TYPE_FIELD_ID_ITEM:
                    assert_(size == out.field_ids_size, IllegalStateException::new);
                    assert_(offset == out.field_ids_off, IllegalStateException::new);
                    break;
                case TYPE_METHOD_ID_ITEM:
                    assert_(size == out.method_ids_size, IllegalStateException::new);
                    assert_(offset == out.method_ids_off, IllegalStateException::new);
                    break;
                case TYPE_CLASS_DEF_ITEM:
                    assert_(size == out.class_defs_size, IllegalStateException::new);
                    assert_(offset == out.class_defs_off, IllegalStateException::new);
                    break;
                case TYPE_CALL_SITE_ID_ITEM:
                    out.call_site_ids_size = size;
                    out.call_site_ids_off = offset;
                    break;
                case TYPE_METHOD_HANDLE_ITEM:
                    out.method_handles_size = size;
                    out.method_handles_off = offset;
                    break;
                case TYPE_MAP_LIST:
                    assert_(size == 1, IllegalStateException::new);
                    break;
                case TYPE_TYPE_LIST:
                case TYPE_ANNOTATION_SET_REF_LIST:
                case TYPE_ANNOTATION_SET_ITEM:
                case TYPE_CLASS_DATA_ITEM:
                case TYPE_CODE_ITEM:
                case TYPE_STRING_DATA_ITEM:
                case TYPE_DEBUG_INFO_ITEM:
                case TYPE_ANNOTATION_ITEM:
                case TYPE_ENCODED_ARRAY_ITEM:
                case TYPE_ANNOTATIONS_DIRECTORY_ITEM:
                case TYPE_HIDDENAPI_CLASS_DATA_ITEM:
                    break; // ok
                default:
                    throw new IllegalStateException("unknown map_item type: " + type);
            }
        }
        return out;
    }
}
