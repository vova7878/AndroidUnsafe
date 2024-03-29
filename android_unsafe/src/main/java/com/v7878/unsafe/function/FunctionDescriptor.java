package com.v7878.unsafe.function;

import static com.v7878.unsafe.Utils.asList;
import static com.v7878.unsafe.Utils.assert_;

import com.v7878.unsafe.memory.Addressable;
import com.v7878.unsafe.memory.Bindable;
import com.v7878.unsafe.memory.Layout;
import com.v7878.unsafe.memory.ValueLayout;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

//TODO: variadic
public class FunctionDescriptor implements Bindable<MethodHandle> {

    private final Layout retLayout;
    private final List<Layout> argLayouts;
    private final boolean has_local_frame;

    private FunctionDescriptor(Layout retLayout, List<Layout> argLayouts,
                               boolean has_local_frame) {
        this.retLayout = retLayout;
        this.argLayouts = argLayouts;
        this.has_local_frame = has_local_frame;
    }

    private FunctionDescriptor(Layout retLayout, List<Layout> argLayouts) {
        this.retLayout = retLayout;
        this.argLayouts = argLayouts;
        this.has_local_frame = isLocalFrameNeeded(retLayout, argLayouts);
    }

    private static boolean isLocalFrameNeeded(Layout retLayout, List<Layout> argLayouts) {
        if (retLayout instanceof ValueLayout.OfObject) {
            return true;
        }
        return argLayouts.stream().anyMatch(layout -> layout instanceof ValueLayout.OfObject);
    }

    public Optional<Layout> returnLayout() {
        return Optional.ofNullable(retLayout);
    }

    public List<Layout> argumentLayouts() {
        return Collections.unmodifiableList(argLayouts);
    }

    public Layout argumentLayout(int num) {
        return argLayouts.get(num);
    }

    public int argumentCount() {
        return argLayouts.size();
    }

    public boolean hasLocalFrame() {
        return has_local_frame;
    }

    private static void checkLayout(Layout value) {
        //TODO
        assert_(value instanceof ValueLayout, IllegalArgumentException::new,
                "only ValueLayout supported");
        //noinspection ConstantConditions
        assert_(((ValueLayout) value).order() == ByteOrder.nativeOrder(),
                IllegalArgumentException::new, "only native order supported");
    }

    private static FunctionDescriptor ofAny(Layout retLayout, Layout... argLayouts) {
        Objects.requireNonNull(argLayouts);
        Arrays.stream(argLayouts).forEach((tmp) -> {
            Objects.requireNonNull(tmp);
            checkLayout(tmp);
        });
        return new FunctionDescriptor(retLayout, Arrays.asList(argLayouts));
    }

    public static FunctionDescriptor of(Layout retLayout, Layout... argLayouts) {
        Objects.requireNonNull(retLayout);
        checkLayout(retLayout);
        return ofAny(retLayout, argLayouts);
    }

    public static FunctionDescriptor ofVoid(Layout... argLayouts) {
        return ofAny(null, argLayouts);
    }

    public MethodType toMethodType() {
        return Linker.inferMethodType(this, false);
    }

    public FunctionDescriptor appendArgumentLayouts(Layout... addedLayouts) {
        return insertArgumentLayouts(argLayouts.size(), addedLayouts);
    }

    public FunctionDescriptor insertArgumentLayouts(int index, Layout... addedLayouts) {
        if (index < 0 || index > argLayouts.size()) {
            throw new IllegalArgumentException("Index out of bounds: " + index);
        }
        List<Layout> added = asList(addedLayouts); // null check on array and its elements
        List<Layout> newLayouts = new ArrayList<>(argLayouts.size() + addedLayouts.length);
        newLayouts.addAll(argLayouts.subList(0, index));
        newLayouts.addAll(added);
        newLayouts.addAll(argLayouts.subList(index, argLayouts.size()));
        return new FunctionDescriptor(retLayout, newLayouts);
    }

    public FunctionDescriptor changeReturnLayout(Layout newReturn) {
        Objects.requireNonNull(newReturn);
        return new FunctionDescriptor(newReturn, argLayouts);
    }

    public FunctionDescriptor dropReturnLayout() {
        return new FunctionDescriptor(null, argLayouts);
    }

    public FunctionDescriptor withLocalFrame() {
        if (has_local_frame) {
            return this;
        }
        return new FunctionDescriptor(retLayout, argLayouts, true);
    }

    @Override
    public MethodHandle bind(Addressable symbol) {
        return Linker.downcallHandle(symbol, this);
    }

    @Override
    public String toString() {
        return String.format("%s(%s)%s",
                has_local_frame ? '+' : '-',
                argLayouts.stream()
                        .map(Object::toString)
                        .collect(Collectors.joining()),
                returnLayout().map(Object::toString).orElse("v"));
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof FunctionDescriptor)) {
            return false;
        }
        FunctionDescriptor otherDescriptor = (FunctionDescriptor) other;
        return has_local_frame == otherDescriptor.has_local_frame
                && Objects.equals(retLayout, otherDescriptor.retLayout)
                && Objects.equals(argLayouts, otherDescriptor.argLayouts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(argLayouts, retLayout, has_local_frame);
    }
}
