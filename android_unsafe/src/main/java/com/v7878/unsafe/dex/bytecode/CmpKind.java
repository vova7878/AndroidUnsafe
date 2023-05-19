package com.v7878.unsafe.dex.bytecode;

import com.v7878.unsafe.dex.WriteContext;
import com.v7878.unsafe.dex.bytecode.InstructionReader.Reader_23x;
import com.v7878.unsafe.io.RandomOutput;

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

    @Override
    public void write(WriteContext context, RandomOutput out) {
        InstructionWriter.write_23x(out, opcode(),
                destination_register, first_source_register_or_pair,
                second_source_register_or_pair);
    }

    @Override
    public String toString() {
        return name() + " " + destination_register
                + " " + first_source_register_or_pair
                + " " + second_source_register_or_pair;
    }

    @Override
    public abstract CmpKind clone();

    public static class CmpLFloat extends CmpKind {

        public static final int OPCODE = 0x2d;

        static void init() {
            InstructionReader.register(OPCODE, new Reader_23x((A, B, C) -> {
                return new CmpLFloat(A, B, C);
            }));
        }

        public CmpLFloat(int A, int B, int C) {
            super(A, B, C);
        }

        @Override
        public int opcode() {
            return OPCODE;
        }

        @Override
        public String name() {
            return "cmpl-float";
        }

        @Override
        public CmpLFloat clone() {
            return new CmpLFloat(destination_register,
                    first_source_register_or_pair,
                    second_source_register_or_pair);
        }
    }

    public static class CmpGFloat extends CmpKind {

        public static final int OPCODE = 0x2e;

        static void init() {
            InstructionReader.register(OPCODE, new Reader_23x((A, B, C) -> {
                return new CmpGFloat(A, B, C);
            }));
        }

        public CmpGFloat(int A, int B, int C) {
            super(A, B, C);
        }

        @Override
        public int opcode() {
            return OPCODE;
        }

        @Override
        public String name() {
            return "cmpg-float";
        }

        @Override
        public CmpGFloat clone() {
            return new CmpGFloat(destination_register,
                    first_source_register_or_pair,
                    second_source_register_or_pair);
        }
    }

    public static class CmpLDouble extends CmpKind {

        public static final int OPCODE = 0x2f;

        static void init() {
            InstructionReader.register(OPCODE, new Reader_23x((A, B, C) -> {
                return new CmpLDouble(A, B, C);
            }));
        }

        public CmpLDouble(int A, int B, int C) {
            super(A, B, C);
        }

        @Override
        public int opcode() {
            return OPCODE;
        }

        @Override
        public String name() {
            return "cmpl-double";
        }

        @Override
        public CmpLDouble clone() {
            return new CmpLDouble(destination_register,
                    first_source_register_or_pair,
                    second_source_register_or_pair);
        }
    }

    public static class CmpGDouble extends CmpKind {

        public static final int OPCODE = 0x30;

        static void init() {
            InstructionReader.register(OPCODE, new Reader_23x((A, B, C) -> {
                return new CmpGDouble(A, B, C);
            }));
        }

        public CmpGDouble(int A, int B, int C) {
            super(A, B, C);
        }

        @Override
        public int opcode() {
            return OPCODE;
        }

        @Override
        public String name() {
            return "cmpg-double";
        }

        @Override
        public CmpGDouble clone() {
            return new CmpGDouble(destination_register,
                    first_source_register_or_pair,
                    second_source_register_or_pair);
        }
    }

    public static class CmpLong extends CmpKind {

        public static final int OPCODE = 0x31;

        static void init() {
            InstructionReader.register(OPCODE, new Reader_23x((A, B, C) -> {
                return new CmpLong(A, B, C);
            }));
        }

        public CmpLong(int A, int B, int C) {
            super(A, B, C);
        }

        @Override
        public int opcode() {
            return OPCODE;
        }

        @Override
        public String name() {
            return "cmp-long";
        }

        @Override
        public CmpLong clone() {
            return new CmpLong(destination_register,
                    first_source_register_or_pair,
                    second_source_register_or_pair);
        }
    }
}
