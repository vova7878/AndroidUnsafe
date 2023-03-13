package com.v7878.unsafe.bytecode.instructions;

import android.util.Pair;
import static com.v7878.unsafe.Utils.*;
import com.v7878.unsafe.bytecode.ReadContext;
import com.v7878.unsafe.io.RandomInput;
import java.util.ArrayList;

public class Instruction {

    public static ArrayList<Pair<Integer, Instruction>> readArray(
            RandomInput in, ReadContext rc) {
        int insns_size = in.readInt();
        int insns_data_size = insns_size * 2;
        long start = in.position();
        ArrayList<Pair<Integer, Instruction>> out = new ArrayList<>();
        while (in.position() - start < insns_data_size) {
            int offset = (int) (in.position() - start);
            assert_((offset & 1) == 0, IllegalStateException::new, "");
            Instruction i = InstructionReader.read(in, rc);
            out.add(new Pair<>(offset, i));
        }
        System.out.println("start = " + start
                + ", end = " + in.position()
                + ", size = " + insns_size);
        return out;
    }
}
