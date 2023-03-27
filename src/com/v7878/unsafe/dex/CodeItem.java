package com.v7878.unsafe.dex;

import static com.v7878.unsafe.Utils.*;
import com.v7878.unsafe.dex.bytecode.Instruction;
import com.v7878.unsafe.io.RandomInput;
import java.util.*;

public class CodeItem implements PublicCloneable {

    private int registers_size;
    private int ins_size;
    private int outs_size;
    private PCList<Instruction> insns;
    private PCList<TryItem> tries;

    public CodeItem(int registers_size, int ins_size, int outs_size,
            PCList<Instruction> insns, PCList<TryItem> tries) {
        setRegistersSize(registers_size);
        setInputsSize(ins_size);
        setOutputsSize(outs_size);
        setInstructions(insns);
        setTries(tries);
    }

    public final void setRegistersSize(int registers_size) {
        assert_(registers_size >= 0, IllegalArgumentException::new,
                "registers_size can`t be negative");
        this.registers_size = registers_size;
    }

    public final int getRegistersSize() {
        return registers_size;
    }

    public final void setInputsSize(int ins_size) {
        assert_(ins_size >= 0, IllegalArgumentException::new,
                "ins_size can`t be negative");
        this.ins_size = ins_size;
    }

    public final int getInputsSize() {
        return ins_size;
    }

    public final void setOutputsSize(int outs_size) {
        assert_(outs_size >= 0, IllegalArgumentException::new,
                "outs_size can`t be negative");
        this.outs_size = outs_size;
    }

    public final int getOutputsSize() {
        return outs_size;
    }

    public final void setInstructions(PCList<Instruction> insns) {
        this.insns = insns == null
                ? PCList.empty() : insns.clone();
    }

    public final PCList<Instruction> getInstructions() {
        return insns;
    }

    public final void setTries(PCList<TryItem> tries) {
        this.tries = tries == null
                ? PCList.empty() : tries.clone();
    }

    public final PCList<TryItem> getTries() {
        return tries;
    }

    static int getInstructionIndex(int[] offsets, int addr) {
        addr = Arrays.binarySearch(offsets, addr);
        assert_(addr >= 0, IllegalStateException::new,
                "unable to find instruction with offset " + addr);
        return addr;
    }

    public static CodeItem read(RandomInput in, ReadContext context) {
        int registers_size = in.readUnsignedShort();
        int ins_size = in.readUnsignedShort();
        int outs_size = in.readUnsignedShort();
        CodeItem out = new CodeItem(registers_size, ins_size, outs_size, null, null);
        int tries_size = in.readUnsignedShort();

        //TODO
        in.readInt(); //out.debug_info_off = in.readInt();

        int[] offsets = Instruction.readArray(in, context, out.insns);
        int insns_size = offsets[out.insns.size()]; // in code units

        for (int i = 0; i < out.insns.size(); i++) {
            System.out.println(offsets[i] + " " + out.insns.get(i));
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
                int handler_offset = (int) (in.position() - handlers_start);
                handlers.put(handler_offset,
                        CatchHandler.read(in, context, offsets));
            }

            in.position(tries_pos);
            for (int i = 0; i < tries_size; i++) {
                out.tries.add(TryItem.read(in, handlers, offsets));
            }
        }
        return out;
    }

    public void collectData(DataCollector data) {
        for (Instruction tmp : insns) {
            data.fill(tmp);
        }
        for (TryItem tmp : tries) {
            data.fill(tmp);
        }
    }

    @Override
    public CodeItem clone() {
        return new CodeItem(registers_size, ins_size, outs_size, insns, tries);
    }
}
