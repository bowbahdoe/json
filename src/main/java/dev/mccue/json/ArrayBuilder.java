package dev.mccue.json;

import java.util.ArrayList;
import java.util.Collection;

record ArrayBuilder(ArrayList<Json> values) implements Json.Array.Builder {
    ArrayBuilder() {
        this(new ArrayList<>());
    }

    @Override
    public Json.Array.Builder add(Json value) {
        this.values.add(value);
        return this;
    }

    @Override
    public Json.Array.Builder addAll(Collection<? extends Json> value) {
        this.values.addAll(value);
        return this;
    }

    @Override
    public Json.Array build() {
        return Json.Array.of(this.values);
    }
}
