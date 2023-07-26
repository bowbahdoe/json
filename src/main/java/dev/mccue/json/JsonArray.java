package dev.mccue.json;


import dev.mccue.json.internal.ArrayBuilderImpl;
import dev.mccue.json.internal.ArrayImpl;
import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Represents an array in the json data model.
 *
 * @author <a href="ethan@mccue.dev">Ethan McCue</a>
 */
public sealed interface JsonArray extends Json, List<Json> permits ArrayImpl {
    static JsonArray of(Json... values) {
        return of(Arrays.asList(values));
    }

    static JsonArray of(List<Json> value) {
        return new ArrayImpl(List.copyOf(value));
    }

    static Builder builder() {
        return new ArrayBuilderImpl();
    }

    static Builder builder(int initialCapacity) {
        return new ArrayBuilderImpl(initialCapacity);
    }

    /**
     * Returns a {@link Collector} which makes a {@link JsonArray} as a terminal {@link java.util.stream.Stream}
     * operation.
     *
     * @return A {@link Collector}
     */
    static Collector<JsonEncodable, ?, JsonArray> collector() {
        return Collector.of(
                JsonArray::builder,
                (JsonArray.Builder builder, JsonEncodable o) -> builder.add(o.toJson()),
                (a, b) -> {
                    var impl = (ArrayBuilderImpl) b;
                    a.addAll(impl.values());
                    return a;
                },
                JsonArray.Builder::build
        );
    }

    static JsonArray empty() {
        return ArrayImpl.EMPTY;
    }

    sealed interface Builder extends JsonEncodable permits ArrayBuilderImpl {
        Builder add(@Nullable Json value);
        Builder addAll(Collection<? extends @Nullable JsonEncodable> value);
        default Builder add(@Nullable JsonEncodable value) {
            return add(Json.of(value));
        }

        default Builder add(@Nullable BigDecimal value) {
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

        default Builder add(@Nullable Double value) {
            return add(Json.of(value));
        }

        default Builder add(@Nullable Long value) {
            return add(Json.of(value));
        }

        default Builder add(@Nullable Float value) {
            return add(Json.of(value));
        }

        default Builder add(@Nullable Integer value) {
            return add(Json.of(value));
        }

        default Builder add(@Nullable BigInteger value) {
            return add(Json.of(value));
        }

        default Builder add(@Nullable String value) {
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

        default Builder add(@Nullable Boolean b) {
            return add(Json.of(b));
        }

        JsonArray build();

        @Override
        default Json toJson() {
            return build();
        }
    }
}
