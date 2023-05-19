package com.v7878.unsafe.dex;

public class DexConstants {

    public static final int ENDIAN_CONSTANT = 0x12345678;
    public static final int NO_INDEX = -1;

    //encoded_value
    public static final int VALUE_BYTE = 0x00;
    public static final int VALUE_SHORT = 0x02;
    public static final int VALUE_CHAR = 0x03;
    public static final int VALUE_INT = 0x04;
    public static final int VALUE_LONG = 0x06;
    public static final int VALUE_FLOAT = 0x10;
    public static final int VALUE_DOUBLE = 0x11;
    public static final int VALUE_METHOD_TYPE = 0x15;
    public static final int VALUE_METHOD_HANDLE = 0x16;
    public static final int VALUE_STRING = 0x17;
    public static final int VALUE_TYPE = 0x18;
    public static final int VALUE_FIELD = 0x19;
    public static final int VALUE_METHOD = 0x1a;
    public static final int VALUE_ENUM = 0x1b;
    public static final int VALUE_ARRAY = 0x1c;
    public static final int VALUE_ANNOTATION = 0x1d;
    public static final int VALUE_NULL = 0x1e;
    public static final int VALUE_BOOLEAN = 0x1f;

    //method_handle_item
    public static final int METHOD_HANDLE_TYPE_STATIC_PUT = 0x00;
    public static final int METHOD_HANDLE_TYPE_STATIC_GET = 0x01;
    public static final int METHOD_HANDLE_TYPE_INSTANCE_PUT = 0x02;
    public static final int METHOD_HANDLE_TYPE_INSTANCE_GET = 0x03;
    public static final int METHOD_HANDLE_TYPE_INVOKE_STATIC = 0x04;
    public static final int METHOD_HANDLE_TYPE_INVOKE_INSTANCE = 0x05;
    public static final int METHOD_HANDLE_TYPE_INVOKE_CONSTRUCTOR = 0x06;
    public static final int METHOD_HANDLE_TYPE_INVOKE_DIRECT = 0x07;
    public static final int METHOD_HANDLE_TYPE_INVOKE_INTERFACE = 0x08;

    public static final int METHOD_HANDLE_TYPE_MIN = 0x00;
    public static final int METHOD_HANDLE_TYPE_MAX = 0x08;

    //map_list
    public static final int TYPE_HEADER_ITEM = 0x0000;
    public static final int TYPE_STRING_ID_ITEM = 0x0001;
    public static final int TYPE_TYPE_ID_ITEM = 0x0002;
    public static final int TYPE_PROTO_ID_ITEM = 0x0003;
    public static final int TYPE_FIELD_ID_ITEM = 0x0004;
    public static final int TYPE_METHOD_ID_ITEM = 0x0005;
    public static final int TYPE_CLASS_DEF_ITEM = 0x0006;
    //TODO
    public static final int TYPE_CALL_SITE_ID_ITEM = 0x0007;
    public static final int TYPE_METHOD_HANDLE_ITEM = 0x0008;
    public static final int TYPE_MAP_LIST = 0x1000;
    public static final int TYPE_TYPE_LIST = 0x1001;
    public static final int TYPE_ANNOTATION_SET_REF_LIST = 0x1002;
    public static final int TYPE_ANNOTATION_SET_ITEM = 0x1003;
    public static final int TYPE_CLASS_DATA_ITEM = 0x2000;
    public static final int TYPE_CODE_ITEM = 0x2001;
    public static final int TYPE_STRING_DATA_ITEM = 0x2002;
    //TODO
    public static final int TYPE_DEBUG_INFO_ITEM = 0x2003;
    public static final int TYPE_ANNOTATION_ITEM = 0x2004;
    public static final int TYPE_ENCODED_ARRAY_ITEM = 0x2005;
    public static final int TYPE_ANNOTATIONS_DIRECTORY_ITEM = 0x2006;
    //TODO?
    public static final int TYPE_HIDDENAPI_CLASS_DATA_ITEM = 0xF000;

    //annotation_item
    public static final int VISIBILITY_BUILD = 0x00;
    public static final int VISIBILITY_RUNTIME = 0x01;
    public static final int VISIBILITY_SYSTEM = 0x02;

    public static final int VISIBILITY_MIN = 0x00;
    public static final int VISIBILITY_MAX = 0x02;
}
