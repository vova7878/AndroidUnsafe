package com.v7878.unsafe.dex.bytecode2;

import com.v7878.unsafe.dex.DataCollector;
import com.v7878.unsafe.dex.ReadContext;
import com.v7878.unsafe.dex.WriteContext;
import com.v7878.unsafe.io.RandomInput;
import com.v7878.unsafe.io.RandomOutput;

import java.util.Objects;

public abstract class Format {
    protected final Opcode opcode;
    private int units;
    private boolean payload;

    Format(Opcode opcode, int units) {
        this(opcode, units, false);
    }

    Format(Opcode opcode, int units, boolean payload) {
        this.opcode = opcode;
        this.units = units;
        this.payload = payload;
    }

    public Opcode opcode() {
        return opcode;
    }

    public int units() {
        return units;
    }

    public boolean isPayload() {
        return payload;
    }

    public abstract Instruction read(RandomInput in, ReadContext context, int arg);

    private static int extend_sign(int value, int width) {
        int shift = 32 - width;
        return (value << shift) >> shift;
    }

    private static long extend_sign64(long value, int width) {
        int shift = 64 - width;
        return (value << shift) >> shift;
    }

    public static class Format10x extends Format {

        public class Instance extends Instruction {

            Instance() {
            }

            @Override
            public void write(WriteContext context, RandomOutput out) {
                InstructionWriter.write_10x(out, opcode2().opcodeValue(context.getOptions()));
            }

            @Override
            public Opcode opcode2() {
                return opcode;
            }

            @Override
            public String toString() {
                return opcode2().opname();
            }

            @Override
            public boolean equals(Object obj) {
                if (obj instanceof Instance) {
                    Instance iobj = (Instance) obj;
                    return Objects.equals(opcode2(), iobj.opcode2());
                }
                return false;
            }

            @Override
            public int hashCode() {
                return Objects.hash(opcode2());
            }

            @Override
            public Instruction clone() {
                return new Instance();
            }
        }

        Format10x(Opcode opcode) {
            super(opcode, 1);
        }

        @Override
        public Instruction read(RandomInput in, ReadContext context, int _00) {
            return make();
        }

        public Instruction make() {
            return new Instance();
        }
    }

    public static class Format12x extends Format {

        public class Instance extends Instruction {

            public final int A, B;

            Instance(int A, int B) {
                this.A = A;
                this.B = B;
            }

            @Override
            public void write(WriteContext context, RandomOutput out) {
                InstructionWriter.write_12x(out, opcode2().opcodeValue(context.getOptions()), A, B);
            }

            @Override
            public Opcode opcode2() {
                return opcode;
            }

            @Override
            public String toString() {
                return opcode2().opname() + " " + A + " " + B;
            }

            @Override
            public boolean equals(Object obj) {
                if (obj instanceof Instance) {
                    Instance iobj = (Instance) obj;
                    return Objects.equals(opcode2(), iobj.opcode2())
                            && A == iobj.A && B == iobj.B;
                }
                return false;
            }

            @Override
            public int hashCode() {
                return Objects.hash(opcode2(), A, B);
            }

            @Override
            public Instruction clone() {
                return new Instance(A, B);
            }
        }

        Format12x(Opcode opcode) {
            super(opcode, 1);
        }

        @Override
        public Instruction read(RandomInput in, ReadContext context, int BA) {
            return make(BA & 0xf, BA >> 4);
        }

        public Instruction make(int A, int B) {
            return new Instance(A, B);
        }
    }

    public static class Format11n extends Format {

        public class Instance extends Instruction {

            public final int A, sB;

            Instance(int A, int sB) {
                this.A = A;
                this.sB = sB;
            }

            @Override
            public void write(WriteContext context, RandomOutput out) {
                InstructionWriter.write_11n(out, opcode2().opcodeValue(context.getOptions()), A, sB);
            }

            @Override
            public Opcode opcode2() {
                return opcode;
            }

            @Override
            public String toString() {
                return opcode2().opname() + " " + A + " " + sB;
            }

            @Override
            public boolean equals(Object obj) {
                if (obj instanceof Instance) {
                    Instance iobj = (Instance) obj;
                    return Objects.equals(opcode2(), iobj.opcode2())
                            && A == iobj.A && sB == iobj.sB;
                }
                return false;
            }

            @Override
            public int hashCode() {
                return Objects.hash(opcode2(), A, sB);
            }

            @Override
            public Instruction clone() {
                return new Instance(A, sB);
            }
        }

        Format11n(Opcode opcode) {
            super(opcode, 1);
        }

        @Override
        public Instruction read(RandomInput in, ReadContext context, int BA) {
            return make(BA & 0xf, extend_sign(BA >> 4, 4));
        }

        public Instruction make(int A, int sB) {
            return new Instance(A, sB);
        }
    }

    public static class Format11x extends Format {

        public class Instance extends Instruction {

            public final int AA;

            Instance(int AA) {
                this.AA = AA;
            }

            @Override
            public void write(WriteContext context, RandomOutput out) {
                InstructionWriter.write_11x(out, opcode2().opcodeValue(context.getOptions()), AA);
            }

            @Override
            public Opcode opcode2() {
                return opcode;
            }

            @Override
            public String toString() {
                return opcode2().opname() + " " + AA;
            }

            @Override
            public boolean equals(Object obj) {
                if (obj instanceof Instance) {
                    Instance iobj = (Instance) obj;
                    return Objects.equals(opcode2(), iobj.opcode2())
                            && AA == iobj.AA;
                }
                return false;
            }

            @Override
            public int hashCode() {
                return Objects.hash(opcode2(), AA);
            }

            @Override
            public Instruction clone() {
                return new Instance(AA);
            }
        }

        Format11x(Opcode opcode) {
            super(opcode, 1);
        }

        @Override
        public Instruction read(RandomInput in, ReadContext context, int AA) {
            return make(AA);
        }

        public Instruction make(int AA) {
            return new Instance(AA);
        }
    }

    public static class Format10t extends Format {

        public class Instance extends Instruction {

            public final int sAA;

            Instance(int sAA) {
                this.sAA = sAA;
            }

            @Override
            public void write(WriteContext context, RandomOutput out) {
                InstructionWriter.write_10t(out, opcode2().opcodeValue(context.getOptions()), sAA);
            }

            @Override
            public Opcode opcode2() {
                return opcode;
            }

            @Override
            public String toString() {
                return opcode2().opname() + " " + sAA;
            }

            @Override
            public boolean equals(Object obj) {
                if (obj instanceof Instance) {
                    Instance iobj = (Instance) obj;
                    return Objects.equals(opcode2(), iobj.opcode2())
                            && sAA == iobj.sAA;
                }
                return false;
            }

            @Override
            public int hashCode() {
                return Objects.hash(opcode2(), sAA);
            }

            @Override
            public Instruction clone() {
                return new Instance(sAA);
            }
        }

        Format10t(Opcode opcode) {
            super(opcode, 1);
        }

        @Override
        public Instruction read(RandomInput in, ReadContext context, int AA) {
            return make(extend_sign(AA, 8));
        }

        public Instruction make(int sAA) {
            return new Instance(sAA);
        }
    }

    public static class Format22x extends Format {

        public class Instance extends Instruction {

            public final int AA, BBBB;

            Instance(int AA, int BBBB) {
                this.AA = AA;
                this.BBBB = BBBB;
            }

            @Override
            public void write(WriteContext context, RandomOutput out) {
                InstructionWriter.write_22x_21c(out,
                        opcode2().opcodeValue(context.getOptions()), AA, BBBB);
            }

            @Override
            public Opcode opcode2() {
                return opcode;
            }

            @Override
            public String toString() {
                return opcode2().opname() + " " + AA + " " + BBBB;
            }

            @Override
            public boolean equals(Object obj) {
                if (obj instanceof Instance) {
                    Instance iobj = (Instance) obj;
                    return Objects.equals(opcode2(), iobj.opcode2())
                            && AA == iobj.AA && BBBB == iobj.BBBB;
                }
                return false;
            }

            @Override
            public int hashCode() {
                return Objects.hash(opcode2(), AA, BBBB);
            }

            @Override
            public Instruction clone() {
                return new Instance(AA, BBBB);
            }
        }

        Format22x(Opcode opcode) {
            super(opcode, 2);
        }

        @Override
        public Instruction read(RandomInput in, ReadContext context, int AA) {
            int BBBB = in.readUnsignedShort();
            return make(AA, BBBB);
        }

