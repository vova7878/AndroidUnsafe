package com.v7878.unsafe.bytecode.instructions;

import com.v7878.unsafe.bytecode.MethodId;
import com.v7878.unsafe.bytecode.instructions.InstructionReader.*;

public abstract class InvokeKind extends Instruction {

    static void init() {
        InvokeVirtual.init();                       // 0x6e
        InvokeSuper.init();                         // 0x6f
        InvokeDirect.init();                        // 0x70
        InvokeStatic.init();                        // 0x71
        InvokeInterface.init();                     // 0x72
    }

    public final int argument_word_count;
    public final MethodId method_reference;
    public final int argument_register_1;
    public final int argument_register_2;
    public final int argument_register_3;
    public final int argument_register_4;
    public final int argument_register_5;

    public InvokeKind(int A, MethodId B, int C, int D, int E, int F, int G) {
        argument_word_count = A;
        method_reference = B;
        argument_register_1 = C;
        argument_register_2 = D;
        argument_register_3 = E;
        argument_register_4 = F;
        argument_register_5 = G;
    }

    private String toString(String name) {
        return name + " " + argument_word_count
                + " " + argument_register_1
                + " " + argument_register_2
                + " " + argument_register_3
                + " " + argument_register_4
                + " " + argument_register_5;
    }

    public static class InvokeVirtual extends InvokeKind {

        public static final int OPCODE = 0x6e;

        static void init() {
            InstructionReader.register(OPCODE, new Reader_35c_35ms_35mi(
                    (context, A, B, C, D, E, F, G) -> {
                        return new InvokeVirtual(A, context.method(B), C, D, E, F, G);
                    }));
        }

        public InvokeVirtual(int A, MethodId B, int C, int D, int E, int F, int G) {
            super(A, B, C, D, E, F, G);
        }

        @Override
        public String toString() {
            return super.toString("invoke-virtual");
        }
    }

    public static class InvokeSuper extends InvokeKind {

        public static final int OPCODE = 0x6f;

        static void init() {
            InstructionReader.register(OPCODE, new Reader_35c_35ms_35mi(
                    (context, A, B, C, D, E, F, G) -> {
                        return new InvokeSuper(A, context.method(B), C, D, E, F, G);
                    }));
        }

        public InvokeSuper(int A, MethodId B, int C, int D, int E, int F, int G) {
            super(A, B, C, D, E, F, G);
        }

        @Override
        public String toString() {
            return super.toString("invoke-super");
        }
    }

    public static class InvokeDirect extends InvokeKind {

        public static final int OPCODE = 0x70;

        static void init() {
            InstructionReader.register(OPCODE, new Reader_35c_35ms_35mi(
                    (context, A, B, C, D, E, F, G) -> {
                        return new InvokeDirect(A, context.method(B), C, D, E, F, G);
                    }));
        }

        public InvokeDirect(int A, MethodId B, int C, int D, int E, int F, int G) {
            super(A, B, C, D, E, F, G);
        }

        @Override
        public String toString() {
            return super.toString("invoke-direct");
        }
    }

    public static class InvokeStatic extends InvokeKind {

        public static final int OPCODE = 0x71;

        static void init() {
            InstructionReader.register(OPCODE, new Reader_35c_35ms_35mi(
                    (context, A, B, C, D, E, F, G) -> {
                        return new InvokeStatic(A, context.method(B), C, D, E, F, G);
                    }));
        }

        public InvokeStatic(int A, MethodId B, int C, int D, int E, int F, int G) {
            super(A, B, C, D, E, F, G);
        }

        @Override
        public String toString() {
            return super.toString("invoke-static");
        }
    }

    public static class InvokeInterface extends InvokeKind {

        public static final int OPCODE = 0x72;

        static void init() {
            InstructionReader.register(OPCODE, new Reader_35c_35ms_35mi(
                    (context, A, B, C, D, E, F, G) -> {
                        return new InvokeInterface(A, context.method(B), C, D, E, F, G);
                    }));
        }

        public InvokeInterface(int A, MethodId B, int C, int D, int E, int F, int G) {
            super(A, B, C, D, E, F, G);
        }

        @Override
        public String toString() {
            return super.toString("invoke-interface");
        }
    }
}
