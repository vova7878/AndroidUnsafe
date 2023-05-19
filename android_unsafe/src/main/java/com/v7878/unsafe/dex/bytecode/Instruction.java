package com.v7878.unsafe.dex.bytecode;

import static com.v7878.unsafe.Utils.assert_;

import com.v7878.unsafe.dex.DataCollector;
import com.v7878.unsafe.dex.PCList;
import com.v7878.unsafe.dex.PublicCloneable;
import com.v7878.unsafe.dex.ReadContext;
import com.v7878.unsafe.dex.WriteContext;
import com.v7878.unsafe.io.RandomInput;
import com.v7878.unsafe.io.RandomOutput;

import java.util.Arrays;

public abstract class Instruction implements PublicCloneable {

    public static int[] readArray(RandomInput in,
                                  ReadContext context, PCList<Instruction> insns) {
        int insns_size = in.readInt();
        int[] offsets = new int[insns_size + 1];

        int insns_bytes = insns_size * 2; // 2-byte code units
        int index = 0;
        long start = in.position();

        while (in.position() - start < insns_bytes) {
            int offset = (int) (in.position() - start);
            assert_((offset & 1) == 0, IllegalStateException::new,
                    "Unaligned code unit");
            insns.add(InstructionReader.read(in, context));
            offsets[index] = offset / 2;
            index++;
        }
        offsets[index] = insns_size;

        assert_(in.position() - start == insns_bytes,
                IllegalStateException::new,
                "Read more code units than expected");

        return Arrays.copyOf(offsets, index + 1);
    }

    public void collectData(DataCollector data) {
    }

    public abstract void write(WriteContext context, RandomOutput out);

    public abstract int opcode();

    public abstract String name();

    @Override
    public abstract Instruction clone();

    //TODO: equals
}
