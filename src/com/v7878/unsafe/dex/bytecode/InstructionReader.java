package com.v7878.unsafe.dex.bytecode;

import static com.v7878.unsafe.Utils.*;
import com.v7878.unsafe.dex.ReadContext;
import com.v7878.unsafe.io.RandomInput;

public abstract class InstructionReader {

    private static final InstructionReader[] readers = new InstructionReader[256];
    private static final InstructionReader[] extraReaders = new InstructionReader[256];

    static void register(int opcode, InstructionReader reader) {
        if (readers[opcode] != null) {
            throw new IllegalArgumentException("opcode " + opcode + " already registered");
        }
        readers[opcode] = reader;
    }

    static void registerExtra(int opcode, InstructionReader reader) {
        if (extraReaders[opcode] != null) {
            throw new IllegalArgumentException("extra opcode " + opcode + " already registered");
        }
        extraReaders[opcode] = reader;
    }

    static {
        Nop.init();                                 // 0x00

        Move.init();                                // 0x01
        MoveFrom16.init();                          // 0x02
        Move16.init();                              // 0x03

        MoveWide.init();                            // 0x04
        MoveWideFrom16.init();                      // 0x05
        MoveWide16.init();                          // 0x06

        MoveObject.init();                          // 0x07
        MoveObjectFrom16.init();                    // 0x08
        MoveObject16.init();                        // 0x09

        MoveResult.init();                          // 0x0a
        MoveResultWide.init();                      // 0x0b
        MoveResultObject.init();                    // 0x0c
        MoveException.init();                       // 0x0d

        ReturnVoid.init();                          // 0x0e
        Return.init();                              // 0x0f
        ReturnWide.init();                          // 0x10
        ReturnObject.init();                        // 0x11

        Const4.init();                              // 0x12
        Const16.init();                             // 0x13
        Const.init();                               // 0x14
        ConstHigh16.init();                         // 0x15

        ConstWide16.init();                         // 0x16
        ConstWide32.init();                         // 0x17
        ConstWide.init();                           // 0x18
        ConstWideHigh16.init();                     // 0x19

        ConstString.init();                         // 0x1a
        ConstStringJumbo.init();                    // 0x1b
        ConstClass.init();                          // 0x1c

        MonitorEnter.init();                        // 0x1d
        MonitorExit.init();                         // 0x1e

        CheckCast.init();                           // 0x1f
        InstanceOf.init();                          // 0x20

        ArrayLength.init();                         // 0x21

        NewInstance.init();                         // 0x22

        NewArray.init();                            // 0x23

        // TODO: 24-25
        FillArrayData.init();                       // 0x26

        Throw.init();                               // 0x27

        // TODO: branch offset to instruction offset
        Goto.init();                                // 0x28
        Goto16.init();                              // 0x29
        Goto32.init();                              // 0x2a

        // TODO: 2b-2c
        CmpKind.init();                             // 0x2d-31

        // TODO: branch offset to instruction offset
        IfTest.init();                              // 0x32-37
        IfTestZ.init();                             // 0x38-3d

        // <unused> 3e-43
        // TODO: 44-51
        IInstanceOp.init();                         // 0x52-5f
        SStaticOp.init();                           // 0x60-6d
        InvokeKind.init();                          // 0x6e-72

        // <unused> 73
        // TODO: 74-78
        // <unused> 79-7a
        // TODO: 7b-8f
        // TODO: 90-af
        // TODO: b0-cf
        // TODO: d0-d7
        // TODO: d8-e2
        // <unused> e3-f9
        // TODO: fa-fd
        // TODO: fe-ff
        // TODO: extra 01-02
        FillArrayDataPayload.init();                // extra 0x03
    }

    private static int extend_sign(int value, int width) {
        int shift = 32 - width;
        return (value << shift) >> shift;
    }

    private static long extend_sign64(long value, int width) {
        int shift = 64 - width;
        return (value << shift) >> shift;
    }

    public static Instruction read(RandomInput in, ReadContext context) {
        int code = in.readUnsignedShort();

        int opcode = code & 0xff;
        int arg = code >> 8;
        boolean is_extra = false;

        InstructionReader reader;
        if (opcode == 0x00 && arg != 0) {
            opcode = arg;
            arg = 0;
            reader = extraReaders[opcode];
            is_extra = true;
        } else {
            reader = readers[opcode];
        }

        if (reader == null) {
            throw new IllegalArgumentException("unknown " + (is_extra ? "extra " : "") + "opcode " + opcode);
        }

        Instruction out = reader.read(in, context, arg);
        assert_(out.opcode() == opcode, IllegalStateException::new,
                "opcode != readed.opcode()");
        return out;
    }

