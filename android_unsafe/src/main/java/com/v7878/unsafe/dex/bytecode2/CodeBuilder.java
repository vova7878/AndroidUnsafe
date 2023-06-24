package com.v7878.unsafe.dex.bytecode2;

import static com.v7878.unsafe.Utils.assert_;

import com.v7878.unsafe.Checks;
import com.v7878.unsafe.dex.CodeItem;
import com.v7878.unsafe.dex.PCList;
import com.v7878.unsafe.dex.bytecode2.Format.Format10t;
import com.v7878.unsafe.dex.bytecode2.Format.Format10x;
import com.v7878.unsafe.dex.bytecode2.Format.Format11x;
import com.v7878.unsafe.dex.bytecode2.Format.Format21c;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public final class CodeBuilder {

    private static class InternalLabel {
    }

    private final int registers_size, ins_size;
    private final List<Supplier<Instruction>> instructions;
    private final Map<Object, Integer> labels;

    private int current_instruction, current_unit, max_outs;

    private CodeBuilder(int registers_size, int ins_size) {
        this.registers_size = Checks.checkRange(registers_size, 0, 1 << 16);
        this.ins_size = Checks.checkRange(ins_size, 0, registers_size + 1);
        instructions = new ArrayList<>();
        labels = new HashMap<>();
        current_instruction = 0;
        current_unit = 0;
    }

    public static CodeBuilder begin(int registers_size, int ins_size) {
        return new CodeBuilder(registers_size, ins_size);
    }

    public CodeItem build() {
        PCList<com.v7878.unsafe.dex.bytecode.Instruction> out = PCList.empty();
        out.addAll(instructions.stream().map(Supplier::get).collect(Collectors.toList()));
        return new CodeItem(registers_size, ins_size, max_outs, out, null);
    }

    public int v(int register) {
        return Checks.checkRange(register, 0, registers_size);
    }

    public int p(int register) {
        return Checks.checkRange(register, 0, ins_size) + registers_size - ins_size;
    }

    private int check_register(int register, int width) {
        return Checks.checkRange(register, 0, Math.min(1 << width, registers_size));
    }

    private int check_register_pair(int register_pair, int width) {
        Checks.checkRange(register_pair + 1, 1, registers_size);
        return Checks.checkRange(register_pair, 0, Math.min(1 << width, registers_size));
    }

    private void add(Instruction instruction) {
        assert_(!instruction.opcode2().format().isPayload(), AssertionError::new);
        instructions.add(() -> instruction);
        current_instruction++;
        current_unit += instruction.units();
    }

    private <F extends Format> void add(F format, Function<F, Instruction> factory) {
        assert_(!format.isPayload(), AssertionError::new);
        instructions.add(() -> factory.apply(format));
        current_instruction++;
        current_unit += format.units();
    }

    private void putLabel(Object label) {
        if (labels.putIfAbsent(Objects.requireNonNull(label), current_unit) != null) {
            throw new IllegalArgumentException("label " + label + " already exists");
        }
    }

    private int getLabelUnit(Object label) {
        Integer unit = labels.get(label);
        if (unit == null) {
            throw new IllegalStateException("can`t find label: " + label);
        }
        return unit;
    }

    private int getLabelBranchOffset(Object label, int start_unit) {
        return getLabelBranchOffset(label, start_unit, false);
    }

    private int getLabelBranchOffset(Object label, int start_unit, boolean allow_zero) {
        int offset = getLabelUnit(label) - start_unit;
        if (offset == 0) {
            if (allow_zero) {
                return 0;
            }
            throw new IllegalStateException("zero branch offset is not allowed");
        }
        return offset;
    }

    public CodeBuilder label(String label) {
        putLabel(label);
        return this;
    }

    public CodeBuilder nop() {
        add(Opcode.NOP.<Format10x>format().make());
        return this;
    }

    public CodeBuilder return_void() {
        add(Opcode.RETURN_VOID.<Format10x>format().make());
        return this;
    }

    public CodeBuilder return_(int return_value_register) {
        add(Opcode.RETURN.<Format11x>format().make(
                check_register(return_value_register, 8)));
        return this;
    }

    public CodeBuilder return_wide(int return_value_register_pair) {
        add(Opcode.RETURN_WIDE.<Format11x>format().make(
                check_register_pair(return_value_register_pair, 8)));
        return this;
    }

    public CodeBuilder return_object(int return_value_register) {
        add(Opcode.RETURN_OBJECT.<Format11x>format().make(
                check_register(return_value_register, 8)));
        return this;
    }

    public CodeBuilder const_string(int destination_register, String value) {
        add(Opcode.CONST_STRING.<Format21c>format().make(
                check_register(destination_register, 8), value));
        return this;
    }

    public CodeBuilder goto_(String label) {
        int start_unit = current_unit;
        add(Opcode.GOTO.<Format10t>format(), format -> {
            int branch_offset = getLabelBranchOffset(label, start_unit);
            InstructionWriter.check_signed(branch_offset, 8);
            return format.make(branch_offset);
        });
        return this;
    }
}
