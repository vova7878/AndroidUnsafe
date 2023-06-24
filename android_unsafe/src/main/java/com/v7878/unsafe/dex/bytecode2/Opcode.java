package com.v7878.unsafe.dex.bytecode2;

import com.v7878.unsafe.dex.DexOptions;

import java.util.function.Function;

public enum Opcode {

    NOP(0x00, "nop", Format.Format10x::new),
    MOVE(0x01, "move", Format.Format12x::new),
    MOVE_FROM16(0x02, "move/from16", Format.Format22x::new),
    MOVE_16(0x03, "move/16", Format.Format32x::new),
    MOVE_WIDE(0x04, "move-wide", Format.Format12x::new),
    MOVE_WIDE_FROM16(0x05, "move-wide/from16", Format.Format22x::new),
    MOVE_WIDE_16(0x06, "move-wide/16", Format.Format32x::new),
    MOVE_OBJECT(0x07, "move-object", Format.Format12x::new),
    MOVE_OBJECT_FROM16(0x08, "move-object/from16", Format.Format22x::new),
    MOVE_OBJECT_16(0x09, "move-object/16", Format.Format32x::new),
    MOVE_RESULT(0x0a, "move-result", Format.Format11x::new),
    MOVE_RESULT_WIDE(0x0b, "move-result-wide", Format.Format11x::new),
    MOVE_RESULT_OBJECT(0x0c, "move-result-object", Format.Format11x::new),
    MOVE_EXCEPTION(0x0d, "move-exception", Format.Format11x::new),
    RETURN_VOID(0x0e, "return-void", Format.Format10x::new),
    RETURN(0x0f, "return", Format.Format11x::new),
    RETURN_WIDE(0x10, "return-wide", Format.Format11x::new),
    RETURN_OBJECT(0x11, "return-object", Format.Format11x::new),
    CONST_4(0x12, "const/4", Format.Format11n::new),
    /*CONST_16(0x13, "const/16", Format.Format21s::new),
    CONST(0x14, "const", Format.Format31i::new),
    CONST_HIGH16(0x15, "const/high16", Format.Format21ih::new),
    CONST_WIDE_16(0x16, "const-wide/16", Format.Format21s::new),
    CONST_WIDE_32(0x17, "const-wide/32", Format.Format31i::new),
    CONST_WIDE(0x18, "const-wide", Format.Format51l::new),
    CONST_WIDE_HIGH16(0x19, "const-wide/high16", Format.Format21lh::new),*/
    CONST_STRING(0x1a, "const-string", opcode -> new Format.Format21c(opcode, ReferenceType.STRING)),
    /*CONST_STRING_JUMBO(0x1b, "const-string/jumbo", ReferenceType.STRING, Format.Format31c::new),*/
    CONST_CLASS(0x1c, "const-class", opcode -> new Format.Format21c(opcode, ReferenceType.TYPE)),
    MONITOR_ENTER(0x1d, "monitor-enter", Format.Format11x::new),
    MONITOR_EXIT(0x1e, "monitor-exit", Format.Format11x::new),
    CHECK_CAST(0x1f, "check-cast", opcode -> new Format.Format21c(opcode, ReferenceType.TYPE)),
    INSTANCE_OF(0x20, "instance-of", opcode -> new Format.Format22c(opcode, ReferenceType.TYPE)),
    ARRAY_LENGTH(0x21, "array-length", Format.Format12x::new),
    NEW_INSTANCE(0x22, "new-instance", opcode -> new Format.Format21c(opcode, ReferenceType.TYPE)),
    NEW_ARRAY(0x23, "new-array", opcode -> new Format.Format22c(opcode, ReferenceType.TYPE)),
    FILLED_NEW_ARRAY(0x24, "filled-new-array", opcode -> new Format.Format35c(opcode, ReferenceType.TYPE)),
    /*FILLED_NEW_ARRAY_RANGE(0x25, "filled-new-array/range", ReferenceType.TYPE, Format.Format3rc, Opcode.CAN_CONTINUE | Opcode.SETS_RESULT),
    FILL_ARRAY_DATA(0x26, "fill-array-data", Format.Format31t, Opcode.CAN_CONTINUE),*/
    THROW(0x27, "throw", Format.Format11x::new),
    GOTO(0x28, "goto", Format.Format10t::new),
    /*GOTO_16(0x29, "goto/16", Format.Format20t),
    GOTO_32(0x2a, "goto/32", Format.Format30t),
    PACKED_SWITCH(0x2b, "packed-switch", Format.Format31t, Opcode.CAN_CONTINUE),
    SPARSE_SWITCH(0x2c, "sparse-switch", Format.Format31t, Opcode.CAN_CONTINUE),*/
    CMPL_FLOAT(0x2d, "cmpl-float", Format.Format23x::new),
    CMPG_FLOAT(0x2e, "cmpg-float", Format.Format23x::new),
    CMPL_DOUBLE(0x2f, "cmpl-double", Format.Format23x::new),
    CMPG_DOUBLE(0x30, "cmpg-double", Format.Format23x::new),
    CMP_LONG(0x31, "cmp-long", Format.Format23x::new),
    /*IF_EQ(0x32, "if-eq", Format.Format22t, Opcode.CAN_CONTINUE),
    IF_NE(0x33, "if-ne", Format.Format22t, Opcode.CAN_CONTINUE),
    IF_LT(0x34, "if-lt", Format.Format22t, Opcode.CAN_CONTINUE),
    IF_GE(0x35, "if-ge", Format.Format22t, Opcode.CAN_CONTINUE),
    IF_GT(0x36, "if-gt", Format.Format22t, Opcode.CAN_CONTINUE),
    IF_LE(0x37, "if-le", Format.Format22t, Opcode.CAN_CONTINUE),
    IF_EQZ(0x38, "if-eqz", Format.Format21t, Opcode.CAN_CONTINUE),
    IF_NEZ(0x39, "if-nez", Format.Format21t, Opcode.CAN_CONTINUE),
    IF_LTZ(0x3a, "if-ltz", Format.Format21t, Opcode.CAN_CONTINUE),
    IF_GEZ(0x3b, "if-gez", Format.Format21t, Opcode.CAN_CONTINUE),
    IF_GTZ(0x3c, "if-gtz", Format.Format21t, Opcode.CAN_CONTINUE),
    IF_LEZ(0x3d, "if-lez", Format.Format21t, Opcode.CAN_CONTINUE),*/
    // 3e - 43 unused
    AGET(0x44, "aget", Format.Format23x::new),
    AGET_WIDE(0x45, "aget-wide", Format.Format23x::new),
    AGET_OBJECT(0x46, "aget-object", Format.Format23x::new),
    AGET_BOOLEAN(0x47, "aget-boolean", Format.Format23x::new),
    AGET_BYTE(0x48, "aget-byte", Format.Format23x::new),
    AGET_CHAR(0x49, "aget-char", Format.Format23x::new),
    AGET_SHORT(0x4a, "aget-short", Format.Format23x::new),
    APUT(0x4b, "aput", Format.Format23x::new),
    APUT_WIDE(0x4c, "aput-wide", Format.Format23x::new),
    APUT_OBJECT(0x4d, "aput-object", Format.Format23x::new),
    APUT_BOOLEAN(0x4e, "aput-boolean", Format.Format23x::new),
    APUT_BYTE(0x4f, "aput-byte", Format.Format23x::new),
    APUT_CHAR(0x50, "aput-char", Format.Format23x::new),
    APUT_SHORT(0x51, "aput-short", Format.Format23x::new),
    IGET(0x52, "iget", opcode -> new Format.Format22c(opcode, ReferenceType.FIELD)),
    IGET_WIDE(0x53, "iget-wide", opcode -> new Format.Format22c(opcode, ReferenceType.FIELD)),
    IGET_OBJECT(0x54, "iget-object", opcode -> new Format.Format22c(opcode, ReferenceType.FIELD)),
    IGET_BOOLEAN(0x55, "iget-boolean", opcode -> new Format.Format22c(opcode, ReferenceType.FIELD)),
    IGET_BYTE(0x56, "iget-byte", opcode -> new Format.Format22c(opcode, ReferenceType.FIELD)),
    IGET_CHAR(0x57, "iget-char", opcode -> new Format.Format22c(opcode, ReferenceType.FIELD)),
    IGET_SHORT(0x58, "iget-short", opcode -> new Format.Format22c(opcode, ReferenceType.FIELD)),
    IPUT(0x59, "iput", opcode -> new Format.Format22c(opcode, ReferenceType.FIELD)),
    IPUT_WIDE(0x5a, "iput-wide", opcode -> new Format.Format22c(opcode, ReferenceType.FIELD)),
    IPUT_OBJECT(0x5b, "iput-object", opcode -> new Format.Format22c(opcode, ReferenceType.FIELD)),
    IPUT_BOOLEAN(0x5c, "iput-boolean", opcode -> new Format.Format22c(opcode, ReferenceType.FIELD)),
    IPUT_BYTE(0x5d, "iput-byte", opcode -> new Format.Format22c(opcode, ReferenceType.FIELD)),
    IPUT_CHAR(0x5e, "iput-char", opcode -> new Format.Format22c(opcode, ReferenceType.FIELD)),
    IPUT_SHORT(0x5f, "iput-short", opcode -> new Format.Format22c(opcode, ReferenceType.FIELD)),
    SGET(0x60, "sget", opcode -> new Format.Format21c(opcode, ReferenceType.FIELD)),
    SGET_WIDE(0x61, "sget-wide", opcode -> new Format.Format21c(opcode, ReferenceType.FIELD)),
    SGET_OBJECT(0x62, "sget-object", opcode -> new Format.Format21c(opcode, ReferenceType.FIELD)),
    SGET_BOOLEAN(0x63, "sget-boolean", opcode -> new Format.Format21c(opcode, ReferenceType.FIELD)),
    SGET_BYTE(0x64, "sget-byte", opcode -> new Format.Format21c(opcode, ReferenceType.FIELD)),
    SGET_CHAR(0x65, "sget-char", opcode -> new Format.Format21c(opcode, ReferenceType.FIELD)),
    SGET_SHORT(0x66, "sget-short", opcode -> new Format.Format21c(opcode, ReferenceType.FIELD)),
    SPUT(0x67, "sput", opcode -> new Format.Format21c(opcode, ReferenceType.FIELD)),
    SPUT_WIDE(0x68, "sput-wide", opcode -> new Format.Format21c(opcode, ReferenceType.FIELD)),
    SPUT_OBJECT(0x69, "sput-object", opcode -> new Format.Format21c(opcode, ReferenceType.FIELD)),
    SPUT_BOOLEAN(0x6a, "sput-boolean", opcode -> new Format.Format21c(opcode, ReferenceType.FIELD)),
    SPUT_BYTE(0x6b, "sput-byte", opcode -> new Format.Format21c(opcode, ReferenceType.FIELD)),
    SPUT_CHAR(0x6c, "sput-char", opcode -> new Format.Format21c(opcode, ReferenceType.FIELD)),
    SPUT_SHORT(0x6d, "sput-short", opcode -> new Format.Format21c(opcode, ReferenceType.FIELD)),
    INVOKE_VIRTUAL(0x6e, "invoke-virtual", opcode -> new Format.Format35c(opcode, ReferenceType.METHOD)),
    INVOKE_SUPER(0x6f, "invoke-super", opcode -> new Format.Format35c(opcode, ReferenceType.METHOD)),
    INVOKE_DIRECT(0x70, "invoke-direct", opcode -> new Format.Format35c(opcode, ReferenceType.METHOD)),
    INVOKE_STATIC(0x71, "invoke-static", opcode -> new Format.Format35c(opcode, ReferenceType.METHOD)),
    INVOKE_INTERFACE(0x72, "invoke-interface", opcode -> new Format.Format35c(opcode, ReferenceType.METHOD)),
    // 73 unused
    /*INVOKE_VIRTUAL_RANGE(0x74, "invoke-virtual/range", ReferenceType.METHOD, Format.Format3rc, Opcode.CAN_CONTINUE | Opcode.SETS_RESULT),
    INVOKE_SUPER_RANGE(0x75, "invoke-super/range", ReferenceType.METHOD, Format.Format3rc, Opcode.CAN_CONTINUE | Opcode.SETS_RESULT),
    INVOKE_DIRECT_RANGE(0x76, "invoke-direct/range", ReferenceType.METHOD, Format.Format3rc, Opcode.CAN_CONTINUE | Opcode.SETS_RESULT | Opcode.CAN_INITIALIZE_REFERENCE),
    INVOKE_STATIC_RANGE(0x77, "invoke-static/range", ReferenceType.METHOD, Format.Format3rc, Opcode.CAN_CONTINUE | Opcode.SETS_RESULT),
    INVOKE_INTERFACE_RANGE(0x78, "invoke-interface/range", ReferenceType.METHOD, Format.Format3rc, Opcode.CAN_CONTINUE | Opcode.SETS_RESULT),*/
    // 79 - 7a unused
    NEG_INT(0x7b, "neg-int", Format.Format12x::new),
    NOT_INT(0x7c, "not-int", Format.Format12x::new),
    NEG_LONG(0x7d, "neg-long", Format.Format12x::new),
    NOT_LONG(0x7e, "not-long", Format.Format12x::new),
    NEG_FLOAT(0x7f, "neg-float", Format.Format12x::new),
    NEG_DOUBLE(0x80, "neg-double", Format.Format12x::new),
    INT_TO_LONG(0x81, "int-to-long", Format.Format12x::new),
    INT_TO_FLOAT(0x82, "int-to-float", Format.Format12x::new),
    INT_TO_DOUBLE(0x83, "int-to-double", Format.Format12x::new),
    LONG_TO_INT(0x84, "long-to-int", Format.Format12x::new),
    LONG_TO_FLOAT(0x85, "long-to-float", Format.Format12x::new),
    LONG_TO_DOUBLE(0x86, "long-to-double", Format.Format12x::new),
    FLOAT_TO_INT(0x87, "float-to-int", Format.Format12x::new),
    FLOAT_TO_LONG(0x88, "float-to-long", Format.Format12x::new),
    FLOAT_TO_DOUBLE(0x89, "float-to-double", Format.Format12x::new),
    DOUBLE_TO_INT(0x8a, "double-to-int", Format.Format12x::new),
    DOUBLE_TO_LONG(0x8b, "double-to-long", Format.Format12x::new),
    DOUBLE_TO_FLOAT(0x8c, "double-to-float", Format.Format12x::new),
    INT_TO_BYTE(0x8d, "int-to-byte", Format.Format12x::new),
    INT_TO_CHAR(0x8e, "int-to-char", Format.Format12x::new),
    INT_TO_SHORT(0x8f, "int-to-short", Format.Format12x::new),
    ADD_INT(0x90, "add-int", Format.Format23x::new),
    SUB_INT(0x91, "sub-int", Format.Format23x::new),
    MUL_INT(0x92, "mul-int", Format.Format23x::new),
    DIV_INT(0x93, "div-int", Format.Format23x::new),
    REM_INT(0x94, "rem-int", Format.Format23x::new),
    AND_INT(0x95, "and-int", Format.Format23x::new),
    OR_INT(0x96, "or-int", Format.Format23x::new),
    XOR_INT(0x97, "xor-int", Format.Format23x::new),
    SHL_INT(0x98, "shl-int", Format.Format23x::new),
    SHR_INT(0x99, "shr-int", Format.Format23x::new),
    USHR_INT(0x9a, "ushr-int", Format.Format23x::new),
    ADD_LONG(0x9b, "add-long", Format.Format23x::new),
    SUB_LONG(0x9c, "sub-long", Format.Format23x::new),
    MUL_LONG(0x9d, "mul-long", Format.Format23x::new),
    DIV_LONG(0x9e, "div-long", Format.Format23x::new),
    REM_LONG(0x9f, "rem-long", Format.Format23x::new),
    AND_LONG(0xa0, "and-long", Format.Format23x::new),
    OR_LONG(0xa1, "or-long", Format.Format23x::new),
    XOR_LONG(0xa2, "xor-long", Format.Format23x::new),
    SHL_LONG(0xa3, "shl-long", Format.Format23x::new),
    SHR_LONG(0xa4, "shr-long", Format.Format23x::new),
    USHR_LONG(0xa5, "ushr-long", Format.Format23x::new),
    ADD_FLOAT(0xa6, "add-float", Format.Format23x::new),
    SUB_FLOAT(0xa7, "sub-float", Format.Format23x::new),
    MUL_FLOAT(0xa8, "mul-float", Format.Format23x::new),
    DIV_FLOAT(0xa9, "div-float", Format.Format23x::new),
    REM_FLOAT(0xaa, "rem-float", Format.Format23x::new),
    ADD_DOUBLE(0xab, "add-double", Format.Format23x::new),
    SUB_DOUBLE(0xac, "sub-double", Format.Format23x::new),
    MUL_DOUBLE(0xad, "mul-double", Format.Format23x::new),
    DIV_DOUBLE(0xae, "div-double", Format.Format23x::new),
    REM_DOUBLE(0xaf, "rem-double", Format.Format23x::new),
    ADD_INT_2ADDR(0xb0, "add-int/2addr", Format.Format12x::new),
    SUB_INT_2ADDR(0xb1, "sub-int/2addr", Format.Format12x::new),
    MUL_INT_2ADDR(0xb2, "mul-int/2addr", Format.Format12x::new),
    DIV_INT_2ADDR(0xb3, "div-int/2addr", Format.Format12x::new),
    REM_INT_2ADDR(0xb4, "rem-int/2addr", Format.Format12x::new),
    AND_INT_2ADDR(0xb5, "and-int/2addr", Format.Format12x::new),
    OR_INT_2ADDR(0xb6, "or-int/2addr", Format.Format12x::new),
    XOR_INT_2ADDR(0xb7, "xor-int/2addr", Format.Format12x::new),
    SHL_INT_2ADDR(0xb8, "shl-int/2addr", Format.Format12x::new),
    SHR_INT_2ADDR(0xb9, "shr-int/2addr", Format.Format12x::new),
    USHR_INT_2ADDR(0xba, "ushr-int/2addr", Format.Format12x::new),
    ADD_LONG_2ADDR(0xbb, "add-long/2addr", Format.Format12x::new),
    SUB_LONG_2ADDR(0xbc, "sub-long/2addr", Format.Format12x::new),
    MUL_LONG_2ADDR(0xbd, "mul-long/2addr", Format.Format12x::new),
    DIV_LONG_2ADDR(0xbe, "div-long/2addr", Format.Format12x::new),
    REM_LONG_2ADDR(0xbf, "rem-long/2addr", Format.Format12x::new),
    AND_LONG_2ADDR(0xc0, "and-long/2addr", Format.Format12x::new),
    OR_LONG_2ADDR(0xc1, "or-long/2addr", Format.Format12x::new),
    XOR_LONG_2ADDR(0xc2, "xor-long/2addr", Format.Format12x::new),
    SHL_LONG_2ADDR(0xc3, "shl-long/2addr", Format.Format12x::new),
    SHR_LONG_2ADDR(0xc4, "shr-long/2addr", Format.Format12x::new),
    USHR_LONG_2ADDR(0xc5, "ushr-long/2addr", Format.Format12x::new),
    ADD_FLOAT_2ADDR(0xc6, "add-float/2addr", Format.Format12x::new),
    SUB_FLOAT_2ADDR(0xc7, "sub-float/2addr", Format.Format12x::new),
    MUL_FLOAT_2ADDR(0xc8, "mul-float/2addr", Format.Format12x::new),
    DIV_FLOAT_2ADDR(0xc9, "div-float/2addr", Format.Format12x::new),
    REM_FLOAT_2ADDR(0xca, "rem-float/2addr", Format.Format12x::new),
    ADD_DOUBLE_2ADDR(0xcb, "add-double/2addr", Format.Format12x::new),
    SUB_DOUBLE_2ADDR(0xcc, "sub-double/2addr", Format.Format12x::new),
    MUL_DOUBLE_2ADDR(0xcd, "mul-double/2addr", Format.Format12x::new),
    DIV_DOUBLE_2ADDR(0xce, "div-double/2addr", Format.Format12x::new),
    REM_DOUBLE_2ADDR(0xcf, "rem-double/2addr", Format.Format12x::new),
    /*ADD_INT_LIT16(0xd0, "add-int/lit16", Format.Format22s::new),
    RSUB_INT(0xd1, "rsub-int", Format.Format22s::new),
    MUL_INT_LIT16(0xd2, "mul-int/lit16", Format.Format22s::new),
    DIV_INT_LIT16(0xd3, "div-int/lit16", Format.Format22s::new),
    REM_INT_LIT16(0xd4, "rem-int/lit16", Format.Format22s::new),
    AND_INT_LIT16(0xd5, "and-int/lit16", Format.Format22s::new),
    OR_INT_LIT16(0xd6, "or-int/lit16", Format.Format22s::new),
    XOR_INT_LIT16(0xd7, "xor-int/lit16", Format.Format22s::new),
    ADD_INT_LIT8(0xd8, "add-int/lit8", Format.Format22b::new),
    RSUB_INT_LIT8(0xd9, "rsub-int/lit8", Format.Format22b::new),
    MUL_INT_LIT8(0xda, "mul-int/lit8", Format.Format22b::new),
    DIV_INT_LIT8(0xdb, "div-int/lit8", Format.Format22b::new),
    REM_INT_LIT8(0xdc, "rem-int/lit8", Format.Format22b::new),
    AND_INT_LIT8(0xdd, "and-int/lit8", Format.Format22b::new),
    OR_INT_LIT8(0xde, "or-int/lit8", Format.Format22b::new),
    XOR_INT_LIT8(0xdf, "xor-int/lit8", Format.Format22b::new),
    SHL_INT_LIT8(0xe0, "shl-int/lit8", Format.Format22b::new),
    SHR_INT_LIT8(0xe1, "shr-int/lit8", Format.Format22b::new),
    USHR_INT_LIT8(0xe2, "ushr-int/lit8", Format.Format22b::new)

    IGET_VOLATILE(firstApi(0xe3, 9), "iget-volatile", ReferenceType.FIELD, Format.Format22c, Opcode.ODEX_ONLY | Opcode.VOLATILE_FIELD_ACCESSOR | Opcode.CAN_THROW | Opcode.CAN_CONTINUE | Opcode.SETS_REGISTER),
    IPUT_VOLATILE(firstApi(0xe4, 9), "iput-volatile", ReferenceType.FIELD, Format.Format22c, Opcode.ODEX_ONLY | Opcode.VOLATILE_FIELD_ACCESSOR | Opcode.CAN_THROW | Opcode.CAN_CONTINUE),
    SGET_VOLATILE(firstApi(0xe5, 9), "sget-volatile", ReferenceType.FIELD, Format.Format21c, Opcode.ODEX_ONLY | Opcode.VOLATILE_FIELD_ACCESSOR | Opcode.CAN_THROW | Opcode.CAN_CONTINUE | Opcode.SETS_REGISTER | Opcode.STATIC_FIELD_ACCESSOR),
    SPUT_VOLATILE(firstApi(0xe6, 9), "sput-volatile", ReferenceType.FIELD, Format.Format21c, Opcode.ODEX_ONLY | Opcode.VOLATILE_FIELD_ACCESSOR | Opcode.CAN_THROW | Opcode.CAN_CONTINUE | Opcode.STATIC_FIELD_ACCESSOR),
    IGET_OBJECT_VOLATILE(firstApi(0xe7, 9), "iget-object-volatile", ReferenceType.FIELD, Format.Format22c, Opcode.ODEX_ONLY | Opcode.VOLATILE_FIELD_ACCESSOR | Opcode.CAN_THROW | Opcode.CAN_CONTINUE | Opcode.SETS_REGISTER),
    IGET_WIDE_VOLATILE(firstApi(0xe8, 9), "iget-wide-volatile", ReferenceType.FIELD, Format.Format22c, Opcode.ODEX_ONLY | Opcode.VOLATILE_FIELD_ACCESSOR | Opcode.CAN_THROW | Opcode.CAN_CONTINUE | Opcode.SETS_REGISTER | Opcode.SETS_WIDE_REGISTER),
    IPUT_WIDE_VOLATILE(firstApi(0xe9, 9), "iput-wide-volatile", ReferenceType.FIELD, Format.Format22c, Opcode.ODEX_ONLY | Opcode.VOLATILE_FIELD_ACCESSOR | Opcode.CAN_THROW | Opcode.CAN_CONTINUE),
    SGET_WIDE_VOLATILE(firstApi(0xea, 9), "sget-wide-volatile", ReferenceType.FIELD, Format.Format21c, Opcode.ODEX_ONLY | Opcode.VOLATILE_FIELD_ACCESSOR | Opcode.CAN_THROW | Opcode.CAN_CONTINUE | Opcode.SETS_REGISTER | Opcode.SETS_WIDE_REGISTER | Opcode.STATIC_FIELD_ACCESSOR),
    SPUT_WIDE_VOLATILE(firstApi(0xeb, 9), "sput-wide-volatile", ReferenceType.FIELD, Format.Format21c, Opcode.ODEX_ONLY | Opcode.VOLATILE_FIELD_ACCESSOR | Opcode.CAN_THROW | Opcode.CAN_CONTINUE | Opcode.STATIC_FIELD_ACCESSOR),
    THROW_VERIFICATION_ERROR(firstApi(0xed, 5), "throw-verification-error", ReferenceType.NONE, Format.Format20bc, Opcode.ODEX_ONLY | Opcode.CAN_THROW),
    EXECUTE_INLINE(allApis(0xee), "execute-inline", ReferenceType.NONE,  Format.Format35mi, Opcode.ODEX_ONLY | Opcode.CAN_THROW | Opcode.CAN_CONTINUE | Opcode.SETS_RESULT),
    EXECUTE_INLINE_RANGE(firstApi(0xef, 8), "execute-inline/range", ReferenceType.NONE,  Format.Format3rmi,  Opcode.ODEX_ONLY | Opcode.CAN_THROW | Opcode.CAN_CONTINUE | Opcode.SETS_RESULT),
    INVOKE_DIRECT_EMPTY(lastApi(0xf0, 13), "invoke-direct-empty", ReferenceType.METHOD,  Format.Format35c, Opcode.ODEX_ONLY | Opcode.CAN_THROW | Opcode.CAN_CONTINUE | Opcode.SETS_RESULT | Opcode.CAN_INITIALIZE_REFERENCE),
    INVOKE_OBJECT_INIT_RANGE(firstApi(0xf0, 14), "invoke-object-init/range", ReferenceType.METHOD,  Format.Format3rc, Opcode.ODEX_ONLY | Opcode.CAN_THROW | Opcode.CAN_CONTINUE | Opcode.SETS_RESULT | Opcode.CAN_INITIALIZE_REFERENCE),
    RETURN_VOID_BARRIER(combine(firstApi(0xf1, 11), lastArtVersion(0x73, 59)), "return-void-barrier", ReferenceType.NONE, Format.Format10x, Opcode.ODEX_ONLY),
    RETURN_VOID_NO_BARRIER(firstArtVersion(0x73, 60), "return-void-no-barrier", ReferenceType.NONE, Format.Format10x, Opcode.ODEX_ONLY),
    IGET_QUICK(combine(allApis(0xf2), allArtVersions(0xe3)), "iget-quick", ReferenceType.NONE,  Format.Format22cs, Opcode.ODEX_ONLY | Opcode.QUICK_FIELD_ACCESSOR | Opcode.CAN_THROW | Opcode.CAN_CONTINUE | Opcode.SETS_REGISTER),
    IGET_WIDE_QUICK(combine(allApis(0xf3), allArtVersions(0xe4)), "iget-wide-quick", ReferenceType.NONE,  Format.Format22cs, Opcode.ODEX_ONLY | Opcode.QUICK_FIELD_ACCESSOR | Opcode.CAN_THROW | Opcode.CAN_CONTINUE | Opcode.SETS_REGISTER | Opcode.SETS_WIDE_REGISTER),
    IGET_OBJECT_QUICK(combine(allApis(0xf4), allArtVersions(0xe5)), "iget-object-quick", ReferenceType.NONE,  Format.Format22cs, Opcode.ODEX_ONLY | Opcode.QUICK_FIELD_ACCESSOR | Opcode.CAN_THROW | Opcode.CAN_CONTINUE | Opcode.SETS_REGISTER),
    IPUT_QUICK(combine(allApis(0xf5), allArtVersions(0xe6)), "iput-quick", ReferenceType.NONE,  Format.Format22cs, Opcode.ODEX_ONLY | Opcode.QUICK_FIELD_ACCESSOR | Opcode.CAN_THROW | Opcode.CAN_CONTINUE),
    IPUT_WIDE_QUICK(combine(allApis(0xf6), allArtVersions(0xe7)), "iput-wide-quick", ReferenceType.NONE,  Format.Format22cs, Opcode.ODEX_ONLY | Opcode.QUICK_FIELD_ACCESSOR | Opcode.CAN_THROW | Opcode.CAN_CONTINUE),
    IPUT_OBJECT_QUICK(combine(allApis(0xf7), allArtVersions(0xe8)), "iput-object-quick", ReferenceType.NONE,  Format.Format22cs, Opcode.ODEX_ONLY | Opcode.QUICK_FIELD_ACCESSOR | Opcode.CAN_THROW | Opcode.CAN_CONTINUE),
    IPUT_BOOLEAN_QUICK(allArtVersions(0xeb), "iput-boolean-quick", ReferenceType.NONE, Format.Format22cs, Opcode.ODEX_ONLY | Opcode.CAN_THROW | Opcode.CAN_CONTINUE | Opcode.QUICK_FIELD_ACCESSOR),
    IPUT_BYTE_QUICK(allArtVersions(0xec), "iput-byte-quick", ReferenceType.NONE, Format.Format22cs, Opcode.ODEX_ONLY | Opcode.CAN_THROW | Opcode.CAN_CONTINUE | Opcode.QUICK_FIELD_ACCESSOR),
    IPUT_CHAR_QUICK(allArtVersions(0xed), "iput-char-quick", ReferenceType.NONE, Format.Format22cs, Opcode.ODEX_ONLY | Opcode.CAN_THROW | Opcode.CAN_CONTINUE | Opcode.QUICK_FIELD_ACCESSOR),
    IPUT_SHORT_QUICK(allArtVersions(0xee), "iput-short-quick", ReferenceType.NONE, Format.Format22cs, Opcode.ODEX_ONLY | Opcode.CAN_THROW | Opcode.CAN_CONTINUE | Opcode.QUICK_FIELD_ACCESSOR),
    IGET_BOOLEAN_QUICK(allArtVersions(0xef), "iget-boolean-quick", ReferenceType.NONE, Format.Format22cs, Opcode.ODEX_ONLY | Opcode.QUICK_FIELD_ACCESSOR | Opcode.CAN_THROW | Opcode.CAN_CONTINUE | Opcode.SETS_REGISTER),
    IGET_BYTE_QUICK(allArtVersions(0xf0), "iget-byte-quick", ReferenceType.NONE, Format.Format22cs, Opcode.ODEX_ONLY | Opcode.QUICK_FIELD_ACCESSOR | Opcode.CAN_THROW | Opcode.CAN_CONTINUE | Opcode.SETS_REGISTER),
    IGET_CHAR_QUICK(allArtVersions(0xf1), "iget-char-quick", ReferenceType.NONE, Format.Format22cs, Opcode.ODEX_ONLY | Opcode.QUICK_FIELD_ACCESSOR | Opcode.CAN_THROW | Opcode.CAN_CONTINUE | Opcode.SETS_REGISTER),
    IGET_SHORT_QUICK(allArtVersions(0xf2), "iget-short-quick", ReferenceType.NONE, Format.Format22cs, Opcode.ODEX_ONLY | Opcode.QUICK_FIELD_ACCESSOR | Opcode.CAN_THROW | Opcode.CAN_CONTINUE | Opcode.SETS_REGISTER),
    INVOKE_VIRTUAL_QUICK(combine(allApis(0xf8), allArtVersions(0xe9)), "invoke-virtual-quick", ReferenceType.NONE,  Format.Format35ms, Opcode.ODEX_ONLY | Opcode.CAN_THROW | Opcode.CAN_CONTINUE | Opcode.SETS_RESULT),
    INVOKE_VIRTUAL_QUICK_RANGE(combine(allApis(0xf9), allArtVersions(0xea)), "invoke-virtual-quick/range", ReferenceType.NONE,  Format.Format3rms, Opcode.ODEX_ONLY | Opcode.CAN_THROW | Opcode.CAN_CONTINUE | Opcode.SETS_RESULT),
    INVOKE_SUPER_QUICK(lastApi(0xfa, 25), "invoke-super-quick", ReferenceType.NONE,  Format.Format35ms, Opcode.ODEX_ONLY | Opcode.CAN_THROW | Opcode.CAN_CONTINUE | Opcode.SETS_RESULT),
    INVOKE_SUPER_QUICK_RANGE(lastApi(0xfb, 25), "invoke-super-quick/range", ReferenceType.NONE,  Format.Format3rms, Opcode.ODEX_ONLY | Opcode.CAN_THROW | Opcode.CAN_CONTINUE | Opcode.SETS_RESULT),
    IPUT_OBJECT_VOLATILE(firstApi(0xfc, 9), "iput-object-volatile", ReferenceType.FIELD, Format.Format22c, Opcode.ODEX_ONLY | Opcode.VOLATILE_FIELD_ACCESSOR | Opcode.CAN_THROW | Opcode.CAN_CONTINUE),
    SGET_OBJECT_VOLATILE(firstApi(0xfd, 9), "sget-object-volatile", ReferenceType.FIELD, Format.Format21c, Opcode.ODEX_ONLY | Opcode.VOLATILE_FIELD_ACCESSOR | Opcode.CAN_THROW | Opcode.CAN_CONTINUE | Opcode.SETS_REGISTER | Opcode.STATIC_FIELD_ACCESSOR),
    SPUT_OBJECT_VOLATILE(betweenApi(0xfe, 9, 19), "sput-object-volatile", ReferenceType.FIELD, Format.Format21c, Opcode.ODEX_ONLY | Opcode.VOLATILE_FIELD_ACCESSOR | Opcode.CAN_THROW | Opcode.CAN_CONTINUE | Opcode.STATIC_FIELD_ACCESSOR),*/

    INVOKE_POLYMORPHIC(firstApi(0xfa, 26), "invoke-polymorphic", opcode -> new Format.Format45cc(opcode, ReferenceType.METHOD, ReferenceType.PROTO)),
    /*INVOKE_POLYMORPHIC_RANGE(firstArtVersion(0xfb, 87), "invoke-polymorphic/range", ReferenceType.METHOD, ReferenceType.METHOD_PROTO, Format.Format4rcc, Opcode.CAN_THROW | Opcode.CAN_CONTINUE | Opcode.SETS_RESULT),*/
    /*INVOKE_CUSTOM(firstArtVersion(0xfc, 111), "invoke-custom", ReferenceType.CALL_SITE, Format.Format35c, Opcode.CAN_THROW | Opcode.CAN_CONTINUE | Opcode.SETS_RESULT),*/
    /*INVOKE_CUSTOM_RANGE(firstArtVersion(0xfd, 111), "invoke-custom/range", ReferenceType.CALL_SITE, Format.Format3rc, Opcode.CAN_THROW | Opcode.CAN_CONTINUE | Opcode.SETS_RESULT),*/
    CONST_METHOD_HANDLE(firstApi(0xfe, 28), "const-method-handle", opcode -> new Format.Format21c(opcode, ReferenceType.METHOD_HANDLE)),
    CONST_METHOD_TYPE(firstApi(0xff, 28), "const-method-type", opcode -> new Format.Format21c(opcode, ReferenceType.PROTO)),

