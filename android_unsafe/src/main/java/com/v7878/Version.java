package com.v7878;

import android.os.Build;

public class Version {
    public static final boolean PREVIEW_SDK_BOOL = Build.VERSION.PREVIEW_SDK_INT != 0;
    public static final int CORRECT_SDK_INT = Build.VERSION.SDK_INT + (PREVIEW_SDK_BOOL ? 1 : 0);
}
