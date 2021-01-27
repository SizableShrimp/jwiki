package org.fastily.jwiki.core;

import java.util.Map;

/**
 * Stores parameter definition rules for a given query and can use these rules to generate a QueryUnit.
 *
 * @author Fastily
 */
public class QTemplate {
    /**
     * The default fields for this query type. These are immutable.
     */
    protected final Map<String, String> defaultFields;

    /**
     * Optional limit parameter. Will be null if not applicable in this definition.
     */
    protected final String limString;

    /**
     * An id which can be used to lookup a query result (in JSON) for a query created from this Object.
     */
    protected final String id;

    /**
     * Constructor, creates a new QueryUnitTemplate
     *
     * @param defaultFields The default parameters for the query described by this QueryUnitTemplate.
     * @param id The id to use to lookup a query result for queries created with this Object.
     */
    public QTemplate(Map<String, String> defaultFields, String id) {
        this(defaultFields, null, id);
    }

    /**
     * Constructor, creates a new QueryUnitTemplate with a limit String.
     *
     * @param defaultFields The default parameters for the query described by this QueryUnitTemplate.
     * @param limString The limit String parameter. Optional, set null to disable.
     * @param id The id to use to lookup a query result for queries created with this Object.
     */
    public QTemplate(Map<String, String> defaultFields, String limString, String id) {
        this.defaultFields = Map.copyOf(defaultFields);
        this.id = id;

        this.limString = limString;
        if (limString != null)
            defaultFields.put(limString, "max");
    }

    public WQuery createQuery(Wiki wiki) {
        return new WQuery(wiki, this);
    }
}
