package com.v7878.unsafe;

import static com.v7878.unsafe.Utils.searchMethod;

import androidx.annotation.Keep;

import com.v7878.unsafe.function.SymbolLookup;
import com.v7878.unsafe.memory.Pointer;

import java.lang.reflect.Method;

@DangerLevel(8)
public class AndroidUnsafe8 extends AndroidUnsafe7 {

    static {
        String iset = getCurrentInstructionSet();
        switch (iset) {
            case "arm":
            case "arm64":
            case "x86":
            case "x86_64":
            case "riscv64":
                //ok
                break;
            default:
                throw new IllegalStateException("unsupported instruction set: " + iset);
        }
    }

    @Keep
    private abstract static class GCUtils {

        private static byte[] getTrampoline(Pointer symbol) {
            byte[] out;
            int offset;

            String iset = getCurrentInstructionSet();
            switch (iset) {
                case "x86":
                    out = new byte[]{
                            (byte) 0x8b, 0x44, 0x24, 0x10,             //mov    eax,DWORD PTR [esp+0x10]
                            (byte) 0x89, 0x44, 0x24, 0x08,             //mov    DWORD PTR [esp+0x8],eax
                            (byte) 0x8b, 0x44, 0x24, 0x0c,             //mov    eax,DWORD PTR [esp+0xc]
                            (byte) 0x89, 0x44, 0x24, 0x04,             //mov    DWORD PTR [esp+0x4],eax
                            (byte) 0xb8, 0x00, 0x00, 0x00, 0x00,       //mov    eax,0x00000000
                            (byte) 0xff, (byte) 0xe0                   //jmp    eax
                    };
                    offset = 17;
                    break;
                case "x86_64":
                    out = new byte[]{
                            0x48, (byte) 0x89, (byte) 0xd7,        //mov    rdi,rdx
                            0x48, (byte) 0x89, (byte) 0xce,        //mov    rsi,rcx
                            0x48, (byte) 0xb8, 0x00, 0x00, 0x00,
                            0x00, 0x00, 0x00, 0x00, 0x00,          //movabs rax,0x0000000000000000
                            (byte) 0xff, (byte) 0xe0               //jmp    rax
                    };
                    offset = 8;
                    break;
                case "arm": // TODO: TEST!!!
                    out = new byte[]{
                            0x02, 0x00, (byte) 0xa0, (byte) 0xe1, //mov    r0, r2
                            0x03, 0x10, (byte) 0xa0, (byte) 0xe1, //mov    r1, r3
                            0x04, 0x20, (byte) 0x9f, (byte) 0xe5, //ldr    r2, [pc, #4]
                            0x12, (byte) 0xff, 0x2f, (byte) 0xe1, //bx     r2
                            0x00, 0x00, 0x00, 0x00                //.word  0x00000000
                    };
                    offset = 16;
                    break;
                case "arm64":
                    out = new byte[]{
                            (byte) 0xe0, 0x03, 0x02, (byte) 0xaa, // mov x0, x2
                            (byte) 0xe1, 0x03, 0x03, (byte) 0xaa, // mov x1, x3
                            0x42, 0x00, 0x00, 0x58, // ldr x2, #0x10
                            0x40, 0x00, 0x1f, (byte) 0xd6, // br  x2
                            0x00, 0x00, 0x00, 0x00,
                            0x00, 0x00, 0x00, 0x00 // .dword 0x0000000000000000
                    };
                    offset = 16;
                    break;
                //case "riscv64": //TODO
                default:
                    throw new IllegalStateException("unsupported instruction set: " + iset);
            }
            putWordUnaligned(out, ARRAY_BYTE_BASE_OFFSET + offset, symbol.getRawAddress());
            return out;
        }

        static {
            Class<?> word = IS64BIT ? long.class : int.class;
            String suffix = IS64BIT ? "64" : "32";

            Method[] methods = getDeclaredMethods(GCUtils.class);

            try (SymbolLookup art = SymbolLookup.defaultLookup()) {
                Pointer increment = art.lookup(
                        "_ZN3art2gc4Heap24IncrementDisableMovingGCEPNS_6ThreadE");
                Pointer decrement = art.lookup(
                        "_ZN3art2gc4Heap24DecrementDisableMovingGCEPNS_6ThreadE");

                Pointer[] trampolines = NativeCodeUtils.makeCode(GCUtils.class,
                        getTrampoline(increment), getTrampoline(increment));

                setExecutableData(searchMethod(methods,
                        "IncrementDisableMovingGC" + suffix, word, word), trampolines[0]);
                setExecutableData(searchMethod(methods,
                        "DecrementDisableMovingGC" + suffix, word, word), trampolines[1]);
            }
        }

        @SuppressWarnings("JavaJniMissingFunction")
        private static native void IncrementDisableMovingGC64(long heap, long thread);

        @SuppressWarnings("JavaJniMissingFunction")
        private static native void IncrementDisableMovingGC32(int heap, int thread);

        @SuppressWarnings("JavaJniMissingFunction")
        private static native void DecrementDisableMovingGC64(long heap, long thread);

        @SuppressWarnings("JavaJniMissingFunction")
        private static native void DecrementDisableMovingGC32(int heap, int thread);
    }

    public static class ScopedDisableGC implements AutoCloseable {
        public ScopedDisableGC() {
            long heap = getHeapPtr().getRawAddress();
            long thread = getNativePeer(Thread.currentThread()).getRawAddress();
            if (IS64BIT) {
                GCUtils.IncrementDisableMovingGC64(heap, thread);
            } else {
                GCUtils.IncrementDisableMovingGC32((int) heap, (int) thread);
            }
        }

        @Override
        public void close() {
            long heap = getHeapPtr().getRawAddress();
            long thread = getNativePeer(Thread.currentThread()).getRawAddress();
            if (IS64BIT) {
                GCUtils.DecrementDisableMovingGC64(heap, thread);
            } else {
                GCUtils.DecrementDisableMovingGC32((int) heap, (int) thread);
            }
        }
    }
}
