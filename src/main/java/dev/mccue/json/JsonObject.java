package dev.mccue.json;

import dev.mccue.json.internal.ArrayBuilderImpl;
import dev.mccue.json.internal.ObjectBuilder;
import dev.mccue.json.internal.ObjectImpl;
import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Represents an object in the json data model.
 *
 * @author <a href="ethan@mccue.dev">Ethan McCue</a>
 */
public sealed interface JsonObject extends Json, Map<String, Json> permits ObjectImpl {
    static JsonObject of(Map<String, ? extends @Nullable JsonEncodable> value) {
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

    /**
     * Returns a {@link Collector} which makes a {@link JsonObject} as a terminal {@link java.util.stream.Stream}
     * operation.
     *
     * @param keyMapper Function to extract a {@link String} from the stream.
     * @param valueMapper Function to extract something {@link JsonEncodable} from the stream.
     * @return A {@link Collector}.
     * @param <T> The type of element stored in the stream.
     */
    static <T extends @Nullable Object> Collector<T, ?, JsonObject> collector(
            Function<? super T, String> keyMapper,
            Function<? super T, ? extends JsonEncodable> valueMapper
    ) {
        return Collector.of(
                JsonObject::builder,
                (JsonObject.Builder builder, T o) -> builder.put(
                        keyMapper.apply(o),
                        valueMapper.apply(o).toJson()
                ),
                (a, b) -> {
                    var impl = (ObjectBuilder) b;
                    a.putAll(impl.values());
                    return a;
                },
                JsonObject.Builder::build
        );
    }

    static JsonObject empty() {
        return ObjectImpl.EMPTY;
    }

    sealed interface Builder extends JsonEncodable permits ObjectBuilder {
        Builder put(String key, Json value);

        default Builder put(String key, @Nullable String value) {
            return put(key, Json.of(value));
        }

        default Builder put(String key, @Nullable BigDecimal value) {
            return put(key, Json.of(value));
        }

        default Builder put(String key, double value) {
            return put(key, Json.of(value));
        }

        default Builder put(String key, long value) {
            return put(key, Json.of(value));
        }

        default Builder put(String key, float value) {
            return put(key, Json.of(value));
        }

        default Builder put(String key, int value) {
            return put(key, Json.of(value));
        }

        default Builder put(String key, @Nullable Double value) {
            return put(key, Json.of(value));
        }

        default Builder put(String key, @Nullable Long value) {
            return put(key, Json.of(value));
        }

        default Builder put(String key, @Nullable Float value) {
            return put(key, Json.of(value));
        }

        default Builder put(String key, @Nullable Integer value) {
            return put(key, Json.of(value));
        }

        default Builder put(String key, @Nullable BigInteger value) {
            return put(key, Json.of(value));
        }

        default Builder putNull(String key) {
            return put(key, Json.ofNull());
        }

        default Builder putTrue(String key) {
            return put(key, Json.ofTrue());
        }

        default Builder putFalse(String key) {
            return put(key, Json.ofFalse());
        }

        default Builder put(String key, boolean value) {
            return put(key, Json.of(value));
        }

        default Builder put(String key, @Nullable Boolean value) {
            return put(key, Json.of(value));
        }

        default Builder put(String key, Collection<? extends @Nullable JsonEncodable> value) {
            return put(key, Json.of(value));
        }

        default Builder put(String key, Map<String, ? extends @Nullable JsonEncodable> value) {
            return put(key, Json.of(value));
        }

        default Builder put(String key, @Nullable JsonEncodable value) {
            return put(key, Json.of(value));
        }

        default Builder put(String key, @Nullable JsonObject value) {
            return put(key, (JsonEncodable) value);
        }

        default Builder put(String key, @Nullable JsonArray value) {
            return put(key, (JsonEncodable) value);
        }

        Builder putAll(Map<java.lang.String, ? extends @Nullable JsonEncodable> values);

        JsonObject build();

        @Override
        default Json toJson() {
            return build();
        }
    }
}