        public Instruction make(int AA, int BBBB) {
            return new Instance(AA, BBBB);
        }
    }

    public static class Format21c extends Format {
        public final ReferenceType referenceType;

        public class Instance extends Instruction {

            public final int AA;
            public final Object cBBBB;

            Instance(int AA, Object cBBBB) {
                this.AA = AA;
                this.cBBBB = referenceType.clone(cBBBB);
            }

            @Override
            public void collectData(DataCollector data) {
                referenceType.collectData(data, cBBBB);
            }

            @Override
            public void write(WriteContext context, RandomOutput out) {
                InstructionWriter.write_22x_21c(out,
                        opcode2().opcodeValue(context.getOptions()), AA,
                        referenceType.refToIndex(context, cBBBB));
            }

            @Override
            public Opcode opcode2() {
                return opcode;
            }

            @Override
            public String toString() {
                return opcode2().opname() + " " + AA + " " + cBBBB;
            }

            @Override
            public boolean equals(Object obj) {
                if (obj instanceof Instance) {
                    Instance iobj = (Instance) obj;
                    return Objects.equals(opcode2(), iobj.opcode2())
                            && AA == iobj.AA && Objects.equals(cBBBB, iobj.cBBBB);
                }
                return false;
            }

            @Override
            public int hashCode() {
                return Objects.hash(opcode2(), AA, cBBBB);
            }

            @Override
            public Instruction clone() {
                return new Instance(AA, referenceType.clone(cBBBB));
            }
        }

        Format21c(Opcode opcode, ReferenceType referenceType) {
            super(opcode, 2);
            this.referenceType = referenceType;
        }

        @Override
        public Instruction read(RandomInput in, ReadContext context, int AA) {
            int BBBB = in.readUnsignedShort();
            return make(AA, referenceType.indexToRef(context, BBBB));
        }

        public Instruction make(int AA, Object cBBBB) {
            return new Instance(AA, cBBBB);
        }
    }

    public static class Format22c extends Format {

        public final ReferenceType referenceType;

        public class Instance extends Instruction {

            public final int A, B;
            public final Object cCCCC;

            Instance(int A, int B, Object cCCCC) {
                this.A = A;
                this.B = B;
                this.cCCCC = referenceType.clone(cCCCC);
            }

            @Override
            public void collectData(DataCollector data) {
                referenceType.collectData(data, cCCCC);
            }

            @Override
            public void write(WriteContext context, RandomOutput out) {
                InstructionWriter.write_22c(out,
                        opcode2().opcodeValue(context.getOptions()), A, B,
                        referenceType.refToIndex(context, cCCCC));
            }

            @Override
            public Opcode opcode2() {
                return opcode;
            }

            @Override
            public String toString() {
                return opcode2().opname() + " " + A + " " + B + " " + cCCCC;
            }

            @Override
            public boolean equals(Object obj) {
                if (obj instanceof Instance) {
                    Instance iobj = (Instance) obj;
                    return Objects.equals(opcode2(), iobj.opcode2())
                            && A == iobj.A && B == iobj.B
                            && Objects.equals(cCCCC, iobj.cCCCC);
                }
                return false;
            }

            @Override
            public int hashCode() {
                return Objects.hash(opcode2(), A, B, cCCCC);
            }

            @Override
            public Instruction clone() {
                return new Instance(A, B, cCCCC);
            }
        }

        Format22c(Opcode opcode, ReferenceType referenceType) {
            super(opcode, 2);
            this.referenceType = referenceType;
        }

        @Override
        public Instruction read(RandomInput in, ReadContext context, int BA) {
            int CCCC = in.readUnsignedShort();
            return make(BA & 0xf, BA >> 4, referenceType.indexToRef(context, CCCC));
        }

        public Instruction make(int A, int B, Object cCCCC) {
            return new Instance(A, B, cCCCC);
        }
    }

    public static class Format23x extends Format {

        public class Instance extends Instruction {

            public final int AA, BB, CC;

            Instance(int AA, int BB, int CC) {
                this.AA = AA;
                this.BB = BB;
                this.CC = CC;
            }

            @Override
            public void write(WriteContext context, RandomOutput out) {
                InstructionWriter.write_23x(out,
                        opcode2().opcodeValue(context.getOptions()), AA, BB, CC);
            }

