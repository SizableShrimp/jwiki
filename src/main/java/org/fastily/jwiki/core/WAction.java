package org.fastily.jwiki.core;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.HttpUrl;
import okhttp3.Response;
import okio.BufferedSource;
import okio.Okio;
import org.fastily.jwiki.dwrap.TokenizedResponse;
import org.fastily.jwiki.util.FL;
import org.fastily.jwiki.util.GSONP;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Static methods to perform changes to a Wiki.
 *
 * @author Fastily
 */
public class WAction {
    private static final int TRUNCATED = (int) Math.pow(2, 12);

    /**
     * All static methods, constructors disallowed.
     */
    private WAction() {

    }

    /**
     * {@code POST} an action
     *
     * @param wiki The Wiki to work on.
     * @param action The type of action to perform. This is the literal API action
     * @param applyToken Set true to apply {@code wiki}'s edit token
     * @param form The form data to post. This should not be URL-encoded
     * @return An {@link AReply} object holding the response data and whether it was a success.
     */
    public static AReply postAction(Wiki wiki, String action, boolean applyToken, String... form) {
        return postAction(wiki, action, applyToken, FL.pMap(form));
    }

    /**
     * {@code POST} an action
     *
     * @param wiki The Wiki to work on.
     * @param action The type of action to perform. This is the literal API action
     * @param applyToken Set true to apply {@code wiki}'s edit token
     * @param form The form data to post. This should not be URL-encoded
     * @return An {@link AReply} object holding the response data and whether it was a success.
     */
    public static AReply postAction(Wiki wiki, String action, boolean applyToken, HashMap<String, String> form) {
        HashMap<String, String> fl = FL.pMap("format", "json");
        if (applyToken)
            fl.put("token", wiki.conf.token);

        fl.putAll(form);

        return doAction(wiki, action, fl, true, 1);
    }

    /**
     * {@code GET} an action
     *
     * @param wiki The Wiki to work on.
     * @param action The type of action to perform. This is the literal API action
     * @param applyToken Set true to apply {@code wiki}'s edit token
     * @param params The parameter data to use. This should not be URL-encoded
     * @return An {@link AReply} object holding the response data and whether it was a success.
     */
    public static AReply getAction(Wiki wiki, String action, boolean applyToken, String... params) {
        return getAction(wiki, action, applyToken, FL.pMap(params));
    }

    /**
     * {@code GET} an action
     *
     * @param wiki The Wiki to work on.
     * @param action The type of action to perform. This is the literal API action
     * @param applyToken Set true to apply {@code wiki}'s edit token
     * @param params The parameter data to use. This should not be URL-encoded
     * @return An {@link AReply} object holding the response data and whether it was a success.
     */
    public static AReply getAction(Wiki wiki, String action, boolean applyToken, HashMap<String, String> params) {
        HashMap<String, String> fl = FL.pMap("format", "json", "action", action);
        if (applyToken)
            fl.put("token", wiki.conf.token);

        fl.putAll(params);

        return doAction(wiki, action, fl, false, 1);
    }

    private static AReply doAction(Wiki wiki, String action, HashMap<String, String> fl, boolean isPOST, int upperLimit) {
        try {
            TokenizedResponse response = isPOST
                    ? wiki.apiclient.basicTokenizedPOST(FL.pMap("action", action), fl)
                    : wiki.apiclient.basicTokenizedGET(fl);
            JsonObject result = response.getJsonBody();

            if (isPOST) {
                if (WikiLogger.isTraceEnabled())
                    WikiLogger.trace(wiki, "Received response from POST: {}", GSONP.gsonPP.toJson(result));
            } else {
                if (WikiLogger.isTraceEnabled())
                    WikiLogger.trace(wiki, "Received response from GET: {}", GSONP.gsonPP.toJson(result));
            }

            AReply reply = AReply.wrap(action, result);
            if (reply.isError() && "ratelimited".equals(reply.getErrorCode())) {
                // See https://en.wikipedia.org/wiki/Exponential_backoff
                if (upperLimit != TRUNCATED)
                    upperLimit <<= 1;
                int wait = ThreadLocalRandom.current().nextInt(upperLimit); // exclusive bounds means this is a proper truncated binary exponential backoff impl
                WikiLogger.warn(wiki, "Ratelimited by server when performing {} '{}', sleeping {} seconds.", isPOST ? "POST" : "GET", action, wait);
                Thread.sleep(wait * 1000L);
                return doAction(wiki, action, fl, isPOST, upperLimit);
            } else {
                return reply;
            }
        } catch (Throwable e) {
            if (isPOST) {
                WikiLogger.error(wiki, "Error when POSTing action", e);
            } else {
                WikiLogger.error(wiki, "Error when GETing action", e);
            }
            return AReply.NULL_REPLY;
        }
    }

    /**
     * Adds text to a page.
     *
     * @param wiki The Wiki to work on.
     * @param title The title to edit.
     * @param text The text to add.
     * @param summary The edit summary to use
     * @param append Set True to append text, or false to prepend text.
     * @return An {@link AReply} object holding the response data and whether it was a success.
     */
    protected static AReply addText(Wiki wiki, String title, String text, String summary, boolean append) {
        WikiLogger.info(wiki, "Adding text to {}", title);

        HashMap<String, String> pl = FL.pMap("title", title, append ? "appendtext" : "prependtext", text, "summary", summary);
        if (wiki.conf.isBot)
            pl.put("bot", "");

        return postAction(wiki, "edit", true, pl);
    }

    /**
     * Edits a page.
     *
     * @param wiki The Wiki to work on.
     * @param title The title to edit
     * @param text The text to replace the text of {@code title} with.
     * @param summary The edit summary to use
     * @return An {@link AReply} object holding the response data and whether it was a success.
     */
    @NotNull
    protected static AReply edit(Wiki wiki, String title, String text, String summary) {
        WikiLogger.info(wiki, "Editing {}", title);

        HashMap<String, String> pl = FL.pMap("title", title, "text", text, "summary", summary);
        if (wiki.conf.isBot)
            pl.put("bot", "");

        AReply reply = null;
        for (int i = 0; i < 5; i++) {
            reply = postAction(wiki, "edit", true, pl);
            if (reply.isSuccess()) {
                return reply;
            } else if (reply.isUnknown()) {
                WikiLogger.warn(wiki, "Got an unknown response: {}, retrying: {}", reply.getResponse().toString(), i);
            } else if (reply.isError()) {
                String code = reply.getErrorCode();
                if ("cascadeprotected".equals(code) || "protectedpage".equals(code)) {
                    WikiLogger.error(wiki, "{} is protected, cannot edit.", title);
                    return reply;
                }
            }
        }

        WikiLogger.error(wiki, "Could not edit '{}', aborting.", title);
        return reply; // Return the last attempted reply
    }

    /**
     * Moves a page.
     *
     * @param wiki The Wiki objec to use
     * @param title The original title to move
     * @param newTitle The new title to move the old page to
     * @param moveTalk Flag indicating if {@code title}'s talk page (assuming it exists) should be moved. Optional, set false to disable.
     * @param moveSubpages Flag indicating if {@code title}'s subpages should also be moved. Requires admin/pagemover rights, otherwise this does nothing. Optional, set false to disable.
     * @param supressRedirect Flag indicating if a redirect to {@code newTitle} should be automatically generated at {@code title}. Requires admin/pagemover rights, otherwise this does nothing.
     * Optional, set false to disable.
     * @param reason The edit summary to use
     * @return An {@link AReply} object holding the response data and whether it was a success.
     */
    protected static AReply move(Wiki wiki, String title, String newTitle, boolean moveTalk, boolean moveSubpages, boolean supressRedirect, String reason) {
        WikiLogger.info(wiki, "Moving {} to {}", title, newTitle);

        HashMap<String, String> pl = FL.pMap("from", title, "to", newTitle, "reason", reason);

        if (moveTalk)
            pl.put("movetalk", "1");
        if (moveSubpages)
            pl.put("movesubpages", "1");
        if (supressRedirect)
            pl.put("noredirect", "1");

        return postAction(wiki, "move", true, pl);
    }

    /**
     * Deletes a page. Wiki must be logged in and have administrator permissions for this to succeed.
     *
     * @param wiki The Wiki to work on.
     * @param title The title to delete
     * @param reason The log summary to use
     * @return An {@link AReply} object holding the response data and whether it was a success.
     */
    protected static AReply delete(Wiki wiki, String title, String reason) {
        WikiLogger.info(wiki, "Deleting {}", title);

        return postAction(wiki, "delete", true, FL.pMap("title", title, "reason", reason));
    }

    /**
     * Undelete a page. Wiki must be logged in and have administrator permissions for this to succeed.
     *
     * @param wiki The Wiki to work on.
     * @param title The title to delete
     * @param reason The log summary to use
     * @return An {@link AReply} object holding the response data and whether it was a success.
     */
    protected static AReply undelete(Wiki wiki, String title, String reason) {
        WikiLogger.info(wiki, "Restoring {}", title);

        return postAction(wiki, "undelete", true, FL.pMap("title", title, "reason", reason));
    }

    /**
     * Purges the cache of pages.
     *
     * @param wiki The Wiki to work on.
     * @param titles The title(s) to purge.
     */
    protected static AReply purge(Wiki wiki, ArrayList<String> titles) {
        WikiLogger.info(wiki, "Purging {}", titles);

        HashMap<String, String> pl = FL.pMap("titles", FL.pipeFence(titles));
        return postAction(wiki, "purge", false, pl);
    }

    /**
     * Uploads a file. Caution: overwrites files automatically.
     *
     * @param wiki The Wiki to work on.
     * @param title The title to upload the file to, excluding the {@code File:} prefix.
     * @param desc The text to put on the newly uploaded file description page
     * @param summary The edit summary to use when uploading a new file.
     * @param file The Path to the file to upload.
     * @return An {@link AReply} object holding the response data and whether it was a success.
     */
    @NotNull
    protected static AReply upload(Wiki wiki, String title, String desc, String summary, Path file) {
        WikiLogger.info(wiki, "Uploading {}", file);

        try {
            ChunkManager cm = new ChunkManager(file);

            String filekey = null;
            String fn = file.getFileName().toString();

            Chunk c;
            while ((c = cm.nextChunk()) != null) {
                WikiLogger.trace(wiki, "Uploading chunk [{} of {}] of '{}'", cm.chunkCnt, cm.totalChunks, file);

                HashMap<String, String> pl = FL.pMap("format", "json", "filename", title, "token", wiki.conf.token, "ignorewarnings", "1", "stash", "1", "offset", "" + c.offset, "filesize",
                        "" + c.filesize);
                if (filekey != null)
                    pl.put("filekey", filekey);

                for (int i = 0; i < 5; i++)
                    try {
                        Response r = wiki.apiclient.multiPartFilePOST(FL.pMap("action", "upload"), pl, fn, c.bl);
                        if (!r.isSuccessful()) {
                            WikiLogger.error(wiki, "Bad response from server: {}", r.code());
                            continue;
                        }

                        filekey = GSONP.getStr(JsonParser.parseString(r.body().string()).getAsJsonObject().getAsJsonObject("upload"), "filekey");
                        if (filekey != null)
                            break;
                    } catch (Throwable e) {
                        WikiLogger.error(wiki, "Encountered an error, retrying - {}", i, e);
                    }
            }

            for (int i = 0; i < 3; i++) {
                WikiLogger.info(wiki, "Unstashing '{}' as '{}'", filekey, title);

                AReply reply = postAction(wiki, "upload", true, FL.pMap("filename", title, "text", desc, "comment", summary, "filekey", filekey, "ignorewarnings", "true"));
                if (reply.isSuccess())
                    return reply;

                WikiLogger.error(wiki, "Encountered an error while unstashing with response {}, retrying - {}", reply.getResponse(), i);
            }
        } catch (Throwable e) {
            WikiLogger.error(wiki, "Error while uploading", e);
        }

        return AReply.NULL_REPLY;
    }

    /**
     * Upload a file by URL. The URL must be on the upload by url whitelist for the target Wiki or this method will automatically fail.
     *
     * @param wiki The Wiki object to use
     * @param url The URL the target file is located at.
     * @param title The title to upload to.
     * @param desc The text to put on the file description page
     * @param summary The edit summary
     * @return An {@link AReply} object holding the response data and whether it was a success.
     */
    protected static AReply uploadByUrl(Wiki wiki, HttpUrl url, String title, String desc, String summary) {
        WikiLogger.info(wiki, "Uploading '{}' to '{}'", url, title);

        return postAction(wiki, "upload", true, FL.pMap("filename", title, "text", desc, "comment", summary, "ignorewarnings", "true", "url", url.toString()));
    }

    /**
     * Creates and manages Chunk Objects for {@link WAction#upload(Wiki, String, String, String, Path)}.
     *
     * @author Fastily
     */
    private static final class ChunkManager {
        /**
         * The default chunk size is 4 Mb
         */
        private static final int chunksize = 1024 * 1024 * 4;

        /**
         * The source file stream
         */
        private final BufferedSource src;

        /**
         * The current Chunk offset, in bytes
         */
        private long offset = 0;

        /**
         * The file size (in bytes) of the file being uploaded
         */
        private final long filesize;

        /**
         * The total number of Chunk objects to upload
         */
        private final long totalChunks;

        /**
         * Counts the number of chunks created so far.
         */
        private int chunkCnt = 0;

        /**
         * Creates a new Chunk Manager. Create a new ChunkManager for every upload.
         *
         * @param fn The local file to upload
         * @throws IOException I/O error.
         */
        private ChunkManager(Path fn) throws IOException {
            filesize = Files.size(fn);
            src = Okio.buffer(Okio.source(fn, StandardOpenOption.READ));
            totalChunks = filesize / chunksize + ((filesize % chunksize) > 0 ? 1 : 0);
        }

        /**
         * Determine if there are still Chunk objects to upload.
         *
         * @return True if there are still Chunk objects to upload.
         */
        private boolean has() {
            return offset < filesize;
        }

        /**
         * Create and return the next sequential Chunk to upload.
         *
         * @return The next sequential Chunk to upload, or null on error or if there are no more chunks to upload.
         */
        private Chunk nextChunk() {
            if (!has())
                return null;

            try {
                Chunk c = new Chunk(offset, filesize, ++chunkCnt == totalChunks ? src.readByteArray() : src.readByteArray(chunksize));

                offset += chunksize;
                // chunkCnt++;

                if (!has())
                    src.close();

                return c;

            } catch (Throwable e) {
                WikiLogger.error(null, "Error while uploading chunk", e);
                return null;
            }
        }
    }

    /**
     * Represents an individual chunk to upload
     *
     * @author Fastily
     */
    private static final class Chunk {
        /**
         * The offset and filesize (both in bytes)
         */
        protected final long offset, filesize;

        /**
         * The raw binary data for this Chunk
         */
        protected final byte[] bl;

        /**
         * Creates a new Chunk to upload
         *
         * @param offset The byte offset of this Chunk
         * @param filesize The total file size of the file this Chunk belongs to
         * @param bl The raw binary data contained by this chunk
         */
        private Chunk(long offset, long filesize, byte[] bl) {
            this.offset = offset;
            this.filesize = filesize;

            this.bl = bl;
        }
    }
}