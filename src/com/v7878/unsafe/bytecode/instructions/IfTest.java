package com.v7878.unsafe.bytecode.instructions;

import com.v7878.unsafe.bytecode.instructions.InstructionReader.*;

public abstract class IfTest extends Instruction {

    static void init() {
        IfEq.init();                                // 0x32
        IfNe.init();                                // 0x33
        IfLt.init();                                // 0x34
        IfGe.init();                                // 0x35
        IfGt.init();                                // 0x36
        IfLe.init();                                // 0x37
    }

    public final int first_register_to_test;
    public final int second_register_to_test;
    public final int signed_branch_offset;

    public IfTest(int A, int B, int C) {
        first_register_to_test = A;
        second_register_to_test = B;
        signed_branch_offset = C;
    }

    private String toString(String name) {
        return name + " " + first_register_to_test
                + " " + second_register_to_test
                + " " + signed_branch_offset;
    }

    public static class IfEq extends IfTest {

        public static final int OPCODE = 0x32;

        static void init() {
            InstructionReader.register(OPCODE, new Reader_22t_22s((A, B, C) -> {
                return new IfEq(A, B, (C << 16) >> 16);
            }));
        }

        public IfEq(int A, int B, int C) {
            super(A, B, C);
        }

        @Override
        public String toString() {
            return super.toString("if-eq");
        }
    }

    public static class IfNe extends IfTest {

        public static final int OPCODE = 0x33;

        static void init() {
            InstructionReader.register(OPCODE, new Reader_22t_22s((A, B, C) -> {
                return new IfNe(A, B, (C << 16) >> 16);
            }));
        }

        public IfNe(int A, int B, int C) {
            super(A, B, C);
        }

        @Override
        public String toString() {
            return super.toString("if-ne");
        }
    }

    public static class IfLt extends IfTest {

        public static final int OPCODE = 0x34;

        static void init() {
            InstructionReader.register(OPCODE, new Reader_22t_22s((A, B, C) -> {
                return new IfLt(A, B, (C << 16) >> 16);
            }));
        }

        public IfLt(int A, int B, int C) {
            super(A, B, C);
        }

        @Override
        public String toString() {
            return super.toString("if-lt");
        }
    }

    public static class IfGe extends IfTest {

        public static final int OPCODE = 0x35;

        static void init() {
            InstructionReader.register(OPCODE, new Reader_22t_22s((A, B, C) -> {
                return new IfGe(A, B, (C << 16) >> 16);
            }));
        }

        public IfGe(int A, int B, int C) {
            super(A, B, C);
        }

        @Override
        public String toString() {
            return super.toString("if-ge");
        }
    }

    public static class IfGt extends IfTest {

        public static final int OPCODE = 0x36;

        static void init() {
            InstructionReader.register(OPCODE, new Reader_22t_22s((A, B, C) -> {
                return new IfGt(A, B, (C << 16) >> 16);
            }));
        }

        public IfGt(int A, int B, int C) {
            super(A, B, C);
        }

        @Override
        public String toString() {
            return super.toString("if-gt");
        }
    }

    public static class IfLe extends IfTest {

        public static final int OPCODE = 0x37;

        static void init() {
            InstructionReader.register(OPCODE, new Reader_22t_22s((A, B, C) -> {
                return new IfLe(A, B, (C << 16) >> 16);
            }));
        }

        public IfLe(int A, int B, int C) {
            super(A, B, C);
        }

        @Override
        public String toString() {
            return super.toString("if-le");
        }
    }
}
