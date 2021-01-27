package org.fastily.jwiki.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Static Collections and Stream utilities.
 *
 * @author Fastily
 */
public final class FL {
    /**
     * Constructors disallowed
     */
    private FL() {

    }

    /**
     * Creates a Map from a Stream. If duplicate keys are encountered, only the latest value will be retained.
     *
     * @param s The Stream to reduce into a Map.
     * @param keyMapper The function mapping each element of {@code s} to a key in the resulting Map.
     * @param valueMapper The function mapping each element of {@code s} to a value in the resulting Map.
     * @param <K> The type of the key in the resulting Map.
     * @param <V> The type of the value in the resulting Map.
     * @param <T1> The type of Object in the Stream.
     * @return A Map, as specified.
     */
    public static <K, V, T1> Map<K, V> toHM(Stream<T1> s, Function<T1, K> keyMapper, Function<T1, V> valueMapper) {
        return s.collect(Collectors.toMap(keyMapper, valueMapper, (oVal, nVal) -> nVal));
    }

    /**
     * Shortcut to turn an array of String objects into a List of String objects.
     *
     * @param strings The list of Strings to put into the resulting List.
     * @return The array as a List
     */
    public static List<String> toSAL(String... strings) {
        return new ArrayList<>(Arrays.asList(strings));
    }

    /**
     * Shortcut to turn an array of String objects into a Set of String objects.
     *
     * @param strings The list of Strings to put into the resulting Set.
     * @return The array as a Set
     */
    public static Set<String> toSHS(String... strings) {
        return new HashSet<>(Arrays.asList(strings));
    }

    /**
     * Extracts each key-value pair from a Map and return the pairs as a List of Tuple objects.
     *
     * @param <T1> The key type of the Map
     * @param <T2> The value type of the Map
     * @param h The Map to work with
     * @return A List of Tuples extracted from <code>h</code>.
     */
    public static <T1, T2> List<Tuple<T1, T2>> mapToList(Map<T1, T2> h) {
        return h.entrySet().stream().map(e -> new Tuple<>(e.getKey(), e.getValue())).collect(Collectors.toList());
    }

    /**
     * Creates a Map with String keys and values. Pass in each pair and value (in that order) into <code>sl</code>.
     * This will be one pair entered into the resulting Map.
     *
     * @param sl The list of elements to turn into a Map
     * @throws IllegalArgumentException if the arguments are an odd size
     * @return The resulting Map
     */
    public static Map<String, String> pMap(String... sl) {
        if (sl.length % 2 == 1)
            throw new IllegalArgumentException("pMap() cannot work with an odd number of entries!");

        Map<String, String> l = new HashMap<>();
        for (int i = 0; i < sl.length; i += 2)
            l.put(sl[i], sl[i + 1]);
        return l;
    }

    /**
     * Creates a Stream from an Iterable.
     *
     * @param i The Iterable to make into a Stream
     * @param <T> {@code i}'s type
     * @return The Stream
     */
    public static <T> Stream<T> streamFrom(Iterable<T> i) {
        return StreamSupport.stream(i.spliterator(), false);
    }

    /**
     * Makes a fence with pipe characters as posts
     *
     * @param planks The planks to use, in order.
     * @return A String with the specified planks and pipe characters as posts
     */
    public static String pipeFence(Collection<String> planks) {
        return String.join("|", planks);
    }

    /**
     * Check if a Collection contains null.
     *
     * @param <T> The type of {@code l}
     * @param l The Collection to check.
     * @return True if there are no null elements in the Collection.
     */
    public static <T> boolean containsNull(Collection<T> l) {
        for (T t : l) {
            if (t == null) {
                return true;
            }
        }
        return false;
    }
}