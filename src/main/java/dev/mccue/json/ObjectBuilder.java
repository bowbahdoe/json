package dev.mccue.json;

import java.util.HashMap;
import java.util.Map;

record ObjectBuilder(HashMap<Json.String, Json> values) implements Json.Object.Builder {
    ObjectBuilder() {
        this(new HashMap<>());
    }

    @Override
    public Json.Object.Builder put(Json.String key, Json value) {
        this.values.put(key, value);
        return this;
    }

    @Override
    public Json.Object.Builder putAll(Map<Json.String, ? extends Json> values) {
        this.values.putAll(values);
        return this;
    }

    @Override
    public Json.Object build() {
        return Json.Object.of(this.values);
    }
}
