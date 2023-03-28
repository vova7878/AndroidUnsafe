package com.v7878.unsafe.dex.bytecode;

import com.v7878.unsafe.dex.DataCollector;
import com.v7878.unsafe.dex.FieldId;
import com.v7878.unsafe.dex.WriteContext;
import com.v7878.unsafe.dex.bytecode.InstructionReader.Reader_22c_22cs;
import com.v7878.unsafe.io.RandomOutput;

public abstract class IInstanceOp extends Instruction {

    static void init() {
        IGet.init();                                // 0x52
        IGetWide.init();                            // 0x53
        IGetObject.init();                          // 0x54
        IGetBoolean.init();                         // 0x55
        IGetByte.init();                            // 0x56
        IGetChar.init();                            // 0x57
        IGetShort.init();                           // 0x58

        IPut.init();                                // 0x59
        IPutWide.init();                            // 0x5a
        IPutObject.init();                          // 0x5b
        IPutBoolean.init();                         // 0x5c
        IPutByte.init();                            // 0x5d
        IPutChar.init();                            // 0x5e
        IPutShort.init();                           // 0x5f
    }

    public final int value_register_or_pair;
    public final int object_register;
    public final FieldId instance_field;

    public IInstanceOp(int A, int B, FieldId C) {
        value_register_or_pair = A;
        object_register = B;
        instance_field = C;
    }

    @Override
    public void collectData(DataCollector data) {
        data.add(instance_field);
    }

    @Override
    public void write(WriteContext context, RandomOutput out) {
        InstructionWriter.write_22c_22cs(out, opcode(),
                value_register_or_pair, object_register,
                context.getFieldIndex(instance_field));
    }

    @Override
    public String toString() {
        return name() + " " + value_register_or_pair
                + " " + object_register
                + " " + instance_field;
    }

    @Override
    public abstract IInstanceOp clone();

    public static class IGet extends IInstanceOp {

        public static final int OPCODE = 0x52;

        static void init() {
            InstructionReader.register(OPCODE, new Reader_22c_22cs((context, A, B, C) -> {
                return new IGet(A, B, context.field(C));
            }));
        }

        public IGet(int A, int B, FieldId C) {
            super(A, B, C);
        }

        @Override
        public int opcode() {
            return OPCODE;
        }

        @Override
        public String name() {
            return "iget";
        }

        @Override
        public IGet clone() {
            return new IGet(value_register_or_pair,
                    object_register, instance_field);
        }
    }

    public static class IGetWide extends IInstanceOp {

        public static final int OPCODE = 0x53;

        static void init() {
            InstructionReader.register(OPCODE, new Reader_22c_22cs((context, A, B, C) -> {
                return new IGetWide(A, B, context.field(C));
            }));
        }

        public IGetWide(int A, int B, FieldId C) {
            super(A, B, C);
        }

        @Override
        public int opcode() {
            return OPCODE;
        }

        @Override
        public String name() {
            return "iget-wide";
        }

        @Override
        public IGetWide clone() {
            return new IGetWide(value_register_or_pair,
                    object_register, instance_field);
        }
    }

    public static class IGetObject extends IInstanceOp {

        public static final int OPCODE = 0x54;

        static void init() {
            InstructionReader.register(OPCODE, new Reader_22c_22cs((context, A, B, C) -> {
                return new IGetObject(A, B, context.field(C));
            }));
        }

        public IGetObject(int A, int B, FieldId C) {
            super(A, B, C);
        }

        @Override
        public int opcode() {
            return OPCODE;
        }

        @Override
        public String name() {
            return "iget-object";
        }

        @Override
        public IGetObject clone() {
            return new IGetObject(value_register_or_pair,
                    object_register, instance_field);
        }
    }

    public static class IGetBoolean extends IInstanceOp {

        public static final int OPCODE = 0x55;

        static void init() {
            InstructionReader.register(OPCODE, new Reader_22c_22cs((context, A, B, C) -> {
                return new IGetBoolean(A, B, context.field(C));
            }));
        }

        public IGetBoolean(int A, int B, FieldId C) {
            super(A, B, C);
        }

        @Override
        public int opcode() {
            return OPCODE;
        }

        @Override
        public String name() {
            return "iget-boolean";
        }

        @Override
        public IGetBoolean clone() {
            return new IGetBoolean(value_register_or_pair,
                    object_register, instance_field);
        }
    }

    public static class IGetByte extends IInstanceOp {

        public static final int OPCODE = 0x56;

        static void init() {
            InstructionReader.register(OPCODE, new Reader_22c_22cs((context, A, B, C) -> {
                return new IGetByte(A, B, context.field(C));
            }));
        }

        public IGetByte(int A, int B, FieldId C) {
            super(A, B, C);
        }

        @Override
        public int opcode() {
            return OPCODE;
        }

        @Override
        public String name() {
            return "iget-byte";
        }

        @Override
        public IGetByte clone() {
            return new IGetByte(value_register_or_pair,
                    object_register, instance_field);
        }
    }

    public static class IGetChar extends IInstanceOp {

        public static final int OPCODE = 0x57;

        static void init() {
            InstructionReader.register(OPCODE, new Reader_22c_22cs((context, A, B, C) -> {
                return new IGetChar(A, B, context.field(C));
            }));
        }

        public IGetChar(int A, int B, FieldId C) {
            super(A, B, C);
        }

