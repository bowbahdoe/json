package dev.mccue.json;


import dev.mccue.json.internal.ArrayBuilderImpl;
import dev.mccue.json.internal.ArrayImpl;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Represents an array in the json data model.
 */
public sealed interface JsonArray extends Json, List<Json> permits ArrayImpl {
    static JsonArray of(Json... values) {
        return of(Arrays.asList(values));
    }

    static JsonArray of(List<Json> value) {
        Objects.requireNonNull(value, "Json.Array value must be nonnull");
        value.forEach(json -> Objects.requireNonNull(json, "Each value in a Json.Array must be nonnull"));
        return new ArrayImpl(List.copyOf(value));
    }

    static Builder builder() {
        return new ArrayBuilderImpl();
    }

    static Builder builder(int initialCapacity) {
        return new ArrayBuilderImpl(initialCapacity);
    }

    sealed interface Builder extends JsonEncodable permits ArrayBuilderImpl {
        Builder add(Json value);
        Builder addAll(Collection<? extends JsonEncodable> value);
        default Builder add(JsonEncodable value) {
            return add(Json.of(value));
        }

        default Builder add(BigDecimal value) {
            return add(Json.of(value));
        }

        default Builder add(double value) {
            return add(Json.of(value));
        }

        default Builder add(long value) {
            return add(Json.of(value));
        }

        default Builder add(float value) {
            return add(Json.of(value));
        }

        default Builder add(int value) {
            return add(Json.of(value));
        }

        default Builder add(java.lang.Double value) {
            return add(Json.of(value));
        }

        default Builder add(java.lang.Long value) {
            return add(Json.of(value));
        }

        default Builder add(Float value) {
            return add(Json.of(value));
        }

        default Builder add(Integer value) {
            return add(Json.of(value));
        }

        default Builder add(java.math.BigInteger value) {
            return add(Json.of(value));
        }

        default Builder add(java.lang.String value) {
            return add(Json.of(value));
        }

        default Builder addNull() {
            return add(Json.ofNull());
        }

        default Builder addTrue() {
            return add(Json.ofTrue());
        }

        default Builder addFalse() {
            return add(Json.ofFalse());
        }

        default Builder add(boolean b) {
            return add(Json.of(b));
        }

        default Builder add(java.lang.Boolean b) {
            return add(Json.of(b));
        }

        JsonArray build();

        @Override
        default Json toJson() {
            return build();
        }
    }
}
