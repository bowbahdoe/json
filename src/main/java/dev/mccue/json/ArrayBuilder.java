package dev.mccue.json;

import dev.mccue.json.internal.InternalInvariant;

import java.util.*;

record ArrayBuilder(ArrayList<Json> values) implements Json.Array.Builder {
    ArrayBuilder() {
        this(new ArrayList<>());
    }

    ArrayBuilder(int initialCapacity) {
        this(new ArrayList<>(initialCapacity));
    }

    @Override
    public Json.Array.Builder add(Json value) {
        this.values.add(value == null ? Json.ofNull() : value);
        return this;
    }

    @Override
    public Json.Array.Builder addAll(Collection<? extends ToJson> value) {
        Objects.requireNonNull(value);
        value.forEach(v -> this.values.add(v == null ? Json.ofNull() : v.toJson()));
        return this;
    }

    @Override
    public Json.Array build() {
        return new Array(List.copyOf(this.values));
    }

    @InternalInvariant({
            "no methods called on builder after this one",
    })
    Json.Array buildInternal() {
        return new Array(Collections.unmodifiableList(this.values));
    }
}
