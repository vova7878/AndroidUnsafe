package com.v7878.unsafe.bytecode.instructions;

import com.v7878.unsafe.bytecode.*;
import com.v7878.unsafe.bytecode.instructions.InstructionReader.*;

public abstract class SStaticOp extends Instruction {

    static void init() {
        SGet.init();                                // 0x60
        SGetWide.init();                            // 0x61
        SGetObject.init();                          // 0x62
        SGetBoolean.init();                         // 0x63
        SGetByte.init();                            // 0x64
        SGetChar.init();                            // 0x65
        SGetShort.init();                           // 0x66

        SPut.init();                                // 0x67
        SPutWide.init();                            // 0x68
        SPutObject.init();                          // 0x69
        SPutBoolean.init();                         // 0x6a
        SPutByte.init();                            // 0x6b
        SPutChar.init();                            // 0x6c
        SPutShort.init();                           // 0x6d
    }

    public final int value_register_or_pair;
    public final FieldId static_field;

    public SStaticOp(int A, FieldId B) {
        value_register_or_pair = A;
        static_field = B;
    }

    @Override
    public void fillContext(DataSet data) {
        data.addField(static_field);
    }

    private String toString(String name) {
        return name + " " + value_register_or_pair
                + " " + static_field;
    }

    public static class SGet extends SStaticOp {

        public static final int OPCODE = 0x60;

        static void init() {
            InstructionReader.register(OPCODE, new Reader_20bc_21c((context, A, B) -> {
                return new SGet(A, context.field(B));
            }));
        }

        public SGet(int A, FieldId B) {
            super(A, B);
        }

        @Override
        public String toString() {
            return super.toString("sget");
        }
    }

    public static class SGetWide extends SStaticOp {

        public static final int OPCODE = 0x61;

        static void init() {
            InstructionReader.register(OPCODE, new Reader_20bc_21c((context, A, B) -> {
                return new SGetWide(A, context.field(B));
            }));
        }

        public SGetWide(int A, FieldId B) {
            super(A, B);
        }

        @Override
        public String toString() {
            return super.toString("sget-wide");
        }
    }

    public static class SGetObject extends SStaticOp {

        public static final int OPCODE = 0x62;

        static void init() {
            InstructionReader.register(OPCODE, new Reader_20bc_21c((context, A, B) -> {
                return new SGetObject(A, context.field(B));
            }));
        }

        public SGetObject(int A, FieldId B) {
            super(A, B);
        }

        @Override
        public String toString() {
            return super.toString("sget-object");
        }
    }

    public static class SGetBoolean extends SStaticOp {

        public static final int OPCODE = 0x63;

        static void init() {
            InstructionReader.register(OPCODE, new Reader_20bc_21c((context, A, B) -> {
                return new SGetBoolean(A, context.field(B));
            }));
        }

        public SGetBoolean(int A, FieldId B) {
            super(A, B);
        }

        @Override
        public String toString() {
            return super.toString("sget-boolean");
        }
    }

    public static class SGetByte extends SStaticOp {

        public static final int OPCODE = 0x64;

        static void init() {
            InstructionReader.register(OPCODE, new Reader_20bc_21c((context, A, B) -> {
                return new SGetByte(A, context.field(B));
            }));
        }

        public SGetByte(int A, FieldId B) {
            super(A, B);
        }

        @Override
        public String toString() {
            return super.toString("sget-byte");
        }
    }

    public static class SGetChar extends SStaticOp {

        public static final int OPCODE = 0x65;

        static void init() {
            InstructionReader.register(OPCODE, new Reader_20bc_21c((context, A, B) -> {
                return new SGetChar(A, context.field(B));
            }));
        }

        public SGetChar(int A, FieldId B) {
            super(A, B);
        }

        @Override
        public String toString() {
            return super.toString("sget-char");
        }
    }

    public static class SGetShort extends SStaticOp {

        public static final int OPCODE = 0x66;

        static void init() {
            InstructionReader.register(OPCODE, new Reader_20bc_21c((context, A, B) -> {
                return new SGetShort(A, context.field(B));
            }));
        }

        public SGetShort(int A, FieldId B) {
            super(A, B);
        }

        @Override
        public String toString() {
            return super.toString("sget-short");
        }
    }

    public static class SPut extends SStaticOp {

        public static final int OPCODE = 0x67;

        static void init() {
            InstructionReader.register(OPCODE, new Reader_20bc_21c((context, A, B) -> {
                return new SPut(A, context.field(B));
            }));
        }

        public SPut(int A, FieldId B) {
            super(A, B);
        }

        @Override
        public String toString() {
            return super.toString("sput");
        }
    }

    public static class SPutWide extends SStaticOp {

        public static final int OPCODE = 0x68;

        static void init() {
            InstructionReader.register(OPCODE, new Reader_20bc_21c((context, A, B) -> {
                return new SPutWide(A, context.field(B));
            }));
        }

        public SPutWide(int A, FieldId B) {
            super(A, B);
        }

        @Override
        public String toString() {
            return super.toString("sput-wide");
        }
    }

    public static class SPutObject extends SStaticOp {

        public static final int OPCODE = 0x69;

        static void init() {
            InstructionReader.register(OPCODE, new Reader_20bc_21c((context, A, B) -> {
                return new SPutObject(A, context.field(B));
            }));
        }

        public SPutObject(int A, FieldId B) {
            super(A, B);
        }

        @Override
        public String toString() {
            return super.toString("sput-object");
        }
    }

    public static class SPutBoolean extends SStaticOp {

        public static final int OPCODE = 0x6a;

        static void init() {
            InstructionReader.register(OPCODE, new Reader_20bc_21c((context, A, B) -> {
                return new SPutBoolean(A, context.field(B));
            }));
        }

        public SPutBoolean(int A, FieldId B) {
            super(A, B);
        }

        @Override
        public String toString() {
            return super.toString("sput-boolean");
        }
    }

    public static class SPutByte extends SStaticOp {

        public static final int OPCODE = 0x6b;

        static void init() {
            InstructionReader.register(OPCODE, new Reader_20bc_21c((context, A, B) -> {
                return new SPutByte(A, context.field(B));
            }));
        }

        public SPutByte(int A, FieldId B) {
            super(A, B);
        }

        @Override
        public String toString() {
            return super.toString("sput-byte");
        }
    }

    public static class SPutChar extends SStaticOp {

        public static final int OPCODE = 0x6c;

        static void init() {
            InstructionReader.register(OPCODE, new Reader_20bc_21c((context, A, B) -> {
                return new SPutChar(A, context.field(B));
            }));
        }

        public SPutChar(int A, FieldId B) {
            super(A, B);
        }

        @Override
        public String toString() {
            return super.toString("sput-char");
        }
    }

    public static class SPutShort extends SStaticOp {

        public static final int OPCODE = 0x6d;

        static void init() {
            InstructionReader.register(OPCODE, new Reader_20bc_21c((context, A, B) -> {
                return new SPutShort(A, context.field(B));
            }));
        }

        public SPutShort(int A, FieldId B) {
            super(A, B);
        }

        @Override
        public String toString() {
            return super.toString("sput-short");
        }
    }
}
