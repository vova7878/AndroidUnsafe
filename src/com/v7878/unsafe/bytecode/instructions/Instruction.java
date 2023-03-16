package com.v7878.unsafe.bytecode.instructions;

import android.util.Pair;
import static com.v7878.unsafe.Utils.*;
import com.v7878.unsafe.bytecode.Context;
import com.v7878.unsafe.io.RandomInput;
import java.util.Arrays;

public class Instruction {

    public static Pair<int[], Instruction[]> readArray(
            RandomInput in, Context context) {
        int insns_size = in.readInt();

        Instruction[] insns = new Instruction[insns_size];
        int[] offsets = new int[insns_size + 1];

        int insns_data_size = insns_size * 2; // 2-byte code units
        int index = 0;
        long start = in.position();

        while (in.position() - start < insns_data_size) {
            int offset = (int) (in.position() - start);
            assert_((offset & 1) == 0, IllegalStateException::new,
                    "Unaligned code unit");
            insns[index] = InstructionReader.read(in, context);
            offsets[index] = offset / 2;
            index++;
        }
        offsets[index] = insns_size;

        assert_(in.position() - start == insns_data_size,
                IllegalStateException::new,
                "Read more code units than expected");

        System.out.println("start = " + start
                + ", end = " + in.position()
                + ", size = " + insns_size);

        offsets = Arrays.copyOf(offsets, index + 1);
        insns = Arrays.copyOf(insns, index);
        return new Pair<>(offsets, insns);
    }
}