    abstract Instruction read(RandomInput in, ReadContext context, int arg);

    static class Reader_10x extends InstructionReader {

        @FunctionalInterface
        public interface Factory {

            public Instruction make();
        }

        public final Factory factory;

        public Reader_10x(Factory factory) {
            this.factory = factory;
        }

        @Override
        Instruction read(RandomInput in, ReadContext context, int _00) {
            return factory.make();
        }
    }

    static class Reader_12x extends InstructionReader {

        @FunctionalInterface
        public interface Factory {

            public Instruction make(int A, int B);
        }

        public final Factory factory;

        public Reader_12x(Factory factory) {
            this.factory = factory;
        }

        @Override
        Instruction read(RandomInput in, ReadContext context, int BA) {
            return factory.make(BA & 0xf, BA >> 4);
        }
    }

    static class Reader_11n extends InstructionReader {

        @FunctionalInterface
        public interface Factory {

            public Instruction make(int A, int sB);
        }

        public final Factory factory;

        public Reader_11n(Factory factory) {
            this.factory = factory;
        }

        @Override
        Instruction read(RandomInput in, ReadContext context, int BA) {
            return factory.make(BA & 0xf, extend_sign(BA >> 4, 4));
        }
    }

    static class Reader_11x extends InstructionReader {

        @FunctionalInterface
        public interface Factory {

            public Instruction make(int AA);
        }

        public final Factory factory;

        public Reader_11x(Factory factory) {
            this.factory = factory;
        }

        @Override
        Instruction read(RandomInput in, ReadContext context, int AA) {
            return factory.make(AA);
        }
    }

    static class Reader_10t extends InstructionReader {

        @FunctionalInterface
        public interface Factory {

            public Instruction make(int sAA);
        }

        public final Factory factory;

        public Reader_10t(Factory factory) {
            this.factory = factory;
        }

        @Override
        Instruction read(RandomInput in, ReadContext context, int AA) {
            return factory.make(extend_sign(AA, 8));
        }
    }

    static class Reader_20t extends InstructionReader {

        @FunctionalInterface
        public interface Factory {

            public Instruction make(int sAAAA);
        }

        public final Factory factory;

        public Reader_20t(Factory factory) {
            this.factory = factory;
        }

        @Override
        Instruction read(RandomInput in, ReadContext context, int _00) {
            int AAAA = in.readUnsignedShort();
            return factory.make(extend_sign(AAAA, 16));
        }
    }

    static class Reader_20bc_21c extends InstructionReader {

        @FunctionalInterface
        public interface Factory {

            public Instruction make(ReadContext context, int AA, int BBBB);
        }

        public final Factory factory;

        public Reader_20bc_21c(Factory factory) {
            this.factory = factory;
        }

        @Override
        Instruction read(RandomInput in, ReadContext context, int AA) {
            int BBBB = in.readUnsignedShort();
            return factory.make(context, AA, BBBB);
        }
    }

    static class Reader_22x extends InstructionReader {

        @FunctionalInterface
        public interface Factory {

            public Instruction make(int AA, int BBBB);
        }

        public final Factory factory;

        public Reader_22x(Factory factory) {
            this.factory = factory;
        }

        @Override
        Instruction read(RandomInput in, ReadContext context, int AA) {
            int BBBB = in.readUnsignedShort();
            return factory.make(AA, BBBB);
        }
    }

    static class Reader_21t_21s32 extends InstructionReader {

        @FunctionalInterface
        public interface Factory {

            public Instruction make(int AA, int sBBBB);
        }

        public final Factory factory;

        public Reader_21t_21s32(Factory factory) {
            this.factory = factory;
        }

        @Override
        Instruction read(RandomInput in, ReadContext context, int AA) {
            int BBBB = in.readUnsignedShort();
            return factory.make(AA, extend_sign(BBBB, 16));
        }
    }

    static class Reader_21s64 extends InstructionReader {

        @FunctionalInterface
        public interface Factory {

            public Instruction make(int AA, long sBBBB);
        }

        public final Factory factory;

