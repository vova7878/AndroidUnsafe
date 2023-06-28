package com.v7878.unsafe.dex;

import static com.v7878.unsafe.Utils.assert_;
import static com.v7878.unsafe.Utils.roundUpL;

import android.util.SparseArray;

import com.v7878.unsafe.Checks;
import com.v7878.unsafe.dex.bytecode.Instruction;
import com.v7878.unsafe.io.RandomInput;
import com.v7878.unsafe.io.RandomOutput;

import java.util.HashMap;
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

    public static CodeItem read(RandomInput in, ReadContext context) {
        int registers_size = in.readUnsignedShort();
        int ins_size = in.readUnsignedShort();
        int outs_size = in.readUnsignedShort();
        int tries_size = in.readUnsignedShort();
        in.readInt(); //TODO: out.debug_info_off = in.readInt();

        CodeItem out = new CodeItem(registers_size, ins_size, outs_size, null, null);

        //TODO: migrate to new bytecode
        out.insns = (PCList<Instruction>) (PCList<?>) com.v7878.unsafe.dex.bytecode2.Instruction.readArray(in, context);

        if (tries_size > 0) {
            in.position(roundUpL(in.position(), 4));
            long tries_pos = in.position();
            in.skipBytes((long) tries_size * TryItem.SIZE);

            long handlers_start = in.position();
            int handlers_size = in.readULeb128();

            SparseArray<CatchHandler> handlers = new SparseArray<>(handlers_size);
            for (int i = 0; i < handlers_size; i++) {
                int handler_offset = (int) (in.position() - handlers_start);
                handlers.put(handler_offset, CatchHandler.read(in, context));
            }

            in.position(tries_pos);
            for (int i = 0; i < tries_size; i++) {
                out.tries.add(TryItem.read(in, handlers));
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
        out.skipBytes(4);

        long insns_start = out.position();
        for (Instruction tmp : insns) {
            tmp.write(context, out);
        }
        int insns_size = (int) (out.position() - insns_start);
        assert_((insns_size & 1) == 0, IllegalStateException::new, "insns_size is odd");

        out.position(insns_size_pos);
        out.writeInt(insns_size / 2); // size in code units
        out.position(insns_start + insns_size);

        if (tries_size != 0) {
            out.alignPositionAndFillZeros(TryItem.ALIGNMENT);

            RandomOutput tries_out = out.duplicate(out.position());
            out.skipBytes((long) TryItem.SIZE * tries_size);

            HashMap<CatchHandler, Integer> handlers = new HashMap<>(tries_size);
            for (TryItem tmp : tries) {
                handlers.put(tmp.getHandler(), null);
            }

            long handlers_start = out.position();
            out.writeULeb128(handlers.size());

            for (CatchHandler tmp : handlers.keySet()) {
                int handler_offset = (int) (out.position() - handlers_start);
                tmp.write(context, out);
                handlers.replace(tmp, handler_offset);
            }
            for (TryItem tmp : tries) {
                tmp.write(tries_out, handlers);
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
