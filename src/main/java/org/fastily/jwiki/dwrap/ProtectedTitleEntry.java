package org.fastily.jwiki.dwrap;

import java.util.Objects;

/**
 * Represents an entry obtained from the {@code protectedtitles} API module.
 *
 * @author Fastily
 */
public class ProtectedTitleEntry extends DataEntry {
    /**
     * The protection level
     */
    public String level;

    /**
     * Constructor, creates a ProtectedTitleEntry with all null fields.
     */
    protected ProtectedTitleEntry() {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;
        ProtectedTitleEntry that = (ProtectedTitleEntry) o;
        return Objects.equals(this.level, that.level);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.level);
    }
}