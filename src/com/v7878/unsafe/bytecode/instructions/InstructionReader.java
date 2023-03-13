package com.v7878.unsafe.bytecode.instructions;

import com.v7878.unsafe.bytecode.ReadContext;
import com.v7878.unsafe.io.RandomInput;

public abstract class InstructionReader {

    private static final InstructionReader[] readers = new InstructionReader[256];

    static void register(int opcode, InstructionReader reader) {
        if (readers[opcode] != null) {
            throw new IllegalArgumentException("opcode " + opcode + " already registered");
        }
        readers[opcode] = reader;
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

        // TODO: 24-26
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
        // TODO: 52-5f
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
    }

    public static Instruction read(RandomInput in, ReadContext rc) {
        int code = in.readUnsignedShort();
        InstructionReader reader = readers[code & 0xff];
        if (reader == null) {
            throw new IllegalArgumentException("unknown opcode " + (code & 0xff));
        }
        return reader.read(in, rc, code >> 8);
    }

    abstract Instruction read(RandomInput in, ReadContext rc, int arg);

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
        Instruction read(RandomInput in, ReadContext rc, int _00) {
            return factory.make();
        }
    }

    static class Reader_12x_11n extends InstructionReader {

        @FunctionalInterface
        public interface Factory {

            public Instruction make(int A, int B);
        }

        public final Factory factory;

        public Reader_12x_11n(Factory factory) {
            this.factory = factory;
        }

        @Override
        Instruction read(RandomInput in, ReadContext rc, int BA) {
            return factory.make(BA & 0xf, BA >> 4);
        }
    }

    static class Reader_11x_10t extends InstructionReader {

        @FunctionalInterface
        public interface Factory {

            public Instruction make(int AA);
        }

        public final Factory factory;

        public Reader_11x_10t(Factory factory) {
            this.factory = factory;
        }

        @Override
        Instruction read(RandomInput in, ReadContext rc, int AA) {
            return factory.make(AA);
        }
    }

    static class Reader_20t extends InstructionReader {

        @FunctionalInterface
        public interface Factory {

            public Instruction make(int AAAA);
        }

        public final Factory factory;

        public Reader_20t(Factory factory) {
            this.factory = factory;
        }

        @Override
        Instruction read(RandomInput in, ReadContext rc, int _00) {
            int AAAA = in.readUnsignedShort();
            return factory.make(AAAA);
        }
    }

    static class Reader_20bc_21c extends InstructionReader {

        @FunctionalInterface
        public interface Factory {

            public Instruction make(ReadContext rc, int AA, int BBBB);
        }

        public final Factory factory;

        public Reader_20bc_21c(Factory factory) {
            this.factory = factory;
        }

        @Override
        Instruction read(RandomInput in, ReadContext rc, int AA) {
            int BBBB = in.readUnsignedShort();
            return factory.make(rc, AA, BBBB);
        }
    }

    static class Reader_22x_21t_21s_21h extends InstructionReader {

        @FunctionalInterface
        public interface Factory {

            public Instruction make(int AA, int BBBB);
        }

        public final Factory factory;

        public Reader_22x_21t_21s_21h(Factory factory) {
            this.factory = factory;
        }

        @Override
        Instruction read(RandomInput in, ReadContext rc, int AA) {
            int BBBB = in.readUnsignedShort();
            return factory.make(AA, BBBB);
        }
    }

    static class Reader_23x_22b extends InstructionReader {

        @FunctionalInterface
        public interface Factory {

            public Instruction make(int AA, int BB, int CC);
        }

        public final Factory factory;

        public Reader_23x_22b(Factory factory) {
            this.factory = factory;
        }

        @Override
        Instruction read(RandomInput in, ReadContext rc, int AA) {
            int CCBB = in.readUnsignedShort();
            return factory.make(AA, CCBB & 0xff, CCBB >> 8);
        }
    }

    static class Reader_22t_22s extends InstructionReader {

        @FunctionalInterface
        public interface Factory {

            public Instruction make(int A, int B, int CCCC);
        }

        public final Factory factory;

        public Reader_22t_22s(Factory factory) {
            this.factory = factory;
        }

        @Override
        Instruction read(RandomInput in, ReadContext rc, int BA) {
            int CCCC = in.readUnsignedShort();
            return factory.make(BA & 0xf, BA >> 4, CCCC);
        }
    }

    static class Reader_22c_22cs extends InstructionReader {

        @FunctionalInterface
        public interface Factory {

            public Instruction make(ReadContext rc, int A, int B, int CCCC);
        }

        public final Factory factory;

        public Reader_22c_22cs(Factory factory) {
            this.factory = factory;
        }

        @Override
        Instruction read(RandomInput in, ReadContext rc, int BA) {
            int CCCC = in.readUnsignedShort();
            return factory.make(rc, BA & 0xf, BA >> 4, CCCC);
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
        Instruction read(RandomInput in, ReadContext rc, int _00) {
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
        Instruction read(RandomInput in, ReadContext rc, int _00) {
            int AAAA = in.readUnsignedShort();
            int BBBB = in.readUnsignedShort();
            return factory.make(AAAA, BBBB);
        }
    }

    static class Reader_31i_31t extends InstructionReader {

        @FunctionalInterface
        public interface Factory {

            public Instruction make(int AA, int BBBBBBBB);
        }

        public final Factory factory;

        public Reader_31i_31t(Factory factory) {
            this.factory = factory;
        }

        @Override
        Instruction read(RandomInput in, ReadContext rc, int AA) {
            int BBBBlo = in.readUnsignedShort();
            int BBBBhi = in.readUnsignedShort();
            return factory.make(AA, BBBBlo | (BBBBhi << 16));
        }
    }

    static class Reader_31c extends InstructionReader {

        @FunctionalInterface
        public interface Factory {

            public Instruction make(ReadContext rc, int AA, int BBBBBBBB);
        }

        public final Factory factory;

        public Reader_31c(Factory factory) {
            this.factory = factory;
        }

        @Override
        Instruction read(RandomInput in, ReadContext rc, int AA) {
            int BBBBlo = in.readUnsignedShort();
            int BBBBhi = in.readUnsignedShort();
            return factory.make(rc, AA, BBBBlo | (BBBBhi << 16));
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
        Instruction read(RandomInput in, ReadContext rc, int AA) {
            long BBBBlolo = in.readUnsignedShort();
            long BBBBhilo = in.readUnsignedShort();
            long BBBBlohi = in.readUnsignedShort();
            long BBBBhihi = in.readUnsignedShort();
            return factory.make(AA, (BBBBhihi << 48) | (BBBBlohi << 32) | (BBBBlohi << 16) | BBBBlolo);
        }
    }

    static class Reader_35c_35ms_35mi extends InstructionReader {

        @FunctionalInterface
        public interface Factory {

            public Instruction make(ReadContext rc, int A, int BBBB, int C, int D, int E, int F, int G);
        }

        public final Factory factory;

        public Reader_35c_35ms_35mi(Factory factory) {
            this.factory = factory;
        }

        @Override
        Instruction read(RandomInput in, ReadContext rc, int AG) {
            int A = AG >> 4;
            int G = AG & 0xf;
            int BBBB = in.readUnsignedShort();
            int FEDC = in.readUnsignedShort();
            int F = FEDC >> 12;
            int E = (FEDC >> 8) & 0xf;
            int D = (FEDC >> 4) & 0xf;
            int C = FEDC & 0xf;
            return factory.make(rc, A, BBBB, C, D, E, F, G);
        }
    }
}
