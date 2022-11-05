package dev.mccue.json;

import java.util.HashMap;
import java.util.Map;

record ObjectBuilder(HashMap<java.lang.String, Json> values) implements Json.Object.Builder {
    ObjectBuilder() {
        this(new HashMap<>());
    }

    @Override
    public Json.Object.Builder put(java.lang.String key, Json value) {
        this.values.put(key, value);
        return this;
    }

    @Override
    public Json.Object.Builder putAll(Map<java.lang.String, ? extends Json> values) {
        values.forEach(this::put);
        return this;
    }

    @Override
    public Json.Object build() {
        return Json.Object.of(this.values);
    }
}
