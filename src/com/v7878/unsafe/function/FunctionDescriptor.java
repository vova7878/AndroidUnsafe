package com.v7878.unsafe.function;

import static com.v7878.unsafe.Utils.*;
import com.v7878.unsafe.memory.*;
import java.lang.invoke.MethodHandle;
import java.nio.ByteOrder;
import java.util.*;
import java.util.stream.*;

//TODO: variadic
public class FunctionDescriptor implements Bindable<MethodHandle> {

    private final Layout retLayout;
    private final List<Layout> argLayouts;

    private FunctionDescriptor(Layout retLayout, List<Layout> argLayouts) {
        this.retLayout = retLayout;
        this.argLayouts = argLayouts;
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

    private static void checkLayout(Layout value) {
        //TODO
        assert_(value instanceof ValueLayout, IllegalArgumentException::new,
                "only ValueLayout supported");
        assert_(((ValueLayout) value).order() == ByteOrder.nativeOrder(),
                IllegalArgumentException::new,
                "only native order supported");
        assert_(!(value instanceof ValueLayout.OfObject), IllegalArgumentException::new,
                "OfObject isn`t supported");
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

    @Override
    public MethodHandle bind(Addressable symbol) {
        return Linker.downcallHandle(symbol, this);
    }

    @Override
    public String toString() {
        return String.format("(%s)%s",
                IntStream.range(0, argLayouts.size())
                        .mapToObj(argLayouts::get)
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
        return Objects.equals(retLayout, otherDescriptor.retLayout)
                && Objects.equals(argLayouts, otherDescriptor.argLayouts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(argLayouts, retLayout);
    }
}
