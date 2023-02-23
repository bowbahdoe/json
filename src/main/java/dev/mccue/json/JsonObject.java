package dev.mccue.json;

import dev.mccue.json.internal.ObjectBuilder;
import dev.mccue.json.internal.ObjectImpl;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Represents an object in the json data model.
 */
public sealed interface JsonObject extends Json, Map<String, Json> permits ObjectImpl {
    static JsonObject of(Map<String, ? extends JsonEncodable> value) {
        return new ObjectImpl(value
                .entrySet()
                .stream()
                .collect(Collectors.toUnmodifiableMap(
                        entry -> {
                            Objects.requireNonNull(entry.getKey(), "Json.Object cannot have null keys");
                            return entry.getKey();
                        },
                        entry -> Json.of(entry.getValue())
                )));
    }

    static Builder builder() {
        return new ObjectBuilder();
    }

    static Builder builder(int initialCapacity) {
        return new ObjectBuilder(initialCapacity);
    }

    sealed interface Builder extends JsonEncodable permits ObjectBuilder {
        Builder put(java.lang.String key, Json value);

        default Builder put(java.lang.String key, java.lang.String value) {
            return put(key, Json.of(value));
        }

        default Builder put(java.lang.String key, BigDecimal value) {
            return put(key, Json.of(value));
        }

        default Builder put(java.lang.String key, double value) {
            return put(key, Json.of(value));
        }

        default Builder put(java.lang.String key, long value) {
            return put(key, Json.of(value));
        }

        default Builder put(java.lang.String key, float value) {
            return put(key, Json.of(value));
        }

        default Builder put(java.lang.String key, int value) {
            return put(key, Json.of(value));
        }

        default Builder put(java.lang.String key, java.lang.Double value) {
            return put(key, Json.of(value));
        }

        default Builder put(java.lang.String key, java.lang.Long value) {
            return put(key, Json.of(value));
        }

        default Builder put(java.lang.String key, Float value) {
            return put(key, Json.of(value));
        }

        default Builder put(java.lang.String key, Integer value) {
            return put(key, Json.of(value));
        }

        default Builder put(java.lang.String key, java.math.BigInteger value) {
            return put(key, Json.of(value));
        }

        default Builder putNull(java.lang.String key) {
            return put(key, Json.ofNull());
        }

        default Builder putTrue(java.lang.String key) {
            return put(key, Json.ofTrue());
        }

        default Builder putFalse(java.lang.String key) {
            return put(key, Json.ofFalse());
        }

        default Builder put(java.lang.String key, boolean value) {
            return put(key, Json.of(value));
        }

        default Builder put(java.lang.String key, java.lang.Boolean value) {
            return put(key, Json.of(value));
        }

        default Builder put(java.lang.String key, Collection<? extends JsonEncodable> value) {
            return put(key, Json.of(value));
        }

        default Builder put(java.lang.String key, Map<java.lang.String, ? extends JsonEncodable> value) {
            return put(key, Json.of(value));
        }

        default Builder put(java.lang.String key, JsonEncodable value) {
            return put(key, Json.of(value));
        }

        default Builder put(java.lang.String key, JsonObject value) {
            return put(key, (JsonEncodable) value);
        }

        default Builder put(java.lang.String key, JsonArray value) {
            return put(key, (JsonEncodable) value);
        }

        Builder putAll(Map<java.lang.String, ? extends JsonEncodable> values);

        JsonObject build();

        @Override
        default Json toJson() {
            return build();
        }
    }
}
