package com.v7878.unsafe.dex.bytecode2;

import static com.v7878.unsafe.Utils.assert_;

import com.v7878.unsafe.Checks;
import com.v7878.unsafe.dex.CodeItem;
import com.v7878.unsafe.dex.FieldId;
import com.v7878.unsafe.dex.MethodId;
import com.v7878.unsafe.dex.PCList;
import com.v7878.unsafe.dex.TypeId;
import com.v7878.unsafe.dex.bytecode2.Format.Format10t;
import com.v7878.unsafe.dex.bytecode2.Format.Format10x;
import com.v7878.unsafe.dex.bytecode2.Format.Format11x;
import com.v7878.unsafe.dex.bytecode2.Format.Format21c;
import com.v7878.unsafe.dex.bytecode2.Format.Format22c;
import com.v7878.unsafe.dex.bytecode2.Format.Format35c;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
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

    private CodeItem end() {
        PCList<com.v7878.unsafe.dex.bytecode.Instruction> out = PCList.empty();
        out.addAll(instructions.stream().map(Supplier::get).collect(Collectors.toList()));
        return new CodeItem(registers_size, ins_size, max_outs, out, null);
    }

    public static CodeItem build(int registers_size, int ins_size, Consumer<CodeBuilder> consumer) {
        CodeBuilder builder = new CodeBuilder(registers_size, ins_size);
        consumer.accept(builder);
        return builder.end();
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
        Checks.checkRange(register_pair + 1, 1, registers_size - 1);
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

    public CodeBuilder const_class(int destination_register, TypeId value) {
        add(Opcode.CONST_CLASS.<Format21c>format().make(
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

    public CodeBuilder iget(int value_register, int object_register, FieldId instance_field) {
        add(Opcode.IGET.<Format22c>format().make(check_register(value_register, 4),
                check_register(object_register, 4), instance_field));
        return this;
    }

    public CodeBuilder iget_wide(int value_register_pair, int object_register, FieldId instance_field) {
        add(Opcode.IGET_WIDE.<Format22c>format().make(
                check_register_pair(value_register_pair, 4),
                check_register(object_register, 4), instance_field));
        return this;
    }

    public CodeBuilder iget_object(int value_register, int object_register, FieldId instance_field) {
        add(Opcode.IGET_OBJECT.<Format22c>format().make(check_register(value_register, 4),
                check_register(object_register, 4), instance_field));
        return this;
    }

    public CodeBuilder iget_boolean(int value_register, int object_register, FieldId instance_field) {
        add(Opcode.IGET_BOOLEAN.<Format22c>format().make(check_register(value_register, 4),
                check_register(object_register, 4), instance_field));
        return this;
    }

    public CodeBuilder iget_byte(int value_register, int object_register, FieldId instance_field) {
        add(Opcode.IGET_BYTE.<Format22c>format().make(check_register(value_register, 4),
                check_register(object_register, 4), instance_field));
        return this;
    }

    public CodeBuilder iget_char(int value_register, int object_register, FieldId instance_field) {
        add(Opcode.IGET_CHAR.<Format22c>format().make(check_register(value_register, 4),
                check_register(object_register, 4), instance_field));
        return this;
    }

    public CodeBuilder iget_short(int value_register, int object_register, FieldId instance_field) {
        add(Opcode.IGET_SHORT.<Format22c>format().make(check_register(value_register, 4),
                check_register(object_register, 4), instance_field));
        return this;
    }

    public CodeBuilder iput(int value_register, int object_register, FieldId instance_field) {
        add(Opcode.IPUT.<Format22c>format().make(check_register(value_register, 4),
                check_register(object_register, 4), instance_field));
        return this;
    }

    public CodeBuilder iput_wide(int value_register_pair, int object_register, FieldId instance_field) {
        add(Opcode.IPUT_WIDE.<Format22c>format().make(
                check_register_pair(value_register_pair, 4),
                check_register(object_register, 4), instance_field));
        return this;
    }

    public CodeBuilder iput_object(int value_register, int object_register, FieldId instance_field) {
        add(Opcode.IPUT_OBJECT.<Format22c>format().make(check_register(value_register, 4),
                check_register(object_register, 4), instance_field));
        return this;
    }

    public CodeBuilder iput_boolean(int value_register, int object_register, FieldId instance_field) {
        add(Opcode.IPUT_BOOLEAN.<Format22c>format().make(check_register(value_register, 4),
                check_register(object_register, 4), instance_field));
        return this;
    }

    public CodeBuilder iput_byte(int value_register, int object_register, FieldId instance_field) {
        add(Opcode.IPUT_BYTE.<Format22c>format().make(check_register(value_register, 4),
                check_register(object_register, 4), instance_field));
        return this;
    }

    public CodeBuilder iput_char(int value_register, int object_register, FieldId instance_field) {
        add(Opcode.IPUT_CHAR.<Format22c>format().make(check_register(value_register, 4),
                check_register(object_register, 4), instance_field));
        return this;
    }

    public CodeBuilder iput_short(int value_register, int object_register, FieldId instance_field) {
        add(Opcode.IPUT_SHORT.<Format22c>format().make(check_register(value_register, 4),
                check_register(object_register, 4), instance_field));
        return this;
    }

    private void format_35c_checks(int argument_count, int argument_registers1,
                                   int argument_registers2, int argument_registers3,
                                   int argument_registers4, int argument_registers5) {
        Checks.checkRange(argument_count, 0, 6);
        if (argument_count == 5) check_register(argument_registers5, 4);
        else assert_(argument_registers5 == 0, IllegalArgumentException::new,
                "argument_count < 5, but argument_registers5 != 0");

        if (argument_count >= 4) check_register(argument_registers4, 4);
        else assert_(argument_registers4 == 0, IllegalArgumentException::new,
                "argument_count < 4, but argument_registers4 != 0");

        if (argument_count >= 3) check_register(argument_registers3, 4);
        else assert_(argument_registers3 == 0, IllegalArgumentException::new,
                "argument_count < 3, but argument_registers3 != 0");

        if (argument_count >= 2) check_register(argument_registers2, 4);
        else assert_(argument_registers2 == 0, IllegalArgumentException::new,
                "argument_count < 2, but argument_registers2 != 0");

        if (argument_count >= 1) check_register(argument_registers1, 4);
        else assert_(argument_registers1 == 0, IllegalArgumentException::new,
                "argument_count == 0, but argument_registers1 != 0");
    }

    private void add_outs(int outs_count) {
        Checks.checkRange(outs_count, 0, 1 << 8);
        max_outs = Math.max(max_outs, outs_count);
    }

    public CodeBuilder invoke_virtual(
            MethodId method, int argument_count, int argument_registers1,
            int argument_registers2, int argument_registers3,
            int argument_registers4, int argument_registers5) {
        format_35c_checks(argument_count, argument_registers1, argument_registers2,
                argument_registers3, argument_registers4, argument_registers5);
        add(Opcode.INVOKE_VIRTUAL.<Format35c>format().make(argument_count,
                method, argument_registers1, argument_registers2,
                argument_registers3, argument_registers4, argument_registers5));
        add_outs(argument_count);
        return this;
    }

    public CodeBuilder invoke_virtual(
            MethodId method, int argument_registers1,
            int argument_registers2, int argument_registers3,
            int argument_registers4, int argument_registers5) {
        return invoke_virtual(method, 5, argument_registers1, argument_registers2,
                argument_registers3, argument_registers4, argument_registers5);
    }

    public CodeBuilder invoke_virtual(
            MethodId method, int argument_registers1, int argument_registers2,
            int argument_registers3, int argument_registers4) {
        return invoke_virtual(method, 4, argument_registers1, argument_registers2,
                argument_registers3, argument_registers4, 0);
    }

    public CodeBuilder invoke_virtual(MethodId method, int argument_registers1,
                                      int argument_registers2, int argument_registers3) {
        return invoke_virtual(method, 3, argument_registers1, argument_registers2,
                argument_registers3, 0, 0);
    }

    public CodeBuilder invoke_virtual(
            MethodId method, int argument_registers1, int argument_registers2) {
        return invoke_virtual(method, 2, argument_registers1, argument_registers2,
                0, 0, 0);
    }

    public CodeBuilder invoke_virtual(MethodId method, int argument_registers1) {
        return invoke_virtual(method, 1, argument_registers1, 0,
                0, 0, 0);
    }

    public CodeBuilder invoke_super(
            MethodId method, int argument_count, int argument_registers1,
            int argument_registers2, int argument_registers3,
            int argument_registers4, int argument_registers5) {
        format_35c_checks(argument_count, argument_registers1, argument_registers2,
                argument_registers3, argument_registers4, argument_registers5);
        add(Opcode.INVOKE_SUPER.<Format35c>format().make(argument_count,
                method, argument_registers1, argument_registers2,
                argument_registers3, argument_registers4, argument_registers5));
        add_outs(argument_count);
        return this;
    }

    public CodeBuilder invoke_super(
            MethodId method, int argument_registers1,
            int argument_registers2, int argument_registers3,
            int argument_registers4, int argument_registers5) {
        return invoke_super(method, 5, argument_registers1, argument_registers2,
                argument_registers3, argument_registers4, argument_registers5);
    }

    public CodeBuilder invoke_super(
            MethodId method, int argument_registers1, int argument_registers2,
            int argument_registers3, int argument_registers4) {
        return invoke_super(method, 4, argument_registers1, argument_registers2,
                argument_registers3, argument_registers4, 0);
    }

    public CodeBuilder invoke_super(MethodId method, int argument_registers1,
                                    int argument_registers2, int argument_registers3) {
        return invoke_super(method, 3, argument_registers1, argument_registers2,
                argument_registers3, 0, 0);
    }

    public CodeBuilder invoke_super(
            MethodId method, int argument_registers1, int argument_registers2) {
        return invoke_super(method, 2, argument_registers1, argument_registers2,
                0, 0, 0);
    }

    public CodeBuilder invoke_super(MethodId method, int argument_registers1) {
        return invoke_super(method, 1, argument_registers1, 0,
                0, 0, 0);
    }

    public CodeBuilder invoke_direct(
            MethodId method, int argument_count, int argument_registers1,
            int argument_registers2, int argument_registers3,
            int argument_registers4, int argument_registers5) {
        format_35c_checks(argument_count, argument_registers1, argument_registers2,
                argument_registers3, argument_registers4, argument_registers5);
        add(Opcode.INVOKE_DIRECT.<Format35c>format().make(argument_count,
                method, argument_registers1, argument_registers2,
                argument_registers3, argument_registers4, argument_registers5));
        add_outs(argument_count);
        return this;
    }

    public CodeBuilder invoke_direct(
            MethodId method, int argument_registers1,
            int argument_registers2, int argument_registers3,
            int argument_registers4, int argument_registers5) {
        return invoke_direct(method, 5, argument_registers1, argument_registers2,
                argument_registers3, argument_registers4, argument_registers5);
    }

    public CodeBuilder invoke_direct(
            MethodId method, int argument_registers1, int argument_registers2,
            int argument_registers3, int argument_registers4) {
        return invoke_direct(method, 4, argument_registers1, argument_registers2,
                argument_registers3, argument_registers4, 0);
    }

    public CodeBuilder invoke_direct(MethodId method, int argument_registers1,
                                     int argument_registers2, int argument_registers3) {
        return invoke_direct(method, 3, argument_registers1, argument_registers2,
                argument_registers3, 0, 0);
    }

    public CodeBuilder invoke_direct(
            MethodId method, int argument_registers1, int argument_registers2) {
        return invoke_direct(method, 2, argument_registers1, argument_registers2,
                0, 0, 0);
    }

    public CodeBuilder invoke_direct(MethodId method, int argument_registers1) {
        return invoke_direct(method, 1, argument_registers1, 0,
                0, 0, 0);
    }

    public CodeBuilder invoke_static(
            MethodId method, int argument_count, int argument_registers1,
            int argument_registers2, int argument_registers3,
            int argument_registers4, int argument_registers5) {
        format_35c_checks(argument_count, argument_registers1, argument_registers2,
                argument_registers3, argument_registers4, argument_registers5);
        add(Opcode.INVOKE_STATIC.<Format35c>format().make(argument_count,
                method, argument_registers1, argument_registers2,
                argument_registers3, argument_registers4, argument_registers5));
        add_outs(argument_count);
        return this;
    }

    public CodeBuilder invoke_static(
            MethodId method, int argument_registers1,
            int argument_registers2, int argument_registers3,
            int argument_registers4, int argument_registers5) {
        return invoke_static(method, 5, argument_registers1, argument_registers2,
                argument_registers3, argument_registers4, argument_registers5);
    }

    public CodeBuilder invoke_static(
            MethodId method, int argument_registers1, int argument_registers2,
            int argument_registers3, int argument_registers4) {
        return invoke_static(method, 4, argument_registers1, argument_registers2,
                argument_registers3, argument_registers4, 0);
    }

    public CodeBuilder invoke_static(MethodId method, int argument_registers1,
                                     int argument_registers2, int argument_registers3) {
        return invoke_static(method, 3, argument_registers1, argument_registers2,
                argument_registers3, 0, 0);
    }

    public CodeBuilder invoke_static(
            MethodId method, int argument_registers1, int argument_registers2) {
        return invoke_static(method, 2, argument_registers1, argument_registers2,
                0, 0, 0);
    }

    public CodeBuilder invoke_static(MethodId method, int argument_registers1) {
        return invoke_static(method, 1, argument_registers1, 0,
                0, 0, 0);
    }

    public CodeBuilder invoke_static(MethodId method) {
        return invoke_static(method, 0, 0, 0,
                0, 0, 0);
    }

    public CodeBuilder invoke_interface(
            MethodId method, int argument_count, int argument_registers1,
            int argument_registers2, int argument_registers3,
            int argument_registers4, int argument_registers5) {
        format_35c_checks(argument_count, argument_registers1, argument_registers2,
                argument_registers3, argument_registers4, argument_registers5);
        add(Opcode.INVOKE_INTERFACE.<Format35c>format().make(argument_count,
                method, argument_registers1, argument_registers2,
                argument_registers3, argument_registers4, argument_registers5));
        add_outs(argument_count);
        return this;
    }

    public CodeBuilder invoke_interface(
            MethodId method, int argument_registers1,
            int argument_registers2, int argument_registers3,
            int argument_registers4, int argument_registers5) {
        return invoke_interface(method, 5, argument_registers1, argument_registers2,
                argument_registers3, argument_registers4, argument_registers5);
    }

    public CodeBuilder invoke_interface(
            MethodId method, int argument_registers1, int argument_registers2,
            int argument_registers3, int argument_registers4) {
        return invoke_interface(method, 4, argument_registers1, argument_registers2,
                argument_registers3, argument_registers4, 0);
    }

    public CodeBuilder invoke_interface(MethodId method, int argument_registers1,
                                        int argument_registers2, int argument_registers3) {
        return invoke_interface(method, 3, argument_registers1, argument_registers2,
                argument_registers3, 0, 0);
    }

    public CodeBuilder invoke_interface(
            MethodId method, int argument_registers1, int argument_registers2) {
        return invoke_interface(method, 2, argument_registers1, argument_registers2,
                0, 0, 0);
    }

    public CodeBuilder invoke_interface(MethodId method, int argument_registers1) {
        return invoke_interface(method, 1, argument_registers1, 0,
                0, 0, 0);
    }
}
