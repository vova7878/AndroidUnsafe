package com.v7878.unsafe.dex.bytecode;

import com.v7878.unsafe.dex.WriteContext;
import com.v7878.unsafe.dex.bytecode.InstructionReader.Reader_21t_21s32;
import com.v7878.unsafe.io.RandomOutput;

public abstract class IfTestZ extends Instruction {

    static void init() {
        IfEqZ.init();                                // 0x38
        IfNeZ.init();                                // 0x39
        IfLtZ.init();                                // 0x3a
        IfGeZ.init();                                // 0x3b
        IfGtZ.init();                                // 0x3c
        IfLeZ.init();                                // 0x3d
    }

    public final int register_to_test;
    public final int signed_branch_offset;

    public IfTestZ(int A, int B) {
        register_to_test = A;
        signed_branch_offset = B;
    }

    @Override
    public void write(WriteContext context, RandomOutput out) {
        InstructionWriter.write_21t_21s32(out, opcode(),
                register_to_test, signed_branch_offset);
    }

    @Override
    public String toString() {
        return name() + " " + register_to_test
                + " " + signed_branch_offset;
    }

    @Override
    public abstract IfTestZ clone();

    public static class IfEqZ extends IfTestZ {

        public static final int OPCODE = 0x38;

        static void init() {
            InstructionReader.register(OPCODE, new Reader_21t_21s32((A, B) -> {
                return new IfEqZ(A, (B << 16) >> 16);
            }));
        }

        public IfEqZ(int A, int B) {
            super(A, B);
        }

        @Override
        public int opcode() {
            return OPCODE;
        }

        @Override
        public String name() {
            return "if-eqz";
        }

        @Override
        public IfEqZ clone() {
            return new IfEqZ(register_to_test, signed_branch_offset);
        }
    }

    public static class IfNeZ extends IfTestZ {

        public static final int OPCODE = 0x39;

        static void init() {
            InstructionReader.register(OPCODE, new Reader_21t_21s32((A, B) -> {
                return new IfNeZ(A, (B << 16) >> 16);
            }));
        }

        public IfNeZ(int A, int B) {
            super(A, B);
        }

        @Override
        public int opcode() {
            return OPCODE;
        }

        @Override
        public String name() {
            return "if-nez";
        }

        @Override
        public IfNeZ clone() {
            return new IfNeZ(register_to_test, signed_branch_offset);
        }
    }

    public static class IfLtZ extends IfTestZ {

        public static final int OPCODE = 0x3a;

        static void init() {
            InstructionReader.register(OPCODE, new Reader_21t_21s32((A, B) -> {
                return new IfLtZ(A, (B << 16) >> 16);
            }));
        }

        public IfLtZ(int A, int B) {
            super(A, B);
        }

        @Override
        public int opcode() {
            return OPCODE;
        }

        @Override
        public String name() {
            return "if-ltz";
        }

        @Override
        public IfLtZ clone() {
            return new IfLtZ(register_to_test, signed_branch_offset);
        }
    }

    public static class IfGeZ extends IfTestZ {

        public static final int OPCODE = 0x3b;

        static void init() {
            InstructionReader.register(OPCODE, new Reader_21t_21s32((A, B) -> {
                return new IfGeZ(A, (B << 16) >> 16);
            }));
        }

        public IfGeZ(int A, int B) {
            super(A, B);
        }

        @Override
        public int opcode() {
            return OPCODE;
        }

        @Override
        public String name() {
            return "if-gez";
        }

        @Override
        public IfGeZ clone() {
            return new IfGeZ(register_to_test, signed_branch_offset);
        }
    }

    public static class IfGtZ extends IfTestZ {

        public static final int OPCODE = 0x3c;

        static void init() {
            InstructionReader.register(OPCODE, new Reader_21t_21s32((A, B) -> {
                return new IfGtZ(A, (B << 16) >> 16);
            }));
        }

        public IfGtZ(int A, int B) {
            super(A, B);
        }

        @Override
        public int opcode() {
            return OPCODE;
        }

        @Override
        public String name() {
            return "if-gtz";
        }

        @Override
        public IfGtZ clone() {
            return new IfGtZ(register_to_test, signed_branch_offset);
        }
    }

    public static class IfLeZ extends IfTestZ {

        public static final int OPCODE = 0x3d;

        static void init() {
            InstructionReader.register(OPCODE, new Reader_21t_21s32((A, B) -> {
                return new IfLeZ(A, (B << 16) >> 16);
            }));
        }

        public IfLeZ(int A, int B) {
            super(A, B);
        }

        @Override
        public int opcode() {
            return OPCODE;
        }

        @Override
        public String name() {
            return "if-lez";
        }

        @Override
        public IfLeZ clone() {
            return new IfLeZ(register_to_test, signed_branch_offset);
        }
    }
}
