package com.v7878.unsafe.dex.old_bytecode;

import com.v7878.unsafe.dex.WriteContext;
import com.v7878.unsafe.dex.old_bytecode.InstructionReader.Reader_22t_22s;
import com.v7878.unsafe.io.RandomOutput;

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

    @Override
    public void write(WriteContext context, RandomOutput out) {
        InstructionWriter.write_22t_22s(out, opcode_old(),
                first_register_to_test, second_register_to_test,
                signed_branch_offset);
    }

    @Override
    public String toString() {
        return name() + " " + first_register_to_test
                + " " + second_register_to_test
                + " " + signed_branch_offset;
    }

    @Override
    public abstract IfTest clone();

    public static class IfEq extends IfTest {

        public static final int OPCODE = 0x32;

        static void init() {
            InstructionReader.register(OPCODE, new Reader_22t_22s(IfEq::new));
        }

        public IfEq(int A, int B, int C) {
            super(A, B, C);
        }

        @Override
        public int opcode_old() {
            return OPCODE;
        }

        @Override
        public String name() {
            return "if-eq";
        }

        @Override
        public IfEq clone() {
            return new IfEq(first_register_to_test,
                    second_register_to_test, signed_branch_offset);
        }
    }

    public static class IfNe extends IfTest {

        public static final int OPCODE = 0x33;

        static void init() {
            InstructionReader.register(OPCODE, new Reader_22t_22s(IfNe::new));
        }

        public IfNe(int A, int B, int C) {
            super(A, B, C);
        }

        @Override
        public int opcode_old() {
            return OPCODE;
        }

        @Override
        public String name() {
            return "if-ne";
        }

        @Override
        public IfNe clone() {
            return new IfNe(first_register_to_test,
                    second_register_to_test, signed_branch_offset);
        }
    }

    public static class IfLt extends IfTest {

        public static final int OPCODE = 0x34;

        static void init() {
            InstructionReader.register(OPCODE, new Reader_22t_22s(IfLt::new));
        }

        public IfLt(int A, int B, int C) {
            super(A, B, C);
        }

        @Override
        public int opcode_old() {
            return OPCODE;
        }

        @Override
        public String name() {
            return "if-lt";
        }

        @Override
        public IfLt clone() {
            return new IfLt(first_register_to_test,
                    second_register_to_test, signed_branch_offset);
        }
    }

    public static class IfGe extends IfTest {

        public static final int OPCODE = 0x35;

        static void init() {
            InstructionReader.register(OPCODE, new Reader_22t_22s(IfGe::new));
        }

        public IfGe(int A, int B, int C) {
            super(A, B, C);
        }

        @Override
        public int opcode_old() {
            return OPCODE;
        }

        @Override
        public String name() {
            return "if-ge";
        }

        @Override
        public IfGe clone() {
            return new IfGe(first_register_to_test,
                    second_register_to_test, signed_branch_offset);
        }
    }

    public static class IfGt extends IfTest {

        public static final int OPCODE = 0x36;

        static void init() {
            InstructionReader.register(OPCODE, new Reader_22t_22s(IfGt::new));
        }

        public IfGt(int A, int B, int C) {
            super(A, B, C);
        }

        @Override
        public int opcode_old() {
            return OPCODE;
        }

        @Override
        public String name() {
            return "if-gt";
        }

        @Override
        public IfGt clone() {
            return new IfGt(first_register_to_test,
                    second_register_to_test, signed_branch_offset);
        }
    }

    public static class IfLe extends IfTest {

        public static final int OPCODE = 0x37;

        static void init() {
            InstructionReader.register(OPCODE, new Reader_22t_22s(IfLe::new));
        }

        public IfLe(int A, int B, int C) {
            super(A, B, C);
        }

        @Override
        public int opcode_old() {
            return OPCODE;
        }

        @Override
        public String name() {
            return "if-le";
        }

        @Override
        public IfLe clone() {
            return new IfLe(first_register_to_test,
                    second_register_to_test, signed_branch_offset);
        }
    }
}
