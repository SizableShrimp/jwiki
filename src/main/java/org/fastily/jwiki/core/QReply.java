package org.fastily.jwiki.core;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.fastily.jwiki.util.FL;
import org.fastily.jwiki.util.GSONP;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A Response from the server for query modules. Contains pre-defined comprehension methods for convenience.
 *
 * @author Fastily
 */
public class QReply extends AReply {
    /**
     * A public constant representing an empty or invalid response from the API, considered to be in an {@link Type#ERROR} state.
     */
    public static final QReply NULL_REPLY = new QReply(Type.ERROR, new JsonObject());
    /**
     * Default path to json for {@code prop} queries.
     */
    protected static final ArrayList<String> defaultPropPTJ = FL.toSAL("query", "pages");

    /**
     * Tracks {@code normalized} titles. The key is the {@code from} (non-normalized) title and the value is the
     * {@code to} (normalized) title.
     */
    private final Map<String, String> normalized;

    protected QReply(Type type, JsonObject response) {
        super("query", type, response);

        if (GSONP.nestedHas(response, FL.toSAL("query", "normalized"))) {
            normalized = GSONP.pairOff(GSONP.getJAofJO(GSONP.getNestedJA(response, FL.toSAL("query", "normalized"))), "from", "to");
        } else {
            normalized = Map.of();
        }
    }

    /**
     * Wrap the {@code query} action and {@code response} into an {@link AReply} object.
     *
     * @param response The JSON GET response from the server.
     * @return a wrapped {@link AReply} object.
     */
    public static QReply wrap(JsonObject response) {
        if (response == null || response.size() == 0)
            return NULL_REPLY;

        return new QReply(getType("query", response), response);
    }

    /**
     * Performs simple {@code list} query Response comprehension. Collects listed JsonObject items in a list.
     *
     * @param k Points to the JsonArray of JsonObject, under {@code query}, of interest.
     * @return A lightly processed List of {@code list} data.
     */
    public List<JsonObject> listComp(String k) {
        return this.response.has("query") ? GSONP.getJAofJO(this.response.getAsJsonObject("query").deepCopy(), k) : new ArrayList<>();
    }

    /**
     * Performs simple {@code prop} query Response comprehension. Collects two values from each returned {@code prop}
     * query item in a HashMap. Title normalization is automatically applied.
     *
     * @param kk Points to the String to set as the HashMap key in each {@code prop} query item.
     * @param vk Points to the JsonElement to set as the HashMap value in each {@code prop} query item.
     * @return A lightly processed HashMap of {@code prop} data.
     */
    public HashMap<String, JsonElement> propComp(String kk, String vk) {
        HashMap<String, JsonElement> m = new HashMap<>();

        JsonObject x = GSONP.getNestedJO(this.response, defaultPropPTJ);
        if (x == null)
            return m;

        for (JsonObject jo : GSONP.getJOofJO(x)) {
                m.put(GSONP.getStr(jo, kk), jo.has(vk) ? jo.get(vk).deepCopy() : null);
        }

        return normalize(m);
    }

    /**
     * Performs simple {@code meta} query Response comprehension.
     *
     * @param k The key to get a JsonElement for.
     * @return The JsonElement pointed to by {@code k} or null/empty JsonObject on error.
     */
    public JsonElement metaComp(String k) {
        return GSONP.nestedHas(this.response, List.of("query", k)) ? this.response.getAsJsonObject("query").get(k).deepCopy() : new JsonObject();
    }

    /**
     * Performs title normalization when it is automatically done by MediaWiki. MediaWiki will return a
     * {@code normalized} JsonArray when it fixes lightly malformed titles. This is intended for use with {@code prop}
     * style queries.
     *
     * @param <V> Any Object.
     * @param m The Map of elements to normalize.
     * @return {@code m}, for chaining convenience.
     */
    public <V> HashMap<String, V> normalize(HashMap<String, V> m) {
        if (normalized != null)
            normalized.forEach((f, t) -> {
                if (m.containsKey(t))
                    m.put(f, m.get(t));
            });

        return m;
    }
}
