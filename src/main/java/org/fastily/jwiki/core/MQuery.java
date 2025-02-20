package org.fastily.jwiki.core;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.fastily.jwiki.dwrap.ImageInfo;
import org.fastily.jwiki.util.FL;
import org.fastily.jwiki.util.GSONP;
import org.fastily.jwiki.util.GroupQueue;
import org.fastily.jwiki.util.MultiMap;
import org.fastily.jwiki.util.Tuple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Perform multi-title queries. Use of these methods is intended for
 * <span style="text-decoration:underline;">advanced</span> users who wish to make queries to the server over a large
 * data set. These methods are optimized for performance, and will consolidate titles into single queries to fetch the
 * most data possible per query. If you're looking to make simple, single-item queries, (which is suitable for most
 * users) please use the methods in Wiki.java.
 *
 * @author Fastily
 * @see Wiki
 */
public final class MQuery {
    /**
     * The group {@code prop} query (multiple titles query) maximum
     */
    private static final int MAX_GROUP_QUERY = 50;

    /**
     * Constructors disallowed
     */
    private MQuery() {}

    /**
     * Generic page property ({@code prop}) fetching. This implementation fetches *all* available properties. Use this
     * for prop queries that only return one String of interest per nested JsonObject.
     *
     * @param wiki The Wiki to use
     * @param titles The titles to query for.
     * @param qut The query template to use. Set this according to the fetching method being implemented
     * @param pl Additional custom parameters to apply to each generated WQuery. Optional, set null to disable.
     * @param elemArrKey The key for each JsonArray for each title the resulting set
     * @return A Map where the key is the title of the page, and the value is the List of properties fetched.
     */
    private static MultiMap<String, JsonObject> getContProp(Wiki wiki, Collection<String> titles, QTemplate qut, Map<String, String> pl, String elemArrKey) {
        MultiMap<String, JsonObject> l = new MultiMap<>();

        if (FL.containsNull(titles))
            throw new IllegalArgumentException("null is not an acceptable title to query with");

        GroupQueue<String> gq = new GroupQueue<>(titles, MAX_GROUP_QUERY);

        while (gq.has()) {
            WQuery wq = new WQuery(wiki, qut).set("titles", gq.poll());
            if (pl != null)
                pl.forEach(wq::set);

            while (wq.has()) {
                wq.next().propComp("title", elemArrKey).forEach((k, v) -> {
                    l.touch(k);
                    if (v != null)
                        l.putAll(k, GSONP.getJAofJO(v.getAsJsonArray()));
                });
            }
        }
        return l;
    }

    /**
     * Performs a non-continuing {@code prop} query. Grabs a title and an element from each returned page.
     *
     * @param wiki The Wiki to query.
     * @param titles The titles to use
     * @param qut The QTemplate to use
     * @param pl Additional parameters to pass to each created WQuery, set null to disable.
     * @param eKey The value key to get from each page element. If this cannot be found, then it is set to null.
     * @return The {@code title} of each page as the key, and the value of the associated {@code eKey}.
     */
    private static Map<String, JsonElement> getNoContProp(Wiki wiki, Collection<String> titles, QTemplate qut, Map<String, String> pl, String eKey) {
        Map<String, JsonElement> m = new HashMap<>();

        if (FL.containsNull(titles))
            throw new IllegalArgumentException("null is not an acceptable title to query with");

        GroupQueue<String> gq = new GroupQueue<>(titles, MAX_GROUP_QUERY);
        while (gq.has()) {
            WQuery wq = new WQuery(wiki, qut).set("titles", gq.poll());
            if (pl != null)
                pl.forEach(wq::set);

            m.putAll(wq.next().propComp("title", eKey));
        }
        return m;
    }

    /**
     * Performs a non-continuing {@code list} query. Grabs JsonObjects from the JsonArray in the server Response.
     *
     * @param wiki The Wiki to query
     * @param titles The titles to use
     * @param qut The QTemplate to use
     * @param pl Additional parameters to pass to each created WQuery, set null to disable.
     * @param tQKey The variable name to use for each set of 50 {@code titles} in the url passed to the server.
     * @param aKey The key pointing to the JsonArray of JsonObject in the server's Response.
     * @return A List of JsonObject collected from the server Response(s).
     */
    private static List<JsonObject> getNoContList(Wiki wiki, Collection<String> titles, QTemplate qut, Map<String, String> pl, String tQKey, String aKey) {
        List<JsonObject> l = new ArrayList<>();

        if (FL.containsNull(titles))
            throw new IllegalArgumentException("null is not an acceptable title to query with");

        GroupQueue<String> gq = new GroupQueue<>(titles, MAX_GROUP_QUERY);
        while (gq.has()) {
            WQuery wq = new WQuery(wiki, qut).set(tQKey, gq.poll());
            if (pl != null)
                pl.forEach(wq::set);

            l.addAll(wq.next().listComp(aKey));
        }

        return l;
    }

    /**
     * Retrieve one String value from each JsonObject List for each pair in a MapList.
     *
     * @param m The MapList to work with
     * @param elemKey The key pointing to String to get in each JsonObject.
     * @return Each title, and the values that were found for it.
     */
    private static Map<String, List<String>> parsePropToSingle(MultiMap<String, JsonObject> m, String elemKey) {
        Map<String, List<String>> xl = new HashMap<>();
        m.forEach((k, v) -> xl.put(k, v.stream().map(e -> GSONP.getStr(e, elemKey)).collect(Collectors.toList())));

        return xl;
    }

    /**
     * Retrieve one String value from each JsonObject List for each pair in a MapList. Assumes that the key to
     * select from each JsonObject is {@code title}.
     *
     * @param m The MapList to work with
     * @return Each title, and the values that were found for it.
     */
    private static Map<String, List<String>> parsePropToSingle(MultiMap<String, JsonObject> m) {
        return parsePropToSingle(m, "title");
    }

    /**
     * Retrieve two String value from each JsonObject List for each pair in a MapList.
     *
     * @param m The MapList to work with
     * @param elemKey1 The key pointing to the first String to get in each JsonObject.
     * @param elemKey2 The key pointing to the second String to get in each JsonObject.
     * @return Each title, and the values that were found for it.
     */
    private static Map<String, List<Tuple<String, String>>> parsePropToDouble(MultiMap<String, JsonObject> m, String elemKey1,
                                                                                       String elemKey2) {
        Map<String, List<Tuple<String, String>>> xl = new HashMap<>();
        m.forEach((k, v) -> xl.put(k, v.stream().map(e -> new Tuple<>(GSONP.getStr(e, elemKey1), GSONP.getStr(e, elemKey2))).collect(Collectors.toList())));

        return xl;
    }

    /**
     * Gets the list of usergroups (rights) users belong to. Sample groups: sysop, user, autoconfirmed, editor.
     *
     * @param wiki The Wiki object to use.
     * @param users Users to get rights information for. Do not include {@code User:} prefix.
     * @return A Map such that the key is the user and the value a List of the user's rights (or null if the user does
     * not exist)
     */
    public static Map<String, List<String>> listUserRights(Wiki wiki, Collection<String> users) {
        Map<String, List<String>> l = new HashMap<>();
        getNoContList(wiki, users, WQuery.USERRIGHTS, null, "ususers", "users")
                .forEach(jo -> l.put(GSONP.getStr(jo, "name"), jo.has("groups") ? GSONP.jaOfStrToAL(jo.getAsJsonArray("groups")) : null));

        return l;
    }

    /**
     * Gets ImageInfo objects for each revision of a File.
     *
     * @param wiki The Wiki object to use
     * @param titles The titles to query
     * @return A map with titles keyed to respective lists of ImageInfo.
     */
    public static Map<String, List<ImageInfo>> getImageInfo(Wiki wiki, Collection<String> titles) {
        Map<String, List<ImageInfo>> l = new HashMap<>();
        getContProp(wiki, titles, WQuery.IMAGEINFO, null, "imageinfo")
                .forEach((k, v) -> l.put(k, v.stream().map(jo -> GSONP.gson.fromJson(jo, ImageInfo.class)).collect(Collectors.toList())));

        // MediaWiki imageinfo is not a well-behaved module
        l.forEach((k, v) -> Collections.sort(v));

        return l;
    }

    /**
     * Gets the list of categories on a page.
     *
     * @param wiki The wiki object to use
     * @param titles The titles to query.
     * @return A list of results keyed by title.
     */
    public static Map<String, List<String>> getCategoriesOnPage(Wiki wiki, Collection<String> titles) {
        return parsePropToSingle(getContProp(wiki, titles, WQuery.PAGECATEGORIES, null, "categories"));
    }

    /**
     * Gets the number of elements contained in a category.
     *
     * @param wiki The wiki object to use
     * @param titles The titles to query. PRECONDITION: Titles *must* begin with the "Category:" prefix
     * @return A list of results keyed by title. Value returned will be -1 if Category entered was empty <b>and</b>
     * non-existent.
     */
    public static Map<String, Integer> getCategorySize(Wiki wiki, Collection<String> titles) {
        Map<String, Integer> l = new HashMap<>();
        getNoContProp(wiki, titles, WQuery.CATEGORYINFO, null, "categoryinfo")
                .forEach((k, v) -> l.put(k, v == null ? 0 : v.getAsJsonObject().get("size").getAsInt()));
        return l;
    }

