package org.fastily.jwiki.dwrap;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

/**
 * Represents a single revision in the history of a page.
 *
 * @author Fastily
 */
public class Revision extends DataEntry {
    /**
     * The unique id associated with this revision.
     */
    public long revid;

    /**
     * The text of this revision
     */
    @SerializedName("*")
    public String text;

    /**
     * Constructor, creates a Revision with all null fields.
     */
    protected Revision() {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;
        Revision revision = (Revision) o;
        return this.revid == revision.revid && Objects.equals(this.text, revision.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.revid, this.text);
    }
}