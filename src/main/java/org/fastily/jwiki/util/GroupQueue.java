package org.fastily.jwiki.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A simple read-only Queue that allows multiple items to be polled at once.
 *
 * @param <T> The type of Object contained in the queue.
 * @author Fastily
 */
public class GroupQueue<T> {
    /**
     * The backing data structure.
     */
    private final List<T> l;

    /**
     * The size, start, end, and maximum size of polls.
     */
    private final int size;
    private int start = 0;
    private int end;
    private final int maxPoll;

    /**
     * Constructor, creates a new GroupQueue.
     *
     * @param l The backing List to use. This will not be modified.
     * @param maxPoll The maximum number of elements to poll at once.
     */
    public GroupQueue(Collection<T> l, int maxPoll) {
        this.l = l instanceof List<?> ? (List<T>) l : new ArrayList<>(l);
        size = l.size();
        end = maxPoll;

        this.maxPoll = maxPoll;
    }

    /**
     * Polls this Queue and returns &le; {@code maxPoll} elements.
     *
     * @return An List with the first {@code maxPoll} elements if possible. Returns the empty list if there is
     * nothing left.
     */
    public List<T> poll() {
        if (!has())
            return new ArrayList<>();

        if (size - start < maxPoll)
            end = size;

        List<T> temp = l.subList(start, end);

        start += maxPoll;
        end += maxPoll;

        return temp;
    }

    /**
     * Determines whether there are elements remaining in the queue.
     *
     * @return True if there are elements left in the queue.
     */
    public boolean has() {
        return start < size;
    }
}