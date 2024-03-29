package com.v7878.unsafe.memory;

import static com.v7878.unsafe.AndroidUnsafe.IS64BIT;
import static com.v7878.unsafe.memory.ValueLayout.ADDRESS;
import static com.v7878.unsafe.memory.ValueLayout.JAVA_BOOLEAN;
import static com.v7878.unsafe.memory.ValueLayout.JAVA_BYTE;
import static com.v7878.unsafe.memory.ValueLayout.JAVA_DOUBLE;
import static com.v7878.unsafe.memory.ValueLayout.JAVA_FLOAT;
import static com.v7878.unsafe.memory.ValueLayout.JAVA_INT;
import static com.v7878.unsafe.memory.ValueLayout.JAVA_LONG;
import static com.v7878.unsafe.memory.ValueLayout.JAVA_SHORT;
import static com.v7878.unsafe.memory.ValueLayout.WORD;

//TODO: is it really that simple? need to check later
public class PlatformLayouts {

    public static final ValueLayout C_BOOL = JAVA_BOOLEAN;

    public static final ValueLayout BOOL32 = JAVA_INT;

    public static final ValueLayout C_CHAR = JAVA_BYTE;

    public static final ValueLayout C_SHORT = JAVA_SHORT;

    public static final ValueLayout C_INT = JAVA_INT;

    public static final ValueLayout C_LONG = IS64BIT ? JAVA_LONG : JAVA_INT;

    public static final ValueLayout C_LONG_LONG = JAVA_LONG;

    public static final ValueLayout C_INT_PTR = WORD;

    public static final ValueLayout C_FLOAT = JAVA_FLOAT;

    public static final ValueLayout C_DOUBLE = JAVA_DOUBLE;

    public static final ValueLayout C_POINTER = ADDRESS;

    public static final ValueLayout C_VA_LIST = C_POINTER;
}
