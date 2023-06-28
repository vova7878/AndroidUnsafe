package com.v7878.unsafe.dex.old_bytecode;

import com.v7878.unsafe.dex.DataCollector;
import com.v7878.unsafe.dex.FieldId;
import com.v7878.unsafe.dex.WriteContext;
import com.v7878.unsafe.dex.old_bytecode.InstructionReader.Reader_20bc_21c;
import com.v7878.unsafe.io.RandomOutput;

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
    public void collectData(DataCollector data) {
        data.add(static_field);
    }

    @Override
    public void write(WriteContext context, RandomOutput out) {
        InstructionWriter.write_22x_20bc_21c(out, opcode_old(), value_register_or_pair,
                context.getFieldIndex(static_field));
    }

    @Override
    public String toString() {
        return name() + " " + value_register_or_pair
                + " " + static_field;
    }

    @Override
    public abstract SStaticOp clone();

    public static class SGet extends SStaticOp {

        public static final int OPCODE = 0x60;

        static void init() {
            InstructionReader.register(OPCODE, new Reader_20bc_21c((context, A, B) -> new SGet(A, context.field(B))));
        }

        public SGet(int A, FieldId B) {
            super(A, B);
        }

        @Override
        public int opcode_old() {
            return OPCODE;
        }

        @Override
        public String name() {
            return "sget";
        }

        @Override
        public SGet clone() {
            return new SGet(value_register_or_pair, static_field);
        }
    }

    public static class SGetWide extends SStaticOp {

        public static final int OPCODE = 0x61;

        static void init() {
            InstructionReader.register(OPCODE, new Reader_20bc_21c((context, A, B) -> new SGetWide(A, context.field(B))));
        }

        public SGetWide(int A, FieldId B) {
            super(A, B);
        }

        @Override
        public int opcode_old() {
            return OPCODE;
        }

        @Override
        public String name() {
            return "sget-wide";
        }

        @Override
        public SGetWide clone() {
            return new SGetWide(value_register_or_pair, static_field);
        }
    }

    public static class SGetObject extends SStaticOp {

        public static final int OPCODE = 0x62;

        static void init() {
            InstructionReader.register(OPCODE, new Reader_20bc_21c((context, A, B) -> new SGetObject(A, context.field(B))));
        }

        public SGetObject(int A, FieldId B) {
            super(A, B);
        }

        @Override
        public int opcode_old() {
            return OPCODE;
        }

        @Override
        public String name() {
            return "sget-object";
        }

        @Override
        public SGetObject clone() {
            return new SGetObject(value_register_or_pair, static_field);
        }
    }

    public static class SGetBoolean extends SStaticOp {

        public static final int OPCODE = 0x63;

        static void init() {
            InstructionReader.register(OPCODE, new Reader_20bc_21c((context, A, B) -> new SGetBoolean(A, context.field(B))));
        }

        public SGetBoolean(int A, FieldId B) {
            super(A, B);
        }

        @Override
        public int opcode_old() {
            return OPCODE;
        }

        @Override
        public String name() {
            return "sget-boolean";
        }

        @Override
        public SGetBoolean clone() {
            return new SGetBoolean(value_register_or_pair, static_field);
        }
    }

    public static class SGetByte extends SStaticOp {

        public static final int OPCODE = 0x64;

        static void init() {
            InstructionReader.register(OPCODE, new Reader_20bc_21c((context, A, B) -> new SGetByte(A, context.field(B))));
        }

        public SGetByte(int A, FieldId B) {
            super(A, B);
        }

        @Override
        public int opcode_old() {
            return OPCODE;
        }

        @Override
        public String name() {
            return "sget-byte";
        }

        @Override
        public SGetByte clone() {
            return new SGetByte(value_register_or_pair, static_field);
        }
    }

    public static class SGetChar extends SStaticOp {

        public static final int OPCODE = 0x65;

        static void init() {
            InstructionReader.register(OPCODE, new Reader_20bc_21c((context, A, B) -> new SGetChar(A, context.field(B))));
        }

        public SGetChar(int A, FieldId B) {
            super(A, B);
        }

        @Override
        public int opcode_old() {
            return OPCODE;
        }

        @Override
        public String name() {
            return "sget-char";
        }

        @Override
        public SGetChar clone() {
            return new SGetChar(value_register_or_pair, static_field);
        }
    }

    public static class SGetShort extends SStaticOp {

        public static final int OPCODE = 0x66;

        static void init() {
            InstructionReader.register(OPCODE, new Reader_20bc_21c((context, A, B) -> new SGetShort(A, context.field(B))));
        }

        public SGetShort(int A, FieldId B) {
            super(A, B);
        }

        @Override
        public int opcode_old() {
            return OPCODE;
        }

        @Override
        public String name() {
            return "sget-short";
        }

        @Override
        public SGetShort clone() {
            return new SGetShort(value_register_or_pair, static_field);
        }
    }

    public static class SPut extends SStaticOp {

        public static final int OPCODE = 0x67;

        static void init() {
            InstructionReader.register(OPCODE, new Reader_20bc_21c((context, A, B) -> new SPut(A, context.field(B))));
        }

        public SPut(int A, FieldId B) {
            super(A, B);
        }

        @Override
        public int opcode_old() {
            return OPCODE;
        }

        @Override
        public String name() {
            return "sput";
        }

        @Override
        public SPut clone() {
            return new SPut(value_register_or_pair, static_field);
        }
    }

    public static class SPutWide extends SStaticOp {

        public static final int OPCODE = 0x68;

        static void init() {
            InstructionReader.register(OPCODE, new Reader_20bc_21c((context, A, B) -> new SPutWide(A, context.field(B))));
        }

        public SPutWide(int A, FieldId B) {
            super(A, B);
        }

        @Override
        public int opcode_old() {
            return OPCODE;
        }

        @Override
        public String name() {
            return "sput-wide";
        }

        @Override
        public SPutWide clone() {
            return new SPutWide(value_register_or_pair, static_field);
        }
    }

    public static class SPutObject extends SStaticOp {

        public static final int OPCODE = 0x69;

        static void init() {
            InstructionReader.register(OPCODE, new Reader_20bc_21c((context, A, B) -> new SPutObject(A, context.field(B))));
        }

        public SPutObject(int A, FieldId B) {
            super(A, B);
        }

        @Override
        public int opcode_old() {
            return OPCODE;
        }

        @Override
        public String name() {
            return "sput-object";
        }

        @Override
        public SPutObject clone() {
            return new SPutObject(value_register_or_pair, static_field);
        }
    }

    public static class SPutBoolean extends SStaticOp {

        public static final int OPCODE = 0x6a;

        static void init() {
            InstructionReader.register(OPCODE, new Reader_20bc_21c((context, A, B) -> new SPutBoolean(A, context.field(B))));
        }

        public SPutBoolean(int A, FieldId B) {
            super(A, B);
        }

        @Override
        public int opcode_old() {
            return OPCODE;
        }

        @Override
        public String name() {
            return "sput-boolean";
        }

        @Override
        public SPutBoolean clone() {
            return new SPutBoolean(value_register_or_pair, static_field);
        }
    }

    public static class SPutByte extends SStaticOp {

        public static final int OPCODE = 0x6b;

        static void init() {
            InstructionReader.register(OPCODE, new Reader_20bc_21c((context, A, B) -> new SPutByte(A, context.field(B))));
        }

        public SPutByte(int A, FieldId B) {
            super(A, B);
        }

        @Override
        public int opcode_old() {
            return OPCODE;
        }

        @Override
        public String name() {
            return "sput-byte";
        }

        @Override
        public SPutByte clone() {
            return new SPutByte(value_register_or_pair, static_field);
        }
    }

    public static class SPutChar extends SStaticOp {

        public static final int OPCODE = 0x6c;

        static void init() {
            InstructionReader.register(OPCODE, new Reader_20bc_21c((context, A, B) -> new SPutChar(A, context.field(B))));
        }

        public SPutChar(int A, FieldId B) {
            super(A, B);
        }

        @Override
        public int opcode_old() {
            return OPCODE;
        }

        @Override
        public String name() {
            return "sput-char";
        }

        @Override
        public SPutChar clone() {
            return new SPutChar(value_register_or_pair, static_field);
        }
    }

    public static class SPutShort extends SStaticOp {

        public static final int OPCODE = 0x6d;

        static void init() {
            InstructionReader.register(OPCODE, new Reader_20bc_21c((context, A, B) -> new SPutShort(A, context.field(B))));
        }

        public SPutShort(int A, FieldId B) {
            super(A, B);
        }

        @Override
        public int opcode_old() {
            return OPCODE;
        }

        @Override
        public String name() {
            return "sput-short";
        }

        @Override
        public SPutShort clone() {
            return new SPutShort(value_register_or_pair, static_field);
        }
    }
}
