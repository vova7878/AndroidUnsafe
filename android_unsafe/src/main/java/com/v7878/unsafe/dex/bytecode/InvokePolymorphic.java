package com.v7878.unsafe.dex.bytecode;

import com.v7878.unsafe.dex.DataCollector;
import com.v7878.unsafe.dex.MethodId;
import com.v7878.unsafe.dex.ProtoId;
import com.v7878.unsafe.dex.WriteContext;
import com.v7878.unsafe.dex.bytecode.InstructionReader.Reader_45cc;
import com.v7878.unsafe.io.RandomOutput;

//TODO dex 038 version
public class InvokePolymorphic extends Instruction {

    public static final int OPCODE = 0xfa;

    static void init() {
        InstructionReader.register(OPCODE, new Reader_45cc(
                (context, A, B, C, D, E, F, G, H) -> new InvokePolymorphic(A, context.method(B),
                        C, D, E, F, G, context.proto(H))));
    }

    public final int argument_word_count;
    public final MethodId method_reference;
    public final int argument_register_1;
    public final int argument_register_2;
    public final int argument_register_3;
    public final int argument_register_4;
    public final int argument_register_5;
    public final ProtoId prototype_reference;

    public InvokePolymorphic(int A, MethodId B, int C,
                             int D, int E, int F, int G, ProtoId H) {
        argument_word_count = A;
        method_reference = B;
        argument_register_1 = C;
        argument_register_2 = D;
        argument_register_3 = E;
        argument_register_4 = F;
        argument_register_5 = G;
        prototype_reference = H;
    }

    @Override
    public void collectData(DataCollector data) {
        data.add(method_reference);
        data.add(prototype_reference);
        //TODO data.requireDexVersion(Dex.Version.V038);
    }

    @Override
    public void write(WriteContext context, RandomOutput out) {
        InstructionWriter.write_45cc(out, opcode(),
                argument_word_count, context.getMethodIndex(method_reference),
                argument_register_1, argument_register_2, argument_register_3,
                argument_register_4, argument_register_5,
                context.getProtoIndex(prototype_reference));
    }

    @Override
    public int opcode() {
        return OPCODE;
    }

    @Override
    public String name() {
        return "invoke-polymorphic";
    }

    @Override
    public String toString() {
        return name() + " " + argument_word_count
                + " " + method_reference
                + " " + argument_register_1
                + " " + argument_register_2
                + " " + argument_register_3
                + " " + argument_register_4
                + " " + argument_register_5
                + " " + prototype_reference;
    }

    @Override
    public InvokePolymorphic clone() {
        return new InvokePolymorphic(argument_word_count, method_reference,
                argument_register_1, argument_register_2,
                argument_register_3, argument_register_4,
                argument_register_5, prototype_reference);
    }
}
