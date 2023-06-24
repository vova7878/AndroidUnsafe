package com.v7878.unsafe.dex;

import static com.v7878.unsafe.Utils.assert_;

import com.v7878.unsafe.Checks;
import com.v7878.unsafe.dex.bytecode.Instruction;
import com.v7878.unsafe.io.RandomInput;
import com.v7878.unsafe.io.RandomOutput;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CodeItem implements PublicCloneable {

    public static final int ALIGNMENT = 4;

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
        Checks.checkRange(registers_size, 0, 1 << 16);
        this.registers_size = registers_size;
    }

    public final int getRegistersSize() {
        return registers_size;
    }

    public final void setInputsSize(int ins_size) {
        Checks.checkRange(ins_size, 0, 1 << 16);
        this.ins_size = ins_size;
    }

    public final int getInputsSize() {
        return ins_size;
    }

    public final void setOutputsSize(int outs_size) {
        Checks.checkRange(ins_size, 0, 1 << 8);
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
        int insns_units_size = offsets[out.insns.size()]; // in code units

        //TODO: remove after tests
        /*for (int i = 0; i < out.insns.size(); i++) {
            System.out.println(offsets[i] + " " + out.insns.get(i));
        }*/
        if (tries_size > 0) {
            if ((insns_units_size & 1) != 0) {
                in.readShort(); // padding
            }

            long tries_pos = in.position();
            in.skipBytes((long) tries_size * TryItem.SIZE);

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

    public void write(WriteContext context, RandomOutput out) {
        out.writeShort(registers_size);
        out.writeShort(ins_size);
        out.writeShort(outs_size);

        int tries_size = tries.size();
        out.writeShort(tries_size);

        out.writeInt(0); // TODO: debug_info_off

        long insns_size_pos = out.position();
        int insns_size = insns.size();
        out.skipBytes(4);

        long insns_start = out.position();
        int[] offsets = new int[insns_size + 1];
        for (int i = 0; i <= insns_size; i++) {
            int offset = (int) (out.position() - insns_start);
            assert_((offset & 1) == 0, IllegalStateException::new,
                    "Unaligned code unit");
            offsets[i] = offset / 2;
            if (i != insns_size) {
                insns.get(i).write(context, out);
            }
        }

        out.position(insns_size_pos);
        out.writeInt(offsets[insns_size]); // size in code units
        out.position(insns_start + offsets[insns_size] * 2L);

        //TODO: remove after tests
        /*System.out.println(registers_size + " " + ins_size + " " + outs_size);
        for (int i = 0; i < insns.size(); i++) {
            System.out.println(offsets[i] + " " + insns.get(i));
        }*/
        if (tries_size != 0) {
            if ((offsets[insns_size] & 1) != 0) {
                out.writeShort(0); // padding
            }

            RandomOutput tries_out = out.duplicate(out.position());
            out.skipBytes((long) TryItem.SIZE * tries_size);

            HashMap<CatchHandler, Integer> handlers
                    = new HashMap<>(tries_size);
            for (TryItem tmp : tries) {
                handlers.put(tmp.getHandler(), null);
            }

            long handlers_start = out.position();
            out.writeULeb128(handlers.size());

            for (CatchHandler tmp : handlers.keySet()) {
                int handler_offset = (int) (out.position() - handlers_start);
                tmp.write(context, out, offsets);
                handlers.replace(tmp, handler_offset);
            }
            for (TryItem tmp : tries) {
                tmp.write(context, tries_out, handlers, offsets);
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CodeItem) {
            CodeItem ciobj = (CodeItem) obj;
            return registers_size == ciobj.registers_size
                    && ins_size == ciobj.ins_size
                    && outs_size == ciobj.outs_size
                    && Objects.equals(insns, ciobj.insns)
                    && Objects.equals(tries, ciobj.tries);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(registers_size, ins_size,
                outs_size, insns, tries);
    }

    @Override
    public CodeItem clone() {
        return new CodeItem(registers_size, ins_size, outs_size, insns, tries);
    }
}