        public Reader_21s64(Factory factory) {
            this.factory = factory;
        }

        @Override
        Instruction read(RandomInput in, ReadContext context, int AA) {
            int BBBB = in.readUnsignedShort();
            return factory.make(AA, extend_sign64(BBBB, 16));
        }
    }

    static class Reader_21h32 extends InstructionReader {

        @FunctionalInterface
        public interface Factory {

            public Instruction make(int AA, int BBBB0000);
        }

        public final Factory factory;

        public Reader_21h32(Factory factory) {
            this.factory = factory;
        }

        @Override
        Instruction read(RandomInput in, ReadContext context, int AA) {
            int BBBB = in.readUnsignedShort();
            return factory.make(AA, BBBB << 16);
        }
    }

    static class Reader_21h64 extends InstructionReader {

        @FunctionalInterface
        public interface Factory {

            public Instruction make(int AA, long BBBB000000000000);
        }

        public final Factory factory;

        public Reader_21h64(Factory factory) {
            this.factory = factory;
        }

        @Override
        Instruction read(RandomInput in, ReadContext context, int AA) {
            long BBBB = in.readUnsignedShort();
            return factory.make(AA, BBBB << 48);
        }
    }

    static class Reader_23x extends InstructionReader {

        @FunctionalInterface
        public interface Factory {

            public Instruction make(int AA, int BB, int CC);
        }

        public final Factory factory;

        public Reader_23x(Factory factory) {
            this.factory = factory;
        }

        @Override
        Instruction read(RandomInput in, ReadContext context, int AA) {
            int CCBB = in.readUnsignedShort();
            return factory.make(AA, CCBB & 0xff, CCBB >> 8);
        }
    }

    static class Reader_22b extends InstructionReader {

        @FunctionalInterface
        public interface Factory {

            public Instruction make(int AA, int BB, int sCC);
        }

        public final Factory factory;

        public Reader_22b(Factory factory) {
            this.factory = factory;
        }

        @Override
        Instruction read(RandomInput in, ReadContext context, int AA) {
            int CCBB = in.readUnsignedShort();
            return factory.make(AA, CCBB & 0xff, extend_sign(CCBB >> 8, 8));
        }
    }

    static class Reader_22t_22s extends InstructionReader {

        @FunctionalInterface
        public interface Factory {

            public Instruction make(int A, int B, int sCCCC);
        }

        public final Factory factory;

        public Reader_22t_22s(Factory factory) {
            this.factory = factory;
        }

        @Override
        Instruction read(RandomInput in, ReadContext context, int BA) {
            int CCCC = in.readUnsignedShort();
            return factory.make(BA & 0xf, BA >> 4, extend_sign(CCCC, 16));
        }
    }

    static class Reader_22c_22cs extends InstructionReader {

        @FunctionalInterface
        public interface Factory {

            public Instruction make(ReadContext context, int A, int B, int CCCC);
        }

        public final Factory factory;

        public Reader_22c_22cs(Factory factory) {
            this.factory = factory;
        }

        @Override
        Instruction read(RandomInput in, ReadContext context, int BA) {
            int CCCC = in.readUnsignedShort();
            return factory.make(context, BA & 0xf, BA >> 4, CCCC);
        }
    }

    static class Reader_30t extends InstructionReader {

        @FunctionalInterface
        public interface Factory {

            public Instruction make(int AAAAAAAA);
        }

        public final Factory factory;

        public Reader_30t(Factory factory) {
            this.factory = factory;
        }

        @Override
        Instruction read(RandomInput in, ReadContext context, int _00) {
            int AAAAlo = in.readUnsignedShort();
            int AAAAhi = in.readUnsignedShort();
            return factory.make(AAAAlo | (AAAAhi << 16));
        }
    }

    static class Reader_32x extends InstructionReader {

        @FunctionalInterface
        public interface Factory {

            public Instruction make(int AAAA, int BBBB);
        }

        public final Factory factory;

        public Reader_32x(Factory factory) {
            this.factory = factory;
        }

        @Override
        Instruction read(RandomInput in, ReadContext context, int _00) {
            int AAAA = in.readUnsignedShort();
            int BBBB = in.readUnsignedShort();
            return factory.make(AAAA, BBBB);
        }
    }

    static class Reader_31i32_31t extends InstructionReader {

        @FunctionalInterface
        public interface Factory {

            public Instruction make(int AA, int BBBBBBBB);
        }

