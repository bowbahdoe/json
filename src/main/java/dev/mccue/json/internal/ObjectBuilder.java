package dev.mccue.json.internal;

import dev.mccue.json.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@ValueCandidate
public record ObjectBuilder(HashMap<java.lang.String, Json> values) implements JsonObject.Builder {
    public ObjectBuilder() {
        this(new HashMap<>());
    }

    public ObjectBuilder(int initialCapacity) {
        this(new HashMap<>(initialCapacity));
    }

    @Override
    public JsonObject.Builder put(String key, Json value) {
        this.values.put(key, value == null ? Json.ofNull() : value);
        return this;
    }

    @Override
    public JsonObject.Builder putAll(Map<String, ? extends JsonEncodable> values) {
        values.forEach((k, v) -> this.put(k, v == null ? Json.ofNull() : v.toJson()));
        return this;
    }

    @Override
    public JsonObject build() {
        return new ObjectImpl(Map.copyOf(this.values));
    }

    @InternalInvariant({
            "no methods called on builder after this one",
    })
    JsonObject buildInternal() {
        return new ObjectImpl(Collections.unmodifiableMap(this.values));
    }
}
