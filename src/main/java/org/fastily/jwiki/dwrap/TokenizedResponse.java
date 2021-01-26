package org.fastily.jwiki.dwrap;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.OkHttpClient;
import okhttp3.Response;

import java.io.IOException;

/**
 * A wrapper around a {@link Response} that allows for requests to be retried if the login token has expired.
 * <p>
 * <b>NOTE:</b> Attempts to get the response body should be delegated to {@link #getBody()}.
 *
 * @author SizableShrimp
 * @since 2.0.0
 */
public class TokenizedResponse {
    private final Response response;
    private final String body;
    private final JsonObject jsonBody;

    /**
     * Wrap a {@link Response} into a {@link TokenizedResponse} which caches the response body and parses it into JSON.
     *
     * @param response the {@link Response} returned from an {@link OkHttpClient}.
     * @throws IOException if the response body cannot be returned.
     */
    public TokenizedResponse(Response response) throws IOException {
        this.response = response;
        if (response.body() != null) {
            this.body = response.body().string();
            this.jsonBody = JsonParser.parseString(this.body).getAsJsonObject();
            response.close();
        } else {
            this.body = null;
            this.jsonBody = null;
        }
    }

    /**
     * Get the original {@link Response}.
     * {@link #getBody()} or {@link #getJsonBody()} should be used to retrieve the body.
     *
     * @return the original {@link Response}.
     */
    public Response getResponse() {
        return response;
    }

    /**
     * Returns the raw {@link String} body returned from the API.
     *
     * @return the raw {@link String} body returned from the API.
     */
    public String getBody() {
        return body;
    }

    /**
     * Returns the parsed {@link JsonObject} from {@link #getBody()}.
     *
     * @return the parsed {@link JsonObject} from {@link #getBody()}.
     */
    public JsonObject getJsonBody() {
        return jsonBody;
    }
}
