package org.fastily.jwiki.core;

import com.google.gson.JsonObject;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.fastily.jwiki.dwrap.TokenizedResponse;

import java.io.IOException;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Functions which perform {@code GET} and {@code POST} requests to the MediaWiki api and returns Response objects in a
 * suitable format.
 *
 * @author Fastily
 */
public class ApiClient {
    /**
     * MediaType for {@code application/octet-stream}.
     */
    private static final MediaType octetstream = MediaType.parse("application/octet-stream");

    /**
     * HTTP client used for all requests.
     */
    protected final OkHttpClient client;

    /**
     * The Wiki object tied to this ApiClient.
     */
    private final Wiki wiki;

    private final JwikiCookieJar cookieJar;

    /**
     * Constructor, create a new ApiClient for a Wiki instance.
     *
     * @param wiki The Wiki object this ApiClient is associated with.
     * @param proxy The proxy to use. Optional param - set null to disable.
     */
    protected ApiClient(Wiki wiki, Proxy proxy) {
        this.wiki = wiki;

        this.cookieJar = new JwikiCookieJar();
        OkHttpClient.Builder builder = new OkHttpClient.Builder().cookieJar(this.cookieJar).readTimeout(2, TimeUnit.MINUTES);
        if (proxy != null)
            builder.proxy(proxy);

        this.client = builder.build();
    }

    /**
     * Constructor, derives an ApiClient from a source Wiki. Useful for {@code centralauth} login/credential sharing.
     *
     * @param from The source Wiki to create the new Wiki with
     * @param to The new Wiki to apply {@code from}'s ApiClient settings on.
     */
    protected ApiClient(Wiki from, Wiki to) {
        this.wiki = to;
        this.client = from.apiclient.client;
        this.cookieJar = from.apiclient.cookieJar;

        Map<String, String> l = new HashMap<>();
        this.cookieJar.getCj().get(from.conf.hostname).forEach((k, v) -> {
            if (k.contains("centralauth"))
                l.put(k, v);
        });

        this.cookieJar.getCj().put(this.wiki.conf.hostname, l);
    }

    /**
     * Create a basic Request template which serves as the basis for any Request objects.
     *
     * @param params Any URL parameters (not URL-encoded).
     * @return A new Request.Builder with default values needed to hit MediaWiki API endpoints.
     */
    private Request.Builder startReq(Map<String, String> params) {
        HttpUrl.Builder hb = this.wiki.conf.baseURL.newBuilder();
        params.forEach(hb::addQueryParameter);

        return new Request.Builder().url(hb.build()).header("User-Agent", this.wiki.conf.userAgent);
    }

    /**
     * Basic {@code GET} to the MediaWiki api.
     *
     * @param params Any URL parameters (not URL-encoded).
     * @return A Response object with the result of this Request.
     * @throws IOException Network error
     */
    protected Response basicGET(Map<String, String> params) throws IOException {
        return this.client.newCall(startReq(params).get().build()).execute();
    }

    /**
     * Basic {@code GET} to the MediaWiki API with a retry if the login token has expired.
     *
     * @param params Any URL parameters (not URL-encoded).
     * @param tokenKey The key to put in the {@code param} data that maps to the token. If null, no token is inserted.
     * @return A {@link TokenizedResponse} object with the result of this Request.
     * @throws IOException Network error
     */
    protected TokenizedResponse basicTokenizedGET(Map<String, String> params, String tokenKey) throws IOException {
        Map<String, String> copiedParams = params instanceof HashMap ? params : new HashMap<>(params);
        if (tokenKey != null)
            copiedParams.put(tokenKey, wiki.conf.token);

        TokenizedResponse response = new TokenizedResponse(this.basicGET(copiedParams));

        if (isBadToken(response) && this.wiki.username != null && this.wiki.password != null) {
            this.wiki.internalLogin();
            if (tokenKey != null)
                copiedParams.put(tokenKey, wiki.conf.token);
            // Only attempt once after refreshing login
            return new TokenizedResponse(this.basicGET(copiedParams));
        }

        return response;
    }

    /**
     * Basic form-data {@code POST} to the MediaWiki API.
     *
     * @param params Any URL parameters (not URL-encoded).
     * @param form The Key-Value form parameters to {@code POST}.
     * @return A Response object with the result of this Request.
     * @throws IOException Network error
     */
    protected Response basicPOST(Map<String, String> params, Map<String, String> form) throws IOException {
        FormBody.Builder fb = new FormBody.Builder();
        form.forEach(fb::add);

        return this.client.newCall(startReq(params).post(fb.build()).build()).execute();
    }

    /**
     * Basic form-data {@code POST} to the MediaWiki API with a retry if the login token has expired.
     *
     * @param params Any URL parameters (not URL-encoded).
     * @param form The Key-Value form parameters to {@code POST}.
     * @param tokenKey The key to put in the form data that maps to the token. If null, no token is inserted.
     * @return A {@link TokenizedResponse} object with the result of this Request.
     * @throws IOException Network error
     */
    protected TokenizedResponse basicTokenizedPOST(Map<String, String> params, Map<String, String> form, String tokenKey) throws IOException {
        Map<String, String> copiedForm = form instanceof HashMap ? form : new HashMap<>(form);
        if (tokenKey != null)
            copiedForm.put(tokenKey, wiki.conf.token);

        TokenizedResponse response = new TokenizedResponse(this.basicPOST(params, copiedForm));

        if (isBadToken(response) && this.wiki.username != null && this.wiki.password != null) {
            this.wiki.internalLogin();
            if (tokenKey != null)
                copiedForm.put(tokenKey, wiki.conf.token);
            // Only attempt once after refreshing login
            return new TokenizedResponse(this.basicPOST(params, copiedForm));
        }

        return response;
    }

    /**
     * Performs a multi-part file {@code POST}.
     *
     * @param params Any URL parameters (not URL-encoded).
     * @param form The Key-Value form parameters to {@code POST}.
     * @param fn The system name of the file to {@code POST}
     * @param chunk The raw byte data associated with this file which will be sent in this {@code POST}.
     * @return A Response with the results of this {@code POST}.
     * @throws IOException Network error
     */
    protected Response multiPartFilePOST(Map<String, String> params, Map<String, String> form, String fn, byte[] chunk)
            throws IOException {
        MultipartBody.Builder mpb = new MultipartBody.Builder().setType(MultipartBody.FORM);
        form.forEach(mpb::addFormDataPart);

        mpb.addFormDataPart("chunk", fn, RequestBody.create(chunk, octetstream));

        Request r = startReq(params).post(mpb.build()).build();
        return this.client.newCall(r).execute();
    }

    protected static boolean isBadToken(TokenizedResponse response) {
        JsonObject json = response.getJsonBody().getAsJsonObject();

        if (json.has("error")) {
            JsonObject error = json.getAsJsonObject("error");
            String code = error.getAsJsonPrimitive("code").getAsString();
            return "badtoken".equals(code);
        }

        return false;
    }

    public JwikiCookieJar getCookieJar() {
        return this.cookieJar;
    }

    /**
     * Basic CookieJar policy for use with jwiki.
     *
     * @author Fastily
     */
    public static class JwikiCookieJar implements CookieJar {
        private final Map<String, Map<String, String>> cj = new HashMap<>();

        /**
         * Constructor, create a new JwikiCookieJar
         */
        private JwikiCookieJar() {

        }

        /**
         * Called when receiving a Response from the Api.
         */
        @Override
        public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
            String host = url.host();

            Map<String, String> m = this.cj.computeIfAbsent(host, k -> new HashMap<>());
            for (Cookie c : cookies)
                m.put(c.name(), c.value());
        }

        /**
         * Called when creating a new Request to the Api.
         */
        @Override
        public List<Cookie> loadForRequest(HttpUrl url) {
            String host = url.host();
            if (this.cj.containsKey(host)) {
                return this.cj.get(host).entrySet().stream()
                        .map(e -> new Cookie.Builder().name(e.getKey()).value(e.getValue()).domain(host).build())
                        .collect(Collectors.toList());
            } else {
                return new ArrayList<>();
            }
        }

        /**
         * Internal Map tracking cookies. Legend - [ domain : [ key : value ] ].
         * @return the internal Map tracking cookies.
         */
        public Map<String, Map<String, String>> getCj() {
            return this.cj;
        }
    }
}