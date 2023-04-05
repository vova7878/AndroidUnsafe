package com.v7878.unsafe.dex;

import java.util.*;

public class PCList<T extends PublicCloneable>
        extends AbstractList<T> implements PublicCloneable {

    private final ArrayList<T> elements;

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
    public final boolean add(T element) {
        return elements.add((T) check(element).clone());
    }

    @Override
    public final void add(int index, T element) {
        elements.add(index, (T) check(element).clone());
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

    @Override
    public final void clear() {
        elements.clear();
    }

    public void ensureCapacity(int minCapacity) {
        elements.ensureCapacity(minCapacity);
    }

    public void trimToSize() {
        elements.trimToSize();
    }

    public final boolean addAll(int index, T[] data, int from, int to) {
        ensureCapacity(size() + to - from);
        Objects.checkFromToIndex(from, to, data.length);
        if (to <= from) {
            return false;
        }
        for (int i = 0; i < to - from; i++) {
            add(index + i, data[from + i]);
        }
        return true;
    }

    public final boolean addAll(T[] data, int from, int to) {
        return addAll(size(), data, from, to);
    }

    public final boolean addAll(int index, T[] data) {
        return addAll(index, data, 0, data.length);
    }

    public final boolean addAll(T[] data) {
        return addAll(size(), data, 0, data.length);
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
