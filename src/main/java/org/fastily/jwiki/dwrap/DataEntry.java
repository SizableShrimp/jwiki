package org.fastily.jwiki.dwrap;

import com.google.gson.annotations.SerializedName;

import java.time.Instant;
import java.util.Objects;

/**
 * Structured data template class.
 *
 * @author Fastily
 */
public abstract class DataEntry {
    /**
     * The name of the user who made the contribution.
     */
    public String user;

    /**
     * Title and edit summary.
     */
    public String title;

    /**
     * The edit summary used in this contribution.
     */
    @SerializedName("comment")
    public String summary;

    /**
     * The date and time at which this edit was made.
     */
    public Instant timestamp;

    /**
     * Constructor, creates a DataEntry with all null fields.
     */
    protected DataEntry() {

    }

    /**
     * Gets a String representation of this DataEntry. Useful for debugging.
     */
    public String toString() {
        return String.format("[ user : %s, title : %s, summary : %s, timestamp : %s ]", user, title, summary, timestamp);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        DataEntry dataEntry = (DataEntry) o;
        return Objects.equals(this.user, dataEntry.user) && Objects.equals(this.title, dataEntry.title) && Objects.equals(this.summary, dataEntry.summary) && Objects.equals(this.timestamp, dataEntry.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.user, this.title, this.summary, this.timestamp);
    }
}