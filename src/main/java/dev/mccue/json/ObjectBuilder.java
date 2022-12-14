package dev.mccue.json;

import dev.mccue.json.internal.InternalInvariant;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

record ObjectBuilder(HashMap<java.lang.String, Json> values) implements Json.Object.Builder {
    ObjectBuilder() {
        this(new HashMap<>());
    }

    ObjectBuilder(int initialCapacity) {
        this(new HashMap<>(initialCapacity));
    }

    @Override
    public Json.Object.Builder put(java.lang.String key, Json value) {
        this.values.put(key, value == null ? Json.ofNull() : value);
        return this;
    }

    @Override
    public Json.Object.Builder putAll(Map<java.lang.String, ? extends ToJson> values) {
        values.forEach((k, v) -> this.put(k, v == null ? Json.ofNull() : v.toJson()));
        return this;
    }

    @Override
    public Json.Object build() {
        return new ObjectImpl(Map.copyOf(this.values));
    }

    @InternalInvariant({
            "no methods called on builder after this one",
    })
    Json.Object buildInternal() {
        return new ObjectImpl(Collections.unmodifiableMap(this.values));
    }
}
