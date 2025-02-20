package org.fastily.jwiki.dwrap;

import com.google.gson.JsonObject;
import org.fastily.jwiki.util.GSONP;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a paragraph in a page of wiki text.
 *
 * @author Fastily
 */
public class PageSection {
    /**
     * The text in the header of the page section section, excluding {@code =} characters. If this is null, then there
     * was no header, i.e. this is the lead paragraph of a page.
     */
    public final String header;

    /**
     * The header level of the section. If this is {@code -1}, then there was no header, i.e. this is the lead paragraph
     * of a page.
     */
    public final int level;

    /**
     * The full text of the section, including headers
     */
    public final String text;

    /**
     * Constructor, creates a new PageSection.
     *
     * @param header The header text to set
     * @param level The level to set
     * @param text The text to set
     */
    private PageSection(String header, int level, String text) {
        this.header = header;
        this.level = level;
        this.text = text;
    }

    /**
     * Constructor, creates a new PageSection from a JsonObject representing a parsed header.
     *
     * @param jo The JsonObject representing a parsed header. {@code line} and {@code level} will be retrieved and set
     * automatically.
     * @param text The text to associate with this PageSection
     */
    private PageSection(JsonObject jo, String text) {
        this(GSONP.getStr(jo, "line"), Integer.parseInt(GSONP.getStr(jo, "level")), text);
    }

    /**
     * Creates PageSection objects in the order of parsed header information {@code jl} using {@code text}.
     *
     * @param jl Parsed header information
     * @param text The text associated with the {@code jl}
     * @return A List of PageSection objects in the same order.
     */
    public static List<PageSection> pageBySection(List<JsonObject> jl, String text) {
        List<PageSection> psl = new ArrayList<>();
        if (text.isEmpty())
            return psl;
        else if (jl.isEmpty()) {
            psl.add(new PageSection(null, -1, text));
            return psl;
        }

        JsonObject first = jl.get(0);
        int firstOffset = offsetOf(first);
        if (firstOffset > 0) // handle headerless leads
            psl.add(new PageSection(null, -1, text.substring(0, firstOffset)));

        if (jl.size() == 1) // handle 1 section pages
            psl.add(new PageSection(first, text.substring(offsetOf(first))));
        else // everything else
        {
            for (int i = 0; i < jl.size() - 1; i++) {
                JsonObject curr = jl.get(i);
                psl.add(new PageSection(curr, text.substring(offsetOf(curr), offsetOf(jl.get(i + 1)))));
            }

            JsonObject last = jl.get(jl.size() - 1);
            psl.add(new PageSection(last, text.substring(offsetOf(last))));
        }

        return psl;
    }

    /**
     * Gets the {@code offset} value of {@code jo} as an int.
     *
     * @param jo The JsonObject to use
     * @return The {@code offset} value of {@code jo} as an int.
     */
    private static int offsetOf(JsonObject jo) {
        return jo.get("byteoffset").getAsInt();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        PageSection that = (PageSection) o;
        return this.level == that.level && Objects.equals(this.header, that.header) && Objects.equals(this.text, that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.header, this.level, this.text);
    }
}