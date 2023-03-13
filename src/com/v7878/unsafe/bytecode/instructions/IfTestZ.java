package com.v7878.unsafe.bytecode.instructions;

import com.v7878.unsafe.bytecode.instructions.InstructionReader.*;

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

    private String toString(String name) {
        return name + " " + register_to_test
                + " " + signed_branch_offset;
    }

    public static class IfEqZ extends IfTestZ {

        public static final int OPCODE = 0x38;

        static void init() {
            InstructionReader.register(OPCODE, new Reader_22x_21t_21s_21h((A, B) -> {
                return new IfEqZ(A, (B << 16) >> 16);
            }));
        }

        public IfEqZ(int A, int B) {
            super(A, B);
        }

        @Override
        public String toString() {
            return super.toString("if-eqz");
        }
    }

    public static class IfNeZ extends IfTestZ {

        public static final int OPCODE = 0x39;

        static void init() {
            InstructionReader.register(OPCODE, new Reader_22x_21t_21s_21h((A, B) -> {
                return new IfNeZ(A, (B << 16) >> 16);
            }));
        }

        public IfNeZ(int A, int B) {
            super(A, B);
        }

        @Override
        public String toString() {
            return super.toString("if-nez");
        }
    }

    public static class IfLtZ extends IfTestZ {

        public static final int OPCODE = 0x3a;

        static void init() {
            InstructionReader.register(OPCODE, new Reader_22x_21t_21s_21h((A, B) -> {
                return new IfLtZ(A, (B << 16) >> 16);
            }));
        }

        public IfLtZ(int A, int B) {
            super(A, B);
        }

        @Override
        public String toString() {
            return super.toString("if-ltz");
        }
    }

    public static class IfGeZ extends IfTestZ {

        public static final int OPCODE = 0x3b;

        static void init() {
            InstructionReader.register(OPCODE, new Reader_22x_21t_21s_21h((A, B) -> {
                return new IfGeZ(A, (B << 16) >> 16);
            }));
        }

        public IfGeZ(int A, int B) {
            super(A, B);
        }

        @Override
        public String toString() {
            return super.toString("if-gez");
        }
    }

    public static class IfGtZ extends IfTestZ {

        public static final int OPCODE = 0x3c;

        static void init() {
            InstructionReader.register(OPCODE, new Reader_22x_21t_21s_21h((A, B) -> {
                return new IfGtZ(A, (B << 16) >> 16);
            }));
        }

        public IfGtZ(int A, int B) {
            super(A, B);
        }

        @Override
        public String toString() {
            return super.toString("if-gtz");
        }
    }

    public static class IfLeZ extends IfTestZ {

        public static final int OPCODE = 0x3d;

        static void init() {
            InstructionReader.register(OPCODE, new Reader_22x_21t_21s_21h((A, B) -> {
                return new IfLeZ(A, (B << 16) >> 16);
            }));
        }

        public IfLeZ(int A, int B) {
            super(A, B);
        }

        @Override
        public String toString() {
            return super.toString("if-lez");
        }
    }
}