    /**
     * Gets the text of a page.
     *
     * @param wiki The wiki to use
     * @param titles The titles to query
     * @return A list of results keyed by title.
     */
    public static Map<String, String> getPageText(Wiki wiki, Collection<String> titles) {
        Map<String, String> l = new HashMap<>();
        getNoContProp(wiki, titles, WQuery.PAGETEXT, null, "revisions").forEach((k, v) -> {
            if (v == null)
                l.put(k, "");
            else {
                List<JsonObject> jl = GSONP.getJAofJO(v.getAsJsonArray());
                l.put(k, jl.isEmpty() ? "" : GSONP.getStr(jl.get(0), "*"));
            }
        });

        return l;
    }

    /**
     * Get wiki links on a page.
     *
     * @param wiki The wiki object to use
     * @param titles The titles to query.
     * @param ns Namespaces to include-only. Optional param: leave blank to disable.
     * @return A list of results keyed by title.
     */
    public static Map<String, List<String>> getLinksOnPage(Wiki wiki, Collection<String> titles, NS... ns) {
        Map<String, String> pl = new HashMap<>();
        if (ns != null && ns.length > 0)
            pl.put("plnamespace", wiki.nsl.createFilter(ns));

        return parsePropToSingle(getContProp(wiki, titles, WQuery.LINKSONPAGE, pl, "links"));
    }

    /**
     * Get pages redirecting to or linking to a page.
     *
     * @param wiki The wiki object to use
     * @param redirects Set to true to search for redirects. False searches for non-redirects.
     * @param titles The titles to query
     * @return A list of results keyed by title.
     */
    public static Map<String, List<String>> linksHere(Wiki wiki, boolean redirects, Collection<String> titles) {
        return parsePropToSingle(
                getContProp(wiki, titles, WQuery.LINKSHERE, FL.pMap("lhshow", (redirects ? "" : "!") + "redirect"), "linkshere"));
    }

    /**
     * Gets a list of pages transcluding a template.
     *
     * @param wiki The wiki object to use
     * @param titles The titles to query
     * @param ns Only return results from this/these namespace(s). Optional param: leave blank to disable.
     * @return A list of results keyed by title.
     */
    public static Map<String, List<String>> transcludesIn(Wiki wiki, Collection<String> titles, NS... ns) {
        Map<String, String> pl = new HashMap<>();
        if (ns.length > 0)
            pl.put("tinamespace", wiki.nsl.createFilter(ns));

        return parsePropToSingle(getContProp(wiki, titles, WQuery.TRANSCLUDEDIN, pl, "transcludedin"));
    }

    /**
     * Gets a list of pages linking (displaying/thumbnailing) a file.
     *
     * @param wiki The wiki to use
     * @param titles The titles to query. PRECONDITION: These must be valid file names prefixed with the "File:" prefix.
     * @return A Map of results keyed by title.
     */
    public static Map<String, List<String>> fileUsage(Wiki wiki, Collection<String> titles) {
        return parsePropToSingle(getContProp(wiki, titles, WQuery.FILEUSAGE, null, "fileusage"));
    }

    /**
     * Gets a list of external (non-interwiki) links on the specified titles.
     *
     * @param wiki The Wiki object to use
     * @param titles The titles to query
     * @return A Map of results keyed by title.
     */
    public static Map<String, List<String>> getExternalLinks(Wiki wiki, Collection<String> titles) {
        return parsePropToSingle(getContProp(wiki, titles, WQuery.EXTLINKS, null, "extlinks"), "*");
    }

    // FIXME: getNoContProp -> doubled output on non-normal titles

    /**
     * Checks if list of titles exists.
     *
     * @param wiki The wiki object to use
     * @param titles The titles to query.
     * @return Results keyed by title. {@code true} means the title exists.
     */
    public static Map<String, Boolean> exists(Wiki wiki, Collection<String> titles) {
        Map<String, Boolean> l = new HashMap<>();
        getNoContProp(wiki, titles, WQuery.EXISTS, null, "missing").forEach((k, v) -> l.put(k, v == null));
        return l;
    }

    /**
     * Checks if a title exists. Can filter results based on whether pages exist.
     *
     * @param wiki The wiki object to use
     * @param exists Set to true to select all pages that exist. False selects all that don't exist
     * @param titles The titles to query.
     * @return A list of titles that exist or don't exist.
     */
    public static List<String> exists(Wiki wiki, boolean exists, Collection<String> titles) {
        List<String> l = new ArrayList<>();
        exists(wiki, titles).forEach((k, v) -> {
            if (Boolean.valueOf(exists).equals(v))
                l.add(k);
        });

        return l;
    }