        @Override
        public int opcode() {
            return OPCODE;
        }

        @Override
        public String name() {
            return "iget-char";
        }

        @Override
        public IGetChar clone() {
            return new IGetChar(value_register_or_pair,
                    object_register, instance_field);
        }
    }

    public static class IGetShort extends IInstanceOp {

        public static final int OPCODE = 0x58;

        static void init() {
            InstructionReader.register(OPCODE, new Reader_22c_22cs((context, A, B, C) -> {
                return new IGetShort(A, B, context.field(C));
            }));
        }

        public IGetShort(int A, int B, FieldId C) {
            super(A, B, C);
        }

        @Override
        public int opcode() {
            return OPCODE;
        }

        @Override
        public String name() {
            return "iget-short";
        }

        @Override
        public IGetShort clone() {
            return new IGetShort(value_register_or_pair,
                    object_register, instance_field);
        }
    }

    public static class IPut extends IInstanceOp {

        public static final int OPCODE = 0x59;

        static void init() {
            InstructionReader.register(OPCODE, new Reader_22c_22cs((context, A, B, C) -> {
                return new IPut(A, B, context.field(C));
            }));
        }

        public IPut(int A, int B, FieldId C) {
            super(A, B, C);
        }

        @Override
        public int opcode() {
            return OPCODE;
        }

        @Override
        public String name() {
            return "iput";
        }

        @Override
        public IPut clone() {
            return new IPut(value_register_or_pair,
                    object_register, instance_field);
        }
    }

    public static class IPutWide extends IInstanceOp {

        public static final int OPCODE = 0x5a;

        static void init() {
            InstructionReader.register(OPCODE, new Reader_22c_22cs((context, A, B, C) -> {
                return new IPutWide(A, B, context.field(C));
            }));
        }

        public IPutWide(int A, int B, FieldId C) {
            super(A, B, C);
        }

        @Override
        public int opcode() {
            return OPCODE;
        }

        @Override
        public String name() {
            return "iput-wide";
        }

        @Override
        public IPutWide clone() {
            return new IPutWide(value_register_or_pair,
                    object_register, instance_field);
        }
    }

    public static class IPutObject extends IInstanceOp {

        public static final int OPCODE = 0x5b;

        static void init() {
            InstructionReader.register(OPCODE, new Reader_22c_22cs((context, A, B, C) -> {
                return new IPutObject(A, B, context.field(C));
            }));
        }

        public IPutObject(int A, int B, FieldId C) {
            super(A, B, C);
        }

        @Override
        public int opcode() {
            return OPCODE;
        }

        @Override
        public String name() {
            return "iput-object";
        }

        @Override
        public IPutObject clone() {
            return new IPutObject(value_register_or_pair,
                    object_register, instance_field);
        }
    }

    public static class IPutBoolean extends IInstanceOp {

        public static final int OPCODE = 0x5c;

        static void init() {
            InstructionReader.register(OPCODE, new Reader_22c_22cs((context, A, B, C) -> {
                return new IPutBoolean(A, B, context.field(C));
            }));
        }

        public IPutBoolean(int A, int B, FieldId C) {
            super(A, B, C);
        }

        @Override
        public int opcode() {
            return OPCODE;
        }

        @Override
        public String name() {
            return "iput-boolean";
        }

        @Override
        public IPutBoolean clone() {
            return new IPutBoolean(value_register_or_pair,
                    object_register, instance_field);
        }
    }

    public static class IPutByte extends IInstanceOp {

        public static final int OPCODE = 0x5d;

        static void init() {
            InstructionReader.register(OPCODE, new Reader_22c_22cs((context, A, B, C) -> {
                return new IPutByte(A, B, context.field(C));
            }));
        }

        public IPutByte(int A, int B, FieldId C) {
            super(A, B, C);
        }

        @Override
        public int opcode() {
            return OPCODE;
        }

        @Override
        public String name() {
            return "iput-byte";
        }

        @Override
        public IPutByte clone() {
            return new IPutByte(value_register_or_pair,
                    object_register, instance_field);
        }
    }

    public static class IPutChar extends IInstanceOp {

        public static final int OPCODE = 0x5e;

        static void init() {
            InstructionReader.register(OPCODE, new Reader_22c_22cs((context, A, B, C) -> {
                return new IPutChar(A, B, context.field(C));
            }));
        }

        public IPutChar(int A, int B, FieldId C) {
            super(A, B, C);
        }

        @Override
        public int opcode() {
            return OPCODE;
        }

        @Override
        public String name() {
            return "iput-char";
        }

        @Override
        public IPutChar clone() {
            return new IPutChar(value_register_or_pair,
                    object_register, instance_field);
        }
    }

    public static class IPutShort extends IInstanceOp {

        public static final int OPCODE = 0x5f;

        static void init() {
            InstructionReader.register(OPCODE, new Reader_22c_22cs((context, A, B, C) -> {
                return new IPutShort(A, B, context.field(C));
            }));
        }

        public IPutShort(int A, int B, FieldId C) {
            super(A, B, C);
        }

        @Override
        public int opcode() {
            return OPCODE;
        }

        @Override
        public String name() {
            return "iput-short";
        }

        @Override
        public IPutShort clone() {
            return new IPutShort(value_register_or_pair,
                    object_register, instance_field);
        }
    }
}
