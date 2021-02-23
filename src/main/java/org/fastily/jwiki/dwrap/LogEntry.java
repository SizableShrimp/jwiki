package org.fastily.jwiki.dwrap;

import java.util.Objects;

/**
 * Represents a MediaWiki Log entry
 *
 * @author Fastily
 */
public class LogEntry extends DataEntry {
    /**
     * The log that this Log Entry belongs to. (e.g. 'delete', 'block')
     */
    public String type;

    /**
     * The action that was performed in this log. (e.g. 'restore', 'revision')
     */
    public String action;

    /**
     * Constructor, creates a LogEntry with all null fields.
     */
    protected LogEntry() {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;
        LogEntry logEntry = (LogEntry) o;
        return Objects.equals(this.type, logEntry.type) && Objects.equals(this.action, logEntry.action);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.type, this.action);
    }
}