package dev.mccue.json;

import java.util.HashMap;
import java.util.Map;

record ObjectBuilder(HashMap<Json.String, Json> values) implements Json.Object.Builder {
    ObjectBuilder() {
        this(new HashMap<>());
    }

    @Override
    public Json.Object.Builder put(CharSequence key, Json value) {
        this.values.put(Json.String.of(key), value);
        return this;
    }

    @Override
    public Json.Object.Builder putAll(Map<? extends CharSequence, ? extends Json> values) {
        values.forEach(this::put);
        return this;
    }

    @Override
    public Json.Object build() {
        return Json.Object.of(this.values);
    }
}
