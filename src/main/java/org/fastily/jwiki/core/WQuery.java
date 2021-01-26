package org.fastily.jwiki.core;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.fastily.jwiki.util.FL;
import org.fastily.jwiki.util.GSONP;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Wraps the various functions of API functions of {@code action=query}.
 *
 * @author Fastily
 */
public class WQuery {
    /**
     * Default parameters for getting category size info
     */
    public static final QTemplate ALLOWEDFILEXTS = new QTemplate(FL.pMap("meta", "siteinfo", "siprop", "fileextensions"),
            "fileextensions");

    /**
     * Default parameters for getting category size info
     */
    public static final QTemplate ALLPAGES = new QTemplate(FL.pMap("list", "allpages"), "aplimit", "allpages");

    /**
     * Default parameters for getting category size info
     */
    public static final QTemplate CATEGORYINFO = new QTemplate(FL.pMap("prop", "categoryinfo", "titles", null), "categoryinfo");

    /**
     * Default parameters for listing category members
     */
    public static final QTemplate CATEGORYMEMBERS = new QTemplate(FL.pMap("list", "categorymembers", "cmtitle", null), "cmlimit",
            "categorymembers");

    /**
     * Default parameters for getting Namespace information on a Wiki.
     */
    public static final QTemplate NAMESPACES = new QTemplate(FL.pMap("meta", "siteinfo", "siprop", "namespaces|namespacealiases"),
            null);

    /**
     * Default parameters for getting duplicate files
     */
    public static final QTemplate DUPLICATEFILES = new QTemplate(FL.pMap("prop", "duplicatefiles", "titles", null), "dflimit",
            "duplicatefiles");

    /**
     * Default parameters for determining if a page exists.
     */
    public static final QTemplate EXISTS = new QTemplate(FL.pMap("prop", "pageprops", "ppprop", "missing", "titles", null), null);

    /**
     * Default parameters for fetching external links on a page
     */
    public static final QTemplate EXTLINKS = new QTemplate(FL.pMap("prop", "extlinks", "elexpandurl", "1", "titles", null), "ellimit",
            "extlinks");

    /**
     * Default parameters for getting file usage
     */
    public static final QTemplate FILEUSAGE = new QTemplate(FL.pMap("prop", "fileusage", "titles", null), "fulimit", "fileusage");

    /**
     * Default parameters for getting global usage of a file
     */
    public static final QTemplate GLOBALUSAGE = new QTemplate(FL.pMap("prop", "globalusage", "titles", null), "gulimit", "globalusage");

    /**
     * Default parameters for getting files on a page
     */
    public static final QTemplate IMAGES = new QTemplate(FL.pMap("prop", "images", "titles", null), "imlimit", "images");

    /**
     * Default parameters for getting image info of a file.
     */
    public static final QTemplate IMAGEINFO = new QTemplate(
            FL.pMap("prop", "imageinfo", "iiprop", "canonicaltitle|url|size|sha1|mime|user|timestamp|comment", "titles", null), "iilimit",
            "imageinfo");

    /**
     * Default parameters for getting links to a page
     */
    public static final QTemplate LINKSHERE = new QTemplate(
            FL.pMap("prop", "linkshere", "lhprop", "title", "lhshow", null, "titles", null), "lhlimit", "linkshere");

    /**
     * Default parameters for getting links on a page
     */
    public static final QTemplate LINKSONPAGE = new QTemplate(FL.pMap("prop", "links", "titles", null), "pllimit", "links");

    /**
     * Default parameters for listing logs.
     */
    public static final QTemplate LOGEVENTS = new QTemplate(FL.pMap("list", "logevents"), "lelimit", "logevents");

    /**
     * Default parameters for getting page categories.
     */
    public static final QTemplate PAGECATEGORIES = new QTemplate(FL.pMap("prop", "categories", "titles", null), "cllimit",
            "categories");

    /**
     * Default parameters for getting page text.
     */
    public static final QTemplate PAGETEXT = new QTemplate(FL.pMap("prop", "revisions", "rvprop", "content", "titles", null), null);

    /**
     * Default parameters for listing protected titles.
     */
    public static final QTemplate PROTECTEDTITLES = new QTemplate(
            FL.pMap("list", "protectedtitles", "ptprop", "timestamp|level|user|comment"), "ptlimit", "protectedtitles");

    /**
     * Default parameters for listing the results of querying Special pages.
     */
    public static final QTemplate QUERYPAGES = new QTemplate(FL.pMap("list", "querypage", "qppage", null), "qplimit", "querypage");

    /**
     * Default parameters for listing random pages
     */
    public static final QTemplate RANDOM = new QTemplate(FL.pMap("list", "random", "rnfilterredir", "nonredirects"), "rnlimit",
            "random");

    /**
     * Default parameters for listing recent changes.
     */
    public static final QTemplate RECENTCHANGES = new QTemplate(
            FL.pMap("list", "recentchanges", "rcprop", "title|timestamp|user|comment", "rctype", "edit|new|log"), "rclimit",
            "recentchanges");

    /**
     * Default parameters for resolving redirects
     */
    public static final QTemplate RESOLVEREDIRECT = new QTemplate(FL.pMap("redirects", "", "titles", null), "redirects");

    /**
     * Default parameters for listing page revisions
     */
    public static final QTemplate REVISIONS = new QTemplate(
            FL.pMap("prop", "revisions", "rvprop", "comment|content|ids|timestamp|user", "titles", null), "rvlimit", "revisions");

    /**
     * Default parameters for listing searches
     */
    public static final QTemplate SEARCH = new QTemplate(FL.pMap("list", "search", "srprop", "", "srnamespace", "*", "srsearch", null),
            "srlimit", "search");

    /**
     * Default parameters for getting templates on a page
     */
    public static final QTemplate TEMPLATES = new QTemplate(FL.pMap("prop", "templates", "tiprop", "title", "titles", null), "tllimit",
            "templates");

    /**
     * Default parameters for getting text extracts from a page
     */
    public static final QTemplate TEXTEXTRACTS = new QTemplate(
            FL.pMap("prop", "extracts", "exintro", "1", "explaintext", "1", "titles", null), "exlimit", "extract");

    /**
     * Default parameters for getting a csrf token.
     */
    public static final QTemplate TOKENS_CSRF = new QTemplate(FL.pMap("meta", "tokens", "type", "csrf"), null);

    /**
     * Default parameters for getting a login token.
     */
    public static final QTemplate TOKENS_LOGIN = new QTemplate(FL.pMap("meta", "tokens", "type", "login"), null);

    /**
     * Default parameters for getting a page's transclusions.
     */
    public static final QTemplate TRANSCLUDEDIN = new QTemplate(FL.pMap("prop", "transcludedin", "tiprop", "title", "titles", null),
            "tilimit", "transcludedin");

    /**
     * Default parameters for listing user contributions.
     */
    public static final QTemplate USERCONTRIBS = new QTemplate(FL.pMap("list", "usercontribs", "ucuser", null), "uclimit",
            "usercontribs");

    /**
     * Default parameters for getting a user's username and id.
     */
    public static final QTemplate USERINFO = new QTemplate(FL.pMap("meta", "userinfo"), null);

    /**
     * Default parameters for listing users and their rights.
     */
    public static final QTemplate USERRIGHTS = new QTemplate(FL.pMap("list", "users", "usprop", "groups", "ususers", null), "users");

    /**
     * Default parameters for listing user uploads
     */
    public static final QTemplate USERUPLOADS = new QTemplate(FL.pMap("list", "allimages", "aisort", "timestamp", "aiuser", null),
            "ailimit", "allimages");

    /**
     * Default parameters for listing pages with prefix.
     */
    public static final QTemplate PREFIXSEARCH = new QTemplate(FL.pMap("list", "prefixsearch"), "pslimit", "prefixsearch");

    /**
     * Type describing a HashMap with a String key and String value.
     */
    private static final Type strMapT = new TypeToken<HashMap<String, String>>() {
    }.getType();

    /**
     * The master parameter list. Tracks current query status.
     */
    private final HashMap<String, String> pl = FL.pMap("action", "query", "format", "json");

    /**
     * The List of limit Strings.
     */
    private final ArrayList<String> limStrList = new ArrayList<>();

    /**
     * The Wiki object to perform queries with
     */
    private final Wiki wiki;

    /**
     * Flag indicating if this query can be continued.
     */
    private boolean canCont = true;

    /**
     * Tracks and limits entries returned, if applicable.
     */
    private int queryLimit, totalLimit = -1, currCount = 0;

    /**
     * Constructor, creates a new WQuery
     *
     * @param wiki The Wiki object to perform queries with
     * @param qut The QueryUnitTemplate objects to instantiate this WQuery with.
     */
    public WQuery(Wiki wiki, QTemplate... qut) {
        this.wiki = wiki;
        this.queryLimit = wiki.conf.maxResultLimit;

        for (QTemplate qt : qut) {
            pl.putAll(qt.defaultFields);
            if (qt.limString != null)
                limStrList.add(qt.limString);
        }
    }

    /**
     * Constructor, creates a limited WQuery.
     *
     * @param wiki The Wiki object to perform queries with.
     * @param totalLimit The maximum number of items to return until WQuery is exhausted. Actual number of items returned
     * may be less. Optional, disable with -1.
     * @param qut The QueryUnitTemplate objects to instantiate this WQuery with.
     */
    public WQuery(Wiki wiki, int totalLimit, QTemplate... qut) {
        this(wiki, qut);
        this.totalLimit = totalLimit;
    }

    /**
     * Test if this WQuery has any queries remaining.
     *
     * @return True if this WQuery can still be used to make continuation queries.
     */
    public boolean has() {
        return canCont;
    }

    /**
     * Attempts to perform the next query in this sequence.
     *
     * @return A JsonObject with the response from the server, or null if something went wrong.
     */
    public QReply next() {
        // sanity check
        if (pl.containsValue(null))
            throw new IllegalStateException(String.format("Fill in *all* the null fields -> %s", pl));
        else if (!canCont)
            return null;

        try {
            if (totalLimit > 0 && (currCount += queryLimit) > totalLimit) {
                adjustLimit(queryLimit - (currCount - totalLimit));
                canCont = false;
            }

            JsonObject result = wiki.apiclient.basicTokenizedGET(pl).getJsonBody();
            if (result.has("continue"))
                pl.putAll(GSONP.gson.fromJson(result.getAsJsonObject("continue"), strMapT));
            else
                canCont = false;

            if (WikiLogger.isTraceEnabled())
                WikiLogger.trace(wiki, GSONP.gsonPP.toJson(result));

            return QReply.wrap(result);
        } catch (Throwable e) {
            WikiLogger.error(wiki, "Error when querying API", e);
            return null;
        }
    }

    /**
     * Sets a key-value pair. DO NOT URL-encode. These are the parameters that will be passed to the MediaWiki API.
     *
     * @param key The parameter key to set
     * @param value The parameter value to set
     * @return This WQuery. Useful for chaining.
     */
    public WQuery set(String key, String value) {
        pl.put(key, value);
        return this;
    }

    /**
     * Sets a key-values pair. DO NOT URL-encode. These are the parameters that will be passed to the MediaWiki API.
     *
     * @param key The parameter key to set
     * @param values The parameter value to set; these will be pipe-fenced.
     * @return This WQuery. Useful for chaining.
     */
    public WQuery set(String key, List<String> values) {
        return set(key, FL.pipeFence(values));
    }

    /**
     * Configure this WQuery to fetch a maximum of {@code limit} items per query. Does nothing if this query does not use
     * limit Strings.
     *
     * @param limit The new limit. Set as -1 to get the maximum number of items per query.
     * @return This WQuery, for chaining convenience.
     */
    public WQuery adjustLimit(int limit) {
        String limitString;
        if (limit <= 0 || limit > wiki.conf.maxResultLimit) {
            limitString = "max";
            queryLimit = wiki.conf.maxResultLimit;
        } else {
            limitString = "" + limit;
            queryLimit = limit;
        }

        for (String s : limStrList)
            pl.put(s, limitString);

        return this;
    }

}
