package org.fastily.jwiki.dwrap;

import java.util.Objects;

/**
 * Represents a contribution made by a user.
 *
 * @author Fastily
 */
public class Contrib extends DataEntry {
    /**
     * This contribution's revision id.
     */
    public long revid;

    /**
     * This contribution's parent ID
     */
    public long parentid;

    /**
     * Constructor, creates a Contrib with all null fields.
     */
    protected Contrib() {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;
        Contrib contrib = (Contrib) o;
        return this.revid == contrib.revid && this.parentid == contrib.parentid;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.revid, this.parentid);
    }
}