package com.v7878.unsafe.function;

import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

class SoftReferenceCache<K, V> {

    private final Map<K, Node> cache = new ConcurrentHashMap<>();

    public V get(K key, Function<K, V> valueFactory) {
        return cache
                .computeIfAbsent(key, k -> new Node()) // short lock (has to be according to ConcurrentHashMap)
                .get(key, valueFactory); // long lock, but just for the particular key
    }

    private class Node {

        private volatile SoftReference<V> ref;

        public Node() {
        }

        public V get(K key, Function<K, V> valueFactory) {
            V result;
            if (ref == null || (result = ref.get()) == null) {
                synchronized (this) { // don't let threads race on the valueFactory::apply call
                    if (ref == null || (result = ref.get()) == null) {
                        result = valueFactory.apply(key); // keep alive
                        ref = new SoftReference<>(result);
                    }
                }
            }
            return result;
        }
    }
}
