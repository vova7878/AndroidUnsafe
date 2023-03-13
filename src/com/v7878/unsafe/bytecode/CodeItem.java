package com.v7878.unsafe.bytecode;

import android.util.Pair;
import com.v7878.unsafe.bytecode.instructions.*;
import com.v7878.unsafe.bytecode.instructions.InstructionReader.*;
import com.v7878.unsafe.io.RandomInput;
import java.util.ArrayList;

public class CodeItem {

    public int registers_size;
    public int ins_size;
    public int outs_size;
    //public short[] insns;
    //public TryItem[] tries;

    public static CodeItem read(RandomInput in, ReadContext rc) {
        CodeItem out = new CodeItem();
        out.registers_size = in.readUnsignedShort();
        out.ins_size = in.readUnsignedShort();
        out.outs_size = in.readUnsignedShort();
        int tries_size = in.readUnsignedShort();

        //TODO
        in.readInt(); //out.debug_info_off = in.readInt();

        ArrayList<Pair<Integer, Instruction>> insns = Instruction.readArray(in, rc);
        for (Pair<Integer, Instruction> pair : insns) {
            System.out.println(pair.first + " " + pair.second);
        }

        /*int insns_size = in.readInt();
        short[] insns = in.readShortArray(insns_size);
        System.out.print("instructions: [");
        for (int i = 0; i < insns_size; i++) {
            System.out.print(Integer.toHexString(out.insns[i] & 0xffff) + ", ");
        }
        System.out.println("]");*/

 /*if (tries_size > 0) {
            if (insns_size % 2 == 1) {
                in.readShort(); // padding
            }
            long old = in.getCurrent();
            long base = old + tries_size * TryItem.getSize();
            in.setCurrent(base);
            int handlers_size = in.readULeb128();
            assert_(tries_size >= handlers_size, () -> "tries_size: "
                    + tries_size + " handlers_size: " + handlers_size,
                    IllegalArgumentException::new);
            Pair<CatchHandler, Integer>[] handlers = new Pair[handlers_size];
            for (int i = 0; i < handlers_size; i++) {
                int off = (int) (in.getCurrent() - base);
                handlers[i] = new Pair<>(CatchHandler.read(in), off);
            }
            in.setCurrent(old);
            old = in.getCurrent();
            out.tries = new TryItem[tries_size];
            for (int i = 0; i < tries_size; i++) {
                out.tries[i] = TryItem.read(in, handlers);
            }
            in.setCurrent(old);
        } else {
            out.tries = new TryItem[0];
        }*/
        return out;
    }
}
