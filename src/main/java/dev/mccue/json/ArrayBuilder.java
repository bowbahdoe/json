package dev.mccue.json;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

record ArrayBuilder(ArrayList<Json> values) implements Json.Array.Builder {
    ArrayBuilder() {
        this(new ArrayList<>());
    }

    ArrayBuilder(int initialCapacity) {
        this(new ArrayList<>(initialCapacity));
    }

    @Override
    public Json.Array.Builder add(Json value) {
        Objects.requireNonNull(value);
        this.values.add(value);
        return this;
    }

    @Override
    public Json.Array.Builder addAll(Collection<? extends Json> value) {
        Objects.requireNonNull(value);
        value.forEach(Objects::requireNonNull);
        this.values.addAll(value);
        return this;
    }

    @Override
    public Json.Array build() {
        return Json.Array.of(this.values);
    }
}