    /**
     * Gets titles of images linked on a page.
     *
     * @param wiki The wiki object to use
     * @param titles The titles to query
     * @return A list of results keyed by title.
     */
    public static Map<String, List<String>> getImagesOnPage(Wiki wiki, Collection<String> titles) {
        return parsePropToSingle(getContProp(wiki, titles, WQuery.IMAGES, null, "images"));
    }

    /**
     * Get templates transcluded on a page.
     *
     * @param wiki The wiki object to use
     * @param titles The titles to query
     * @return A list of results keyed by title.
     */
    public static Map<String, List<String>> getTemplatesOnPage(Wiki wiki, Collection<String> titles) {
        return parsePropToSingle(getContProp(wiki, titles, WQuery.TEMPLATES, null, "templates"));
    }

    /**
     * Gets the global usage of a file.
     *
     * @param wiki The wiki object to use
     * @param titles The titles to query
     * @return A list of results keyed by title. The inner tuple is of the form (title, shorthand url notation).
     */
    public static Map<String, List<Tuple<String, String>>> globalUsage(Wiki wiki, Collection<String> titles) {
        return parsePropToDouble(getContProp(wiki, titles, WQuery.GLOBALUSAGE, null, "globalusage"), "title", "wiki");
    }

    /**
     * Resolves title redirects on a Wiki.
     *
     * @param wiki The Wiki to run the query against
     * @param titles The titles to attempt resolving.
     * @return A Map where each key is the original title, and the value is the resolved title.
     */
    public static Map<String, String> resolveRedirects(Wiki wiki, Collection<String> titles) {
        Map<String, String> l = new HashMap<>();
        for (String s : titles)
            l.put(s, s);

        getNoContList(wiki, titles, WQuery.RESOLVEREDIRECT, null, "titles", "redirects")
                .forEach(jo -> l.put(GSONP.getStr(jo, "from"), GSONP.getStr(jo, "to")));

        return l;
    }

    /**
     * Gets duplicates of a file.
     *
     * @param wiki The wiki object to use
     * @param localOnly Set to true if you only want to look for files in the local repository.
     * @param titles The titles to query
     * @return A list of results keyed by title.
     */
    public static Map<String, List<String>> getDuplicatesOf(Wiki wiki, boolean localOnly, Collection<String> titles) {
        Map<String, String> pl = new HashMap<>();
        if (localOnly)
            pl.put("dflocalonly", "");

        Map<String, List<String>> l = parsePropToSingle(getContProp(wiki, titles, WQuery.DUPLICATEFILES, pl, "duplicatefiles"),
                "name");
        l.forEach((k, v) -> v.replaceAll(s -> wiki.convertIfNotInNS(s.replace('_', ' '), NS.FILE)));

        return l;
    }

    /**
     * Gets shared (non-local) duplicates of a file. PRECONDITION: The Wiki this query is run against has the
     * <a href="https://www.mediawiki.org/wiki/Extension:GlobalUsage">GlobalUsage</a> extension installed.
     *
     * @param wiki The wiki object to use
     * @param titles The titles to query
     * @return A list of results keyed by title.
     */
    public static Map<String, List<String>> getSharedDuplicatesOf(Wiki wiki, Collection<String> titles) {
        Map<String, List<Tuple<String, String>>> xl = parsePropToDouble(
                getContProp(wiki, titles, WQuery.DUPLICATEFILES, null, "duplicatefiles"), "name", "shared");

        Map<String, List<String>> l = new HashMap<>();
        xl.forEach((k, v) -> l.put(k, v.stream().filter(t -> t.y != null).map(t -> wiki.convertIfNotInNS(t.x.replace('_', ' '), NS.FILE)).collect(Collectors.toList())));
        return l;
    }

    /**
     * Gets a text extract (the lead paragraph) of a page.
     *
     * @param wiki The Wiki object to use.
     * @param titles The titles to get a text extract for.
     * @return A Map of results keyed by title. A null mapping means that the page doesn't exist or is not eligible for
     * text extract.
     */
    public static Map<String, String> getTextExtracts(Wiki wiki, Collection<String> titles) {
        Map<String, String> l = new HashMap<>();
        getNoContProp(wiki, titles, WQuery.TEXTEXTRACTS, null, "extract")
                .forEach((k, v) -> l.put(k, v == null ? null : v.getAsString()));

        return l;
    }
}