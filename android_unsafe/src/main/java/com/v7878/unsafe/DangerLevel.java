package com.v7878.unsafe;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value = RetentionPolicy.CLASS)
@Target(value = {ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR})
public @interface DangerLevel {

    public static final int VERY_CAREFUL = Integer.MAX_VALUE / 2;
    public static final int MAX = Integer.MAX_VALUE;

    public int value();
}
