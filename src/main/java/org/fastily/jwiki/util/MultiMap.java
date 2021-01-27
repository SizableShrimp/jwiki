package org.fastily.jwiki.util;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A Map which allows multiple values for each key. Duplicate values are permitted.
 *
 * @param <K> The type of the key
 * @param <V> The type of the values, which will be stored in a List.
 * @author Fastily
 */
public class MultiMap<K, V> extends HashMap<K, List<V>> {
    private static final long serialVersionUID = 2009710673163769278L;

    /**
     * Constructor, creates an empty MultiMap.
     */
    public MultiMap() {
        super();
    }

    /**
     * Adds a key-value pair to this MultiMap.
     *
     * @param k The key to add
     * @param v The value to add to the list
     */
    public List<V> putValue(K k, V v) {
        List<V> l = getOrMake(k);
        l.add(v);
        return l;
    }

    /**
     * Merges a {@link List} of V objects into the existing value set for a given key.
     *
     * @param k the key to add
     * @param v the list of {@link V} objects to merge
     * @return the value in the map
     */
    public List<V> putAll(K k, List<V> v) {
        List<V> l = getOrMake(k);
        l.addAll(v);
        return l;
    }

    @NotNull
    private List<V> getOrMake(K k) {
        return super.computeIfAbsent(k, x -> new ArrayList<>());
    }
}