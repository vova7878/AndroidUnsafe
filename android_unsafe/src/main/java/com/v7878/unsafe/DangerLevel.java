package com.v7878.unsafe;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value = RetentionPolicy.CLASS)
@Target(value = {ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR})
public @interface DangerLevel {

    int VERY_CAREFUL = Integer.MAX_VALUE / 2;
    int POTENTIAL_GC_COLLISION = Integer.MAX_VALUE / 2 + 1;
    int GC_COLLISION_MOVABLE_OBJECTS = Integer.MAX_VALUE / 2 + 2;
    int BAD_GC_COLLISION = Integer.MAX_VALUE / 2 + 3;
    int MAX = Integer.MAX_VALUE;

    int value();
}
