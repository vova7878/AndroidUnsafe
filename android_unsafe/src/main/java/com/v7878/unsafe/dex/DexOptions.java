package com.v7878.unsafe.dex;

import static com.v7878.unsafe.Utils.getSdkInt;

//TODO: split into Read and Write Options
public class DexOptions {
    private final int targetApi;
    private final boolean includesOdexInstructions;

    public DexOptions(int targetApi, boolean includesOdexInstructions) {
        this.targetApi = targetApi;
        this.includesOdexInstructions = includesOdexInstructions;
    }

    public int getTargetApi() {
        return targetApi;
    }

    public boolean includesOdexInstructions() {
        return includesOdexInstructions;
    }

    public static DexOptions defaultOptions() {
        return new DexOptions(getSdkInt(), false);
    }

    public void requireMinApi(int minApi) {
        if (targetApi < minApi) {
            //TODO: message
            throw new IllegalArgumentException();
        }
    }

    public void requireMaxApi(int maxApi) {
        if (targetApi > maxApi) {
            //TODO: message
            throw new IllegalArgumentException();
        }
    }

    public void requireOdexInstructions() {
        if (!includesOdexInstructions) {
            //TODO: message
            throw new IllegalArgumentException();
        }
    }
}