            @Override
            public Opcode opcode2() {
                return opcode;
            }

            @Override
            public String toString() {
                return opcode2().opname() + " " + AA + " " + BB + " " + CC;
            }

            @Override
            public boolean equals(Object obj) {
                if (obj instanceof Instance) {
                    Instance iobj = (Instance) obj;
                    return Objects.equals(opcode2(), iobj.opcode2())
                            && AA == iobj.AA && BB == iobj.BB && CC == iobj.CC;
                }
                return false;
            }

            @Override
            public int hashCode() {
                return Objects.hash(opcode2(), AA, BB, CC);
            }

            @Override
            public Instruction clone() {
                return new Instance(AA, BB, CC);
            }
        }

        Format23x(Opcode opcode) {
            super(opcode, 2);
        }

        @Override
        public Instruction read(RandomInput in, ReadContext context, int AA) {
            int CCBB = in.readUnsignedShort();
            return make(AA, CCBB & 0xff, CCBB >> 8);
        }

        public Instruction make(int AA, int BB, int CC) {
            return new Instance(AA, BB, CC);
        }
    }

    public static class Format32x extends Format {

        public class Instance extends Instruction {

            public final int AAAA, BBBB;

            Instance(int AAAA, int BBBB) {
                this.AAAA = AAAA;
                this.BBBB = BBBB;
            }

            @Override
            public void write(WriteContext context, RandomOutput out) {
                InstructionWriter.write_32x(out,
                        opcode2().opcodeValue(context.getOptions()), AAAA, BBBB);
            }

            @Override
            public Opcode opcode2() {
                return opcode;
            }

            @Override
            public String toString() {
                return opcode2().opname() + " " + AAAA + " " + BBBB;
            }

            @Override
            public boolean equals(Object obj) {
                if (obj instanceof Instance) {
                    Instance iobj = (Instance) obj;
                    return Objects.equals(opcode2(), iobj.opcode2())
                            && AAAA == iobj.AAAA && BBBB == iobj.BBBB;
                }
                return false;
            }

            @Override
            public int hashCode() {
                return Objects.hash(opcode2(), AAAA, BBBB);
            }

            @Override
            public Instruction clone() {
                return new Instance(AAAA, BBBB);
            }
        }

        Format32x(Opcode opcode) {
            super(opcode, 3);
        }

        @Override
        public Instruction read(RandomInput in, ReadContext context, int _00) {
            int AAAA = in.readUnsignedShort();
            int BBBB = in.readUnsignedShort();
            return make(AAAA, BBBB);
        }

        public Instruction make(int AAAA, int BBBB) {
            return new Instance(AAAA, BBBB);
        }
    }

    public static class Format35c extends Format {

        public final ReferenceType referenceType;

        public class Instance extends Instruction {

            public final int A, C, D, E, F, G;
            public final Object cBBBB;

            Instance(int A, Object cBBBB, int C, int D, int E, int F, int G) {
                this.A = A;
                this.cBBBB = referenceType.clone(cBBBB);
                this.C = C;
                this.D = D;
                this.E = E;
                this.F = F;
                this.G = G;
            }

            @Override
            public void collectData(DataCollector data) {
                referenceType.collectData(data, cBBBB);
            }

            @Override
            public void write(WriteContext context, RandomOutput out) {
                InstructionWriter.write_35c_35ms_35mi(out,
                        opcode2().opcodeValue(context.getOptions()), A,
                        referenceType.refToIndex(context, cBBBB), C, D, E, F, G);
            }

            @Override
            public Opcode opcode2() {
                return opcode;
            }

            @Override
            public String toString() {
                return opcode2().opname() + " " + A + " " + cBBBB
                        + " " + C + " " + D + " " + E + " " + F + " " + G;
            }

            @Override
            public boolean equals(Object obj) {
                if (obj instanceof Instance) {
                    Instance iobj = (Instance) obj;
                    return Objects.equals(opcode2(), iobj.opcode2())
                            && A == iobj.A && Objects.equals(cBBBB, iobj.cBBBB)
                            && C == iobj.C && D == iobj.D && E == iobj.E
                            && F == iobj.F && G == iobj.G;
                }
                return false;
            }

