package com.v7878.unsafe.bytecode.instructions;

import com.v7878.unsafe.bytecode.instructions.InstructionReader.*;

public abstract class CmpKind extends Instruction {

    static void init() {
        CmpLFloat.init();                           // 0x2d
        CmpGFloat.init();                           // 0x2e
        CmpLDouble.init();                          // 0x2f
        CmpGDouble.init();                          // 0x30
        CmpLong.init();                             // 0x31
    }

    public final int destination_register;
    public final int first_source_register_or_pair;
    public final int second_source_register_or_pair;

    public CmpKind(int A, int B, int C) {
        destination_register = A;
        first_source_register_or_pair = B;
        second_source_register_or_pair = C;
    }

    private String toString(String name) {
        return name + " " + destination_register
                + " " + first_source_register_or_pair
                + " " + second_source_register_or_pair;
    }

    public static class CmpLFloat extends CmpKind {

        public static final int OPCODE = 0x2d;

        static void init() {
            InstructionReader.register(OPCODE, new Reader_23x_22b((A, B, C) -> {
                return new CmpLFloat(A, B, C);
            }));
        }

        public CmpLFloat(int A, int B, int C) {
            super(A, B, C);
        }

        @Override
        public String toString() {
            return super.toString("cmpl-float");
        }
    }

    public static class CmpGFloat extends CmpKind {

        public static final int OPCODE = 0x2e;

        static void init() {
            InstructionReader.register(OPCODE, new Reader_23x_22b((A, B, C) -> {
                return new CmpGFloat(A, B, C);
            }));
        }

        public CmpGFloat(int A, int B, int C) {
            super(A, B, C);
        }

        @Override
        public String toString() {
            return super.toString("cmpg-float");
        }
    }

    public static class CmpLDouble extends CmpKind {

        public static final int OPCODE = 0x2f;

        static void init() {
            InstructionReader.register(OPCODE, new Reader_23x_22b((A, B, C) -> {
                return new CmpLDouble(A, B, C);
            }));
        }

        public CmpLDouble(int A, int B, int C) {
            super(A, B, C);
        }

        @Override
        public String toString() {
            return super.toString("cmpl-double");
        }
    }

    public static class CmpGDouble extends CmpKind {

        public static final int OPCODE = 0x30;

        static void init() {
            InstructionReader.register(OPCODE, new Reader_23x_22b((A, B, C) -> {
                return new CmpGDouble(A, B, C);
            }));
        }

        public CmpGDouble(int A, int B, int C) {
            super(A, B, C);
        }

        @Override
        public String toString() {
            return super.toString("cmpg-double");
        }
    }

    public static class CmpLong extends CmpKind {

        public static final int OPCODE = 0x31;

        static void init() {
            InstructionReader.register(OPCODE, new Reader_23x_22b((A, B, C) -> {
                return new CmpLong(A, B, C);
            }));
        }

        public CmpLong(int A, int B, int C) {
            super(A, B, C);
        }

        @Override
        public String toString() {
            return super.toString("cmp-long");
        }
    }
}
