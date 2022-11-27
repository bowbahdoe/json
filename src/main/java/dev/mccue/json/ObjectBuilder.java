package dev.mccue.json;

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
    public Json.Object.Builder put(java.lang.String key, ToJson value) {
        this.values.put(key, value == null ? Json.ofNull() : value.toJson());
        return this;
    }

    @Override
    public Json.Object.Builder putAll(Map<java.lang.String, ? extends ToJson> values) {
        values.forEach((k, v) -> this.put(k, v == null ? Json.ofNull() : v.toJson()));
        return this;
    }

    @Override
    public Json.Object build() {
        return Json.Object.of(this.values);
    }
}
