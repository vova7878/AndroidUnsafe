package com.v7878.unsafe.dex;

import java.util.*;

public class PCList<T extends PublicCloneable>
        extends AbstractList<T> implements PublicCloneable {

    private final List<T> elements;

    public PCList(T... elements) {
        int length = 0;
        if (elements != null) {
            length = elements.length;
        }
        this.elements = new ArrayList<>(length);
        if (length != 0) {
            addAll(elements);
        }
    }

    public static <E extends PublicCloneable> PCList<E> empty() {
        return new PCList<>();
    }

    protected T check(T element) {
        return Objects.requireNonNull(element,
                "PCList can`t contain null element");
    }

    @Override
    public final void add(int index, T element) {
        elements.add((T) check(element).clone());
    }

    @Override
    public final T set(int index, T element) {
        return elements.set(index, (T) check(element).clone());
    }

    @Override
    public final T get(int index) {
        return elements.get(index);
    }

    @Override
    public final T remove(int index) {
        return elements.remove(index);
    }

    public final boolean addAll(int index, T[] data, int from, int to) {
        return super.addAll(index, Arrays.asList(Arrays.copyOfRange(data, from, to)));
    }

    public final boolean addAll(T[] data, int from, int to) {
        return super.addAll(Arrays.asList(Arrays.copyOfRange(data, from, to)));
    }

    public final boolean addAll(int index, T[] data) {
        return super.addAll(index, Arrays.asList(data));
    }

    public final boolean addAll(T[] data) {
        return super.addAll(Arrays.asList(data));
    }

    @Override
    public final int size() {
        return elements.size();
    }

    @Override
    public boolean isEmpty() {
        return elements.isEmpty();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PCList) {
            PCList<?> tlobj = (PCList<?>) obj;
            return Objects.equals(elements, tlobj.elements);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(elements);
    }

    @Override
    public PCList<T> clone() {
        PCList<T> out = new PCList<>();
        out.addAll(elements);
        return out;
    }
}