        public final Factory factory;

        public Reader_31i32_31t(Factory factory) {
            this.factory = factory;
        }

        @Override
        Instruction read(RandomInput in, ReadContext context, int AA) {
            int BBBBlo = in.readUnsignedShort();
            int BBBBhi = in.readUnsignedShort();
            return factory.make(AA, BBBBlo | (BBBBhi << 16));
        }
    }

    static class Reader_31i64 extends InstructionReader {

        @FunctionalInterface
        public interface Factory {

            public Instruction make(int AA, long sBBBBBBBB);
        }

        public final Factory factory;

        public Reader_31i64(Factory factory) {
            this.factory = factory;
        }

        @Override
        Instruction read(RandomInput in, ReadContext context, int AA) {
            int BBBBlo = in.readUnsignedShort();
            int BBBBhi = in.readUnsignedShort();
            return factory.make(AA, extend_sign64(BBBBlo | (BBBBhi << 16), 32));
        }
    }

    static class Reader_31c extends InstructionReader {

        @FunctionalInterface
        public interface Factory {

            public Instruction make(ReadContext context, int AA, int BBBBBBBB);
        }

        public final Factory factory;

        public Reader_31c(Factory factory) {
            this.factory = factory;
        }

        @Override
        Instruction read(RandomInput in, ReadContext context, int AA) {
            int BBBBlo = in.readUnsignedShort();
            int BBBBhi = in.readUnsignedShort();
            return factory.make(context, AA, BBBBlo | (BBBBhi << 16));
        }
    }

    static class Reader_51l extends InstructionReader {

        @FunctionalInterface
        public interface Factory {

            public Instruction make(int AA, long BBBBBBBBBBBBBBBB);
        }

        public final Factory factory;

        public Reader_51l(Factory factory) {
            this.factory = factory;
        }

        @Override
        Instruction read(RandomInput in, ReadContext context, int AA) {
            long BBBBlolo = in.readUnsignedShort();
            long BBBBhilo = in.readUnsignedShort();
            long BBBBlohi = in.readUnsignedShort();
            long BBBBhihi = in.readUnsignedShort();
            return factory.make(AA, (BBBBhihi << 48) | (BBBBlohi << 32) | (BBBBhilo << 16) | BBBBlolo);
        }
    }

    static class Reader_35c_35ms_35mi extends InstructionReader {

        @FunctionalInterface
        public interface Factory {

            public Instruction make(ReadContext context, int A, int BBBB, int C, int D, int E, int F, int G);
        }

        public final Factory factory;

        public Reader_35c_35ms_35mi(Factory factory) {
            this.factory = factory;
        }

        @Override
        Instruction read(RandomInput in, ReadContext context, int AG) {
            int A = AG >> 4;
            int G = AG & 0xf;
            int BBBB = in.readUnsignedShort();
            int FEDC = in.readUnsignedShort();
            int F = FEDC >> 12;
            int E = (FEDC >> 8) & 0xf;
            int D = (FEDC >> 4) & 0xf;
            int C = FEDC & 0xf;
            return factory.make(context, A, BBBB, C, D, E, F, G);
        }
    }

    static class Reader_3rc_3rms_3rmi extends InstructionReader {

        @FunctionalInterface
        public interface Factory {

            public Instruction make(ReadContext context, int AA, int BBBB, int CCCC);
        }

        public final Factory factory;

        public Reader_3rc_3rms_3rmi(Factory factory) {
            this.factory = factory;
        }

        @Override
        Instruction read(RandomInput in, ReadContext context, int AA) {
            int BBBB = in.readUnsignedShort();
            int CCCC = in.readUnsignedShort();
            return factory.make(context, AA, BBBB, CCCC);
        }
    }

    static class Reader_fill_array_data_payload extends InstructionReader {

        @FunctionalInterface
        public interface Factory {

            public Instruction make(int element_width, byte[] data);
        }

        public final Factory factory;

        public Reader_fill_array_data_payload(Factory factory) {
            this.factory = factory;
        }

        @Override
        Instruction read(RandomInput in, ReadContext context, int _00) {
            int element_width = in.readUnsignedShort();
            int size = in.readInt();
            byte[] data = in.readByteArray(size * element_width);
            if ((size & 1) != 0) {
                in.readByte(); // padding
            }
            return factory.make(element_width, data);
        }
    }
}