            @Override
            public int hashCode() {
                return Objects.hash(opcode2(), A, cBBBB, C, D, E, F, G);
            }

            @Override
            public Instruction clone() {
                return new Instance(A, cBBBB, C, D, E, F, G);
            }
        }

        Format35c(Opcode opcode, ReferenceType referenceType) {
            super(opcode, 3);
            this.referenceType = referenceType;
        }

        @Override
        public Instruction read(RandomInput in, ReadContext context, int AG) {
            int A = AG >> 4;
            int G = AG & 0xf;
            int BBBB = in.readUnsignedShort();
            int FEDC = in.readUnsignedShort();
            int F = FEDC >> 12;
            int E = (FEDC >> 8) & 0xf;
            int D = (FEDC >> 4) & 0xf;
            int C = FEDC & 0xf;
            return make(A, referenceType.indexToRef(context, BBBB), C, D, E, F, G);
        }

        public Instruction make(int A, Object cBBBB, int C, int D, int E, int F, int G) {
            return new Instance(A, cBBBB, C, D, E, F, G);
        }
    }

    public static class Format45cc extends Format {

        public final ReferenceType referenceType;
        public final ReferenceType referenceType2;

        public class Instance extends Instruction {

            public final int A, C, D, E, F, G;
            public final Object cBBBB, cHHHH;

            Instance(int A, Object cBBBB, int C, int D, int E, int F, int G, Object cHHHH) {
                this.A = A;
                this.cBBBB = referenceType.clone(cBBBB);
                this.C = C;
                this.D = D;
                this.E = E;
                this.F = F;
                this.G = G;
                this.cHHHH = referenceType2.clone(cHHHH);
            }

            @Override
            public void collectData(DataCollector data) {
                referenceType.collectData(data, cBBBB);
                referenceType2.collectData(data, cHHHH);
            }

            @Override
            public void write(WriteContext context, RandomOutput out) {
                InstructionWriter.write_45cc(out,
                        opcode2().opcodeValue(context.getOptions()), A,
                        referenceType.refToIndex(context, cBBBB), C, D, E, F, G,
                        referenceType2.refToIndex(context, cHHHH));
            }

            @Override
            public Opcode opcode2() {
                return opcode;
            }

            @Override
            public String toString() {
                return opcode2().opname() + " " + A + " " + cBBBB + " " + C
                        + " " + D + " " + E + " " + F + " " + G + " " + cHHHH;
            }

            @Override
            public boolean equals(Object obj) {
                if (obj instanceof Instance) {
                    Instance iobj = (Instance) obj;
                    return Objects.equals(opcode2(), iobj.opcode2())
                            && A == iobj.A && Objects.equals(cBBBB, iobj.cBBBB)
                            && C == iobj.C && D == iobj.D && E == iobj.E
                            && F == iobj.F && G == iobj.G && Objects.equals(cHHHH, iobj.cHHHH);
                }
                return false;
            }

            @Override
            public int hashCode() {
                return Objects.hash(opcode2(), A, cBBBB, C, D, E, F, G, cHHHH);
            }

            @Override
            public Instruction clone() {
                return new Instance(A, cBBBB, C, D, E, F, G, cHHHH);
            }
        }

        Format45cc(Opcode opcode, ReferenceType referenceType, ReferenceType referenceType2) {
            super(opcode, 4);
            this.referenceType = referenceType;
            this.referenceType2 = referenceType2;
        }

        @Override
        public Instruction read(RandomInput in, ReadContext context, int AG) {
            int A = AG >> 4;
            int G = AG & 0xf;
            int BBBB = in.readUnsignedShort();
            int FEDC = in.readUnsignedShort();
            int F = FEDC >> 12;
            int E = (FEDC >> 8) & 0xf;
            int D = (FEDC >> 4) & 0xf;
            int C = FEDC & 0xf;
            int HHHH = in.readUnsignedShort();
            return make(A, referenceType.indexToRef(context, BBBB), C, D,
                    E, F, G, referenceType2.indexToRef(context, HHHH));
        }

        public Instruction make(int A, Object cBBBB, int C, int D, int E, int F, int G, Object cHHHH) {
            return new Instance(A, cBBBB, C, D, E, F, G, cHHHH);
        }
    }
}
