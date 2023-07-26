package dev.mccue.json;

import org.jspecify.annotations.Nullable;

import java.util.function.Function;

/**
 * Object that knows how to encode an object into Json.
 *
 * <p>
 *     This is a counterpart to {@link JsonEncodable} which can be provided
 *     to encode objects in contexts where elements are not, for whatever reason,
 *     have an intrinsic Json representation.
 * </p>
 * @param <T> The type of element to encode.
 * @author <a href="ethan@mccue.dev">Ethan McCue</a>
 */
public interface JsonEncoder<T extends @Nullable Object> {
    /**
     * Encodes the given value to Json.
     * @param value The value to encode.
     * @return The encoded value.
     */
    Json encode(T value);


    /**
     * Creates a {@link JsonEncoder} which delegates to an intrinsic implementation
     * provided by virtue of being {@link JsonEncodable}.
     * @return A {@link JsonEncoder}.
     * @param <T> The type to encode.
     */
    static <T extends JsonEncodable> JsonEncoder<T> of() {
        return JsonEncodable::toJson;
    }

    /**
     * Convenience method to target a lambda expression to a {@link JsonEncoder}
     * and be able to call methods such as {@link JsonEncoder#map(Function)}.
     * @param encoder The expression to wrap as an encoder.
     * @return A {@link JsonEncoder}.
     * @param <T> The type to encode.
     */
    static <T extends @Nullable Object> JsonEncoder<T> of(JsonEncoder<? super T> encoder) {
        return encoder::encode;
    }

    /**
     * Maps the result of this encoder.
     * @param f The function to apply.
     * @return An encoder that encodes a different type.
     * @param <R> The type the returned encoder will encode.
     */
    default <R extends @Nullable Object> JsonEncoder<R> map(Function<? super R, ? extends T> f) {
        return r -> this.encode(f.apply(r));
    }
}
