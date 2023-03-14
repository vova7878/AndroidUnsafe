package com.v7878.unsafe.bytecode;

import android.util.Pair;
import static com.v7878.unsafe.Utils.*;
import com.v7878.unsafe.bytecode.instructions.*;
import com.v7878.unsafe.io.RandomInput;
import java.util.*;

public class CodeItem {

    public int registers_size;
    public int ins_size;
    public int outs_size;
    public Instruction[] insns;
    public TryItem[] tries;

    public static CodeItem read(RandomInput in, ReadContext rc) {
        CodeItem out = new CodeItem();
        out.registers_size = in.readUnsignedShort();
        out.ins_size = in.readUnsignedShort();
        out.outs_size = in.readUnsignedShort();
        int tries_size = in.readUnsignedShort();

        //TODO
        in.readInt(); //out.debug_info_off = in.readInt();

        Pair<int[], Instruction[]> insns_data = Instruction.readArray(in, rc);
        out.insns = insns_data.second;

        int[] offsets = insns_data.first;
        int insns_size = offsets[out.insns.length]; // in code units
        offsets = Arrays.copyOf(offsets, out.insns.length);

        for (int i = 0; i < out.insns.length; i++) {
            System.out.println(offsets[i] + " " + out.insns[i]);
        }

        if (tries_size > 0) {
            if ((insns_size & 1) != 0) {
                in.readShort(); // padding
            }

            long tries_pos = in.position();
            in.skipBytes(tries_size * TryItem.SIZE);

            long handlers_start = in.position();
            int handlers_size = in.readULeb128();
            assert_(tries_size >= handlers_size, IllegalArgumentException::new,
                    String.format("tries_size(%s) less than handlers_size(%s)",
                            tries_size, handlers_size));

            Map<Integer, CatchHandler> handlers = new HashMap<>(handlers_size);
            for (int i = 0; i < handlers_size; i++) {
                int offset = (int) (in.position() - handlers_start);
                handlers.put(offset, CatchHandler.read(in, rc));
            }

            in.position(tries_pos);
            out.tries = new TryItem[tries_size];
            for (int i = 0; i < tries_size; i++) {
                out.tries[i] = TryItem.read(in, handlers);
                System.out.println(out.tries[i]);
            }
        } else {
            out.tries = new TryItem[0];
        }
        return out;
    }
}