    /*PACKED_SWITCH_PAYLOAD(0x100, "packed-switch-payload", ReferenceType.NONE, Format.PackedSwitchPayload, 0),
    SPARSE_SWITCH_PAYLOAD(0x200, "sparse-switch-payload", ReferenceType.NONE, Format.SparseSwitchPayload, 0),
    ARRAY_PAYLOAD(0x300, "array-payload", ReferenceType.NONE, Format.ArrayPayload, 0)*/;
    private static final int ODEX_ONLY = 0x1;

    @FunctionalInterface
    public interface VersionConstraints {
        int opcodeValue(DexOptions options);
    }

    private final VersionConstraints constraints;
    private final String name;
    private final int flags;
    private final Format format;

    public String opname() {
        return name;
    }

    public int opcodeValue(DexOptions options) {
        return constraints.opcodeValue(options);
    }

    public <T extends Format> T format() {
        //noinspection unchecked
        return (T) format;
    }

    public boolean odexOnly() {
        return (flags & ODEX_ONLY) != 0;
    }

    Opcode(int opcodeValue, String name, Function<Opcode, Format> format) {
        this(allApis(opcodeValue), name, format, 0);
    }

    Opcode(VersionConstraints constraints, String name, Function<Opcode, Format> format) {
        this(constraints, name, format, 0);
    }

    Opcode(VersionConstraints constraints, String name, Function<Opcode, Format> format, int flags) {
        this.constraints = constraints;
        this.name = name;
        this.format = format.apply(this);
        this.flags = flags;
    }

    private static VersionConstraints allApis(int opcodeValue) {
        return options -> opcodeValue;
    }

    private static VersionConstraints firstApi(int opcodeValue, int api) {
        return options -> {
            options.requireMinApi(api);
            return opcodeValue;
        };
    }

    private static VersionConstraints lastApi(int opcodeValue, int api) {
        return options -> {
            options.requireMaxApi(api);
            return opcodeValue;
        };
    }

    private static VersionConstraints betweenApi(int opcodeValue, int minApi, int maxApi) {
        return options -> {
            options.requireMinApi(minApi);
            options.requireMaxApi(maxApi);
            return opcodeValue;
        };
    }

    private static VersionConstraints onlyOdex(VersionConstraints constraints) {
        return options -> {
            options.requireOdexInstructions();
            return constraints.opcodeValue(options);
        };
    }
}
