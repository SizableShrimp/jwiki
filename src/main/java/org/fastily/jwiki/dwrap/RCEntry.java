package org.fastily.jwiki.dwrap;

import java.util.Objects;

/**
 * Represents a Recent Changes entry.
 *
 * @author Fastily
 */
public class RCEntry extends DataEntry {
    /**
     * The type of entry this RCEntry represents (ex: log, edit, new)
     */
    public String type;

    /**
     * Constructor, creates an RCEntry with all null fields.
     */
    protected RCEntry() {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;
        RCEntry rcEntry = (RCEntry) o;
        return Objects.equals(this.type, rcEntry.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.type);
    }
}