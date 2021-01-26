package org.fastily.jwiki.core;

import com.google.gson.JsonObject;
import org.fastily.jwiki.util.GSONP;

import java.io.IOException;

/**
 * An action reply from a MediaWiki server.
 * Used to check if the reply was a success, an error, or unknown.
 * Stores the related JSON information.
 *
 * @author SizableShrimp
 * @since 2.0.0
 */
public class AReply {
    /**
     * A public constant representing an empty or invalid response from the API, considered to be in an {@link Type#ERROR} state.
     */
    public static final AReply NULL_REPLY = new AReply(null, Type.ERROR, new JsonObject());
    protected final String action;
    protected final Type type;
    protected final JsonObject response;

    protected AReply(String action, Type type, JsonObject response) {
        this.action = action;
        this.type = type;
        this.response = response;
    }

    /**
     * Wrap the {@code action} and {@code response} into an {@link AReply} object.
     *
     * @param action The action of the request, e.g. {@code edit} or {@code delete}.
     * @param response The JSON response from the server, can be from either POST or GET.
     * @return a wrapped {@link AReply} object.
     */
    public static AReply wrap(String action, JsonObject response) {
        if (response == null || response.size() == 0)
            return NULL_REPLY;

        return new AReply(action, getType(action, response), response);
    }

    protected static Type getType(String action, JsonObject response) {
        if (response.has("error")) {
            return Type.ERROR;
        } else if (response.has(action)) {
            return Type.SUCCESS;
        } else {
            return Type.UNKNOWN;
        }
    }

    /**
     * Returns a copy of the {@link JsonObject} associated to the {@code action}'s reply JSON.
     * For the entire response, see {@link #getResponse()}.
     * <p>
     * This method retrieves the {@code action} JSON object from the response JSON object.
     *
     * @return a copy of the {@link JsonObject} associated to the {@code action}'s response.
     * @throws IllegalStateException if the state of this reply is not {@link Type#SUCCESS}.
     * @see #getResponse()
     */
    public JsonObject getSuccessJson() {
        JsonObject json = getInternalSuccessJson();
        return json == null ? new JsonObject() : json.deepCopy();
    }

    protected JsonObject getInternalSuccessJson() {
        requireCorrectType(Type.SUCCESS);

        return this.response.getAsJsonObject(getAction());
    }

    /**
     * Return a copy of the {@link JsonObject} associated to the {@code error} reply JSON.
     * For the entire response, see {@link #getResponse()}.
     * <p>
     * This method retrieves the {@code error} JSON object from the response JSON object.
     *
     * @return a copy of the {@link JsonObject} associated to the {@code error} reply JSON.
     * @throws IllegalStateException if the state of this reply is not {@link Type#ERROR}.
     * @see #getResponse()
     */
    public JsonObject getErrorJson() {
        JsonObject json = getInternalErrorJson();
        return json == null ? new JsonObject() : json.deepCopy();
    }

    protected JsonObject getInternalErrorJson() {
        requireCorrectType(Type.ERROR);

        return this.response.getAsJsonObject("error");
    }

    /**
     * Return a copy the entire {@link JsonObject} response, since the returned JSON was not in a valid format.
     *
     * @return a copy of the entire {@link JsonObject} response, since the returned JSON was not in a valid format.
     * @throws IllegalStateException if the state of this reply is not {@link Type#UNKNOWN}.
     * @see #getResponse()
     */
    public JsonObject getUnknownJson() {
        requireCorrectType(Type.UNKNOWN);

        return getResponse(); // getResponse already returns a deep copy
    }

    /**
     * Return the error {@code code} retrieved from {@link #getErrorJson()}.
     * <p>
     * This method retrieves the {@code code} {@link String} from the error JSON object.
     *
     * @return the error {@code code} retrieved from {@link #getErrorJson()}.
     * @throws IllegalStateException if the state of this reply is not {@link Type#ERROR}.
     * @see #getErrorJson()
     * @see #getErrorInfo()
     */
    public String getErrorCode() {
        requireCorrectType(Type.ERROR);

        return GSONP.getStr(getInternalErrorJson(), "code");
    }

    /**
     * Return the error {@code info} retrieved from {@link #getErrorJson()}.
     * <p>
     * This method retrieves the {@code info} {@link String} from the error JSON object.
     *
     * @return the error {@code info} retrieved from {@link #getErrorJson()}.
     * @throws IllegalStateException if the state of this reply is not {@link Type#ERROR}.
     * @see #getErrorJson()
     * @see #getErrorCode()
     */
    public String getErrorInfo() {
        requireCorrectType(Type.ERROR);

        return GSONP.getStr(getInternalErrorJson(), "info");
    }

    /**
     * Return the {@link #getErrorCode() error code} followed by the {@link #getErrorInfo() error info}, joined by a hyphen.
     *
     * @return the {@link #getErrorCode() error code} followed by the {@link #getErrorInfo() error info}, joined by a hyphen.
     * @throws IllegalStateException if the state of this reply is not {@link Type#ERROR}.
     * @see #getErrorCode()
     * @see #getErrorInfo()
     */
    public String getError() {
        requireCorrectType(Type.ERROR);

        return getErrorCode() + " - " + getErrorInfo();
    }

    /**
     * Return whether this {@link AReply} is an instance of {@link #NULL_REPLY}.
     * <p>
     * If true, this means that an exception occurred when querying the API, most likely an {@link IOException}.
     * This will also return true if the API returned an empty response, which is considered an error.
     *
     * @return true if this is a null or empty error response.
     */
    public boolean isNullError() {
        return this == NULL_REPLY;
    }

    /**
     * Get the {@code action} type that was used in the API request of this reply.
     *
     * @return the {@code action} type that was used in the API request of this reply.
     */
    public String getAction() {
        return this.action;
    }

    /**
     * Get the response type of this reply.
     *
     * @return the response type of this reply.
     */
    public Type getType() {
        return this.type;
    }

    /**
     * Get a copy of the full JSON response returned from the API request of this reply.
     *
     * @return a copy of the full JSON response returned from the API request of this reply.
     */
    public JsonObject getResponse() {
        return this.response.deepCopy();
    }

    /**
     * Returns true when {@link #getType()} is equal to {@link Type#SUCCESS}.
     *
     * @return true when {@link #getType()} is equal to {@link Type#SUCCESS}.
     */
    public boolean isSuccess() {
        return this.type == Type.SUCCESS;
    }

    /**
     * Returns true when {@link #getType()} is equal to {@link Type#ERROR}.
     *
     * @return true when {@link #getType()} is equal to {@link Type#ERROR}.
     */
    public boolean isError() {
        return this.type == Type.ERROR;
    }

    /**
     * Returns true when {@link #getType()} is equal to {@link Type#UNKNOWN}.
     *
     * @return true when {@link #getType()} is equal to {@link Type#UNKNOWN}.
     */
    public boolean isUnknown() {
        return this.type == Type.UNKNOWN;
    }

    private final void requireCorrectType(Type type) {
        if (this.type != type)
            throw new IllegalStateException("Reply is not of state " + type + " but is instead " + this.type);
    }

    public enum Type {
        SUCCESS,
        ERROR,
        UNKNOWN
    }
}
