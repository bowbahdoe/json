package dev.mccue.json.internal;

import dev.mccue.json.*;

import java.util.*;

@ValueCandidate
public record ArrayBuilderImpl(ArrayList<Json> values) implements JsonArray.Builder {
    public ArrayBuilderImpl() {
        this(new ArrayList<>());
    }

    public ArrayBuilderImpl(int initialCapacity) {
        this(new ArrayList<>(initialCapacity));
    }

    @Override
    public JsonArray.Builder add(Json value) {
        this.values.add(value == null ? Json.ofNull() : value);
        return this;
    }

    @Override
    public JsonArray.Builder addAll(Collection<? extends JsonEncodable> value) {
        Objects.requireNonNull(value);
        value.forEach(v -> this.values.add(v == null ? Json.ofNull() : v.toJson()));
        return this;
    }

    @Override
    public JsonArray build() {
        return new ArrayImpl(List.copyOf(this.values));
    }

    @InternalInvariant({
            "no methods called on builder after this one",
    })
    JsonArray buildInternal() {
        return new ArrayImpl(Collections.unmodifiableList(this.values));
    }
}
