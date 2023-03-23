package com.v7878.unsafe.dex;

import java.util.Objects;

public class PCPair<A extends PublicCloneable, B extends PublicCloneable>
        implements PublicCloneable {

    public final A first;
    public final B second;

    private static <E extends PublicCloneable> E check(E element) {
        return Objects.requireNonNull(element,
                "PCPair can`t contain null element");
    }

    public PCPair(A first, B second) {
        this.first = (A) check(first).clone();
        this.second = (B) check(second).clone();
    }

    public A first() {
        return first;
    }

    public B second() {
        return second;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PCPair) {
            PCPair pobj = (PCPair) obj;
            return Objects.equals(first, pobj.first)
                    && Objects.equals(second, pobj.second);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }

    @Override
    public PCPair<A, B> clone() {
        return new PCPair<>(first, second);
    }
}
