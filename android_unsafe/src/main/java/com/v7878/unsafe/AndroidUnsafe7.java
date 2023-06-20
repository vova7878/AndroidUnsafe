package com.v7878.unsafe;

import static com.v7878.unsafe.Utils.getSdkInt;

@DangerLevel(7)
public class AndroidUnsafe7 extends AndroidUnsafe6 {
    public enum ClassStatus {
        NotReady,  // Zero-initialized Class object starts in this state.
        Retired,  // Retired, should not be used. Use the newly cloned one instead.
        ErrorResolved,
        ErrorUnresolved,
        Idx,  // Loaded, DEX idx in super_class_type_idx_ and interfaces_type_idx_.
        Loaded,  // DEX idx values resolved.
        Resolving,  // Just cloned from temporary class object.
        Resolved,  // Part of linking.
        Verifying,  // In the process of being verified.
        RetryVerificationAtRuntime,  // Compile time verification failed, retry at runtime.
        Verified,  // Logically part of linking; done pre-init.
        Initializing,  // Class init in progress.
        Initialized;  // Ready to go.

        static {
            switch (getSdkInt()) {
                case 34: // android 14
                case 33: // android 13
                case 32: // android 12L
                case 31: // android 12
                case 30: // android 11
                case 29: // android 10
                case 28: // android 9
                    NotReady.value = 0;
                    Retired.value = 1;
                    ErrorResolved.value = 2;
                    ErrorUnresolved.value = 3;
                    Idx.value = 4;
                    Loaded.value = 5;
                    Resolving.value = 6;
                    Resolved.value = 7;
                    Verifying.value = 8;
                    RetryVerificationAtRuntime.value = 9;
                    Verified.value = 11;
                    Initializing.value = 13;
                    Initialized.value = 14;
                    break;
                case 27: // android 8.1
                    NotReady.value = 0;
                    Retired.value = -3;
                    ErrorResolved.value = -2;
                    ErrorUnresolved.value = -1;
                    Idx.value = 1;
                    Loaded.value = 2;
                    Resolving.value = 3;
                    Resolved.value = 4;
                    Verifying.value = 5;
                    RetryVerificationAtRuntime.value = 6;
                    Verified.value = 8;
                    Initializing.value = 10;
                    Initialized.value = 11;
                    break;
                case 26: // android 8
                    NotReady.value = 0;
                    Retired.value = -3;
                    ErrorResolved.value = -2;
                    ErrorUnresolved.value = -1;
                    Idx.value = 1;
                    Loaded.value = 2;
                    Resolving.value = 3;
                    Resolved.value = 4;
                    Verifying.value = 5;
                    RetryVerificationAtRuntime.value = 6;
                    Verified.value = 8;
                    Initializing.value = 9;
                    Initialized.value = 10;
                    break;
                default:
                    throw new IllegalStateException("unsupported sdk: " + getSdkInt());
            }
        }

        private int value;

        public int getValue() {
            return value;
        }
    }

    public static int getRawClassStatus(Class<?> clazz) {
        ClassMirror[] mirror = arrayCast(ClassMirror.class, clazz);
        return getSdkInt() <= 27 ? mirror[0].status : (mirror[0].status >>> 32 - 4);
    }

    public static ClassStatus getClassStatus(Class<?> clazz) {
        int status = getRawClassStatus(clazz);
        for (ClassStatus tmp : ClassStatus.values()) {
            if (tmp.value == status) {
                return tmp;
            }
        }
        //TODO: maybe throw exception?
        return null;
    }

    public static void setRawClassStatus(Class<?> clazz, int status) {
        ClassMirror[] mirror = arrayCast(ClassMirror.class, clazz);
        if (getSdkInt() <= 27) {
            mirror[0].status = status;
        } else {
            mirror[0].status = (mirror[0].status & ~0 >>> 4) | (status << 32 - 4);
        }
    }

    public static void setClassStatus(Class<?> clazz, ClassStatus status) {
        setRawClassStatus(clazz, status.value);
    }
}
