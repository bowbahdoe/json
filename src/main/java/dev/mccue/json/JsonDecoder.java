package dev.mccue.json;

import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Function;

/**
 * An object that knows how to convert another kind of object can be converted to JSON.
 *
 * <p>
 *     It is preferred that when a decoder fails it throw a {@link JsonDecodeException}.
 * </p>
 *
 * @param <T> The type that being constructed from the Json.
 *
 * @author <a href="ethan@mccue.dev">Ethan McCue</a>
 */
@FunctionalInterface
public interface JsonDecoder<T extends @Nullable Object> {
    /**
     * Interpret some {@link Json} as some type.
     * @param json The json being decoded.
     * @return An instance of the type decoded from the Json.
     * @throws JsonDecodeException If the structure of Json does match the needed structure to make a T.
     *                              Other kinds of exceptions may be thrown, but good citizens should try to
     *                              use {@link JsonDecodeException}.
     */
    T decode(Json json) throws JsonDecodeException;

    /**
     * Given a decoder which constructs some type of output,
     * transforms the result of that decoder.
     *
     * @param f The function to use to transform the output of the decoder.
     * @return A new decoder with its output being the result of applying the given function.
     * @param <R> The new result type to expect from a run of the decoder.
     */
    default <R extends @Nullable Object> JsonDecoder<R> map(Function<? super T, ? extends R> f) {
        return value -> f.apply(this.decode(value));
    }

    static <T extends @Nullable Object> JsonDecoder<T> of(JsonDecoder<? extends T> decoder) {
        return decoder::decode;
    }

    static java.lang.String string(Json json) throws JsonDecodeException {
        if (!(json instanceof JsonString jsonString)) {
            throw JsonDecodeException.of(
                    "expected a string",
                    json
            );
        } else {
            return jsonString.toString();
        }
    }

    static boolean boolean_(Json json) throws JsonDecodeException {
        if (!(json instanceof JsonBoolean jsonBoolean)) {
            throw JsonDecodeException.of(
                    "expected a boolean",
                    json
            );
        } else {
            return jsonBoolean.value();
        }
    }

    static int int_(Json json) throws JsonDecodeException {
        if (!(json instanceof JsonNumber jsonNumber)) {
            throw JsonDecodeException.of(
                    "expected a number",
                    json
            );
        } else if (!jsonNumber.isIntegral()) {
            throw JsonDecodeException.of(
                    "expected a number with no decimal part",
                    json
            );
        } else {
            try {
                return jsonNumber.intValueExact();
            } catch (ArithmeticException e) {
                throw JsonDecodeException.of(
                        "expected a number which could be converted to an int",
                        json
                );
            }
        }
    }

    static long long_(Json json) throws JsonDecodeException {
        if (!(json instanceof JsonNumber jsonNumber)) {
            throw JsonDecodeException.of(
                    "expected a number",
                    json
            );
        } else if (!jsonNumber.isIntegral()) {
            throw JsonDecodeException.of(
                    "expected a number with no decimal part",
                    json
            );
        } else {
            try {
                return jsonNumber.longValueExact();
            } catch (ArithmeticException e) {
                throw JsonDecodeException.of(
                        "expected a number which could be converted to a long",
                        json
                );
            }
        }
    }

    static float float_(Json json) throws JsonDecodeException {
        if (!(json instanceof JsonNumber jsonNumber)) {
            throw JsonDecodeException.of(
                    "expected a number",
                    json
            );
        } else {
            return jsonNumber.floatValue();
        }
    }

    static double double_(Json json) throws JsonDecodeException {
        if (!(json instanceof JsonNumber jsonNumber)) {
            throw JsonDecodeException.of(
                    "expected a number",
                    json
            );
        } else {
            return jsonNumber.doubleValue();
        }
    }

    static BigInteger bigInteger(Json json) throws JsonDecodeException {
        if (!(json instanceof JsonNumber jsonNumber)) {
            throw JsonDecodeException.of(
                    "expected a number",
                    json
            );
        } else {
            try {
                return jsonNumber.bigIntegerValueExact();
            } catch (ArithmeticException e) {
                throw JsonDecodeException.of(
                        "expected a number which could be converted to a bigInteger",
                        json
                );
            }
        }
    }

    static BigDecimal bigDecimal(Json json) throws JsonDecodeException {
        if (!(json instanceof JsonNumber jsonNumber)) {
            throw JsonDecodeException.of(
                    "expected a number",
                    json
            );
        } else {
            return jsonNumber.bigDecimalValue();
        }
    }

    static <@Nullable T> T null_(Json json) throws JsonDecodeException {
        if (!(json instanceof JsonNull)) {
            throw JsonDecodeException.of(
                    "expected null",
                    json
            );
        } else {
            return null;
        }
    }

    static <T extends @Nullable Object> T null_(Json json, T value) throws JsonDecodeException {
        if (!(json instanceof JsonNull)) {
            throw JsonDecodeException.of(
                    "expected null",
                    json
            );
        } else {
            return value;
        }
    }

    static <T extends @Nullable Object> JsonDecoder<List<T>> array(
            JsonDecoder<? extends T> itemDecoder
    ) throws JsonDecodeException {
        return json -> array(json, itemDecoder);
    }

    static JsonArray array(Json json) throws JsonDecodeException {
        if (!(json instanceof JsonArray jsonArray)) {
            throw JsonDecodeException.of(
                    "expected an array",
                    json
            );
        } else {
            return jsonArray;
        }
    }

    static <T extends @Nullable Object> List<T> array(Json json, JsonDecoder<? extends T> itemDecoder) throws JsonDecodeException {
        if (!(json instanceof JsonArray jsonArray)) {
            throw JsonDecodeException.of(
                    "expected an array",
                    json
            );
        } else {
            var items = new ArrayList<T>(jsonArray.size());
            for (int i = 0; i < jsonArray.size(); i++) {
                var jsonItem = jsonArray.get(i);
                try {
                    items.add(itemDecoder.decode(jsonItem));
                } catch (JsonDecodeException e) {
                    throw JsonDecodeException.atIndex(i, e);
                } catch (Exception e) {
                    throw JsonDecodeException.atIndex(i, JsonDecodeException.of(e, jsonItem));
                }
            }
            return Collections.unmodifiableList(items);
        }
    }

    static JsonObject object(Json json) throws JsonDecodeException {
        if (!(json instanceof JsonObject jsonObject)) {
            throw JsonDecodeException.of(
                    "expected an object",
                    json
            );
        } else {
            return jsonObject;
        }
    }

    static <T extends @Nullable Object> JsonDecoder<Map<String, T>> object(JsonDecoder<? extends T> valueDecoder) throws JsonDecodeException {
        return json -> object(json, valueDecoder);
    }

    static <T extends @Nullable Object> Map<java.lang.String, T> object(
            Json json,
            JsonDecoder<? extends T> valueDecoder
    ) throws JsonDecodeException {
        var jsonObject = object(json);
        var m = new LinkedHashMap<String, T>(jsonObject.size());
        jsonObject.forEach((key, value) -> {
            try {
                m.put(key, valueDecoder.decode(value));
            } catch (JsonDecodeException e) {
                throw JsonDecodeException.atField(key, e);
            } catch (Exception e) {
                throw JsonDecodeException.atField(key, JsonDecodeException.of(e, value));
            }
        });
        return Collections.unmodifiableMap(m);
    }

    static <T extends @Nullable Object> JsonDecoder<T> field(
            java.lang.String fieldName,
            JsonDecoder<? extends T> valueDecoder
    ) throws JsonDecodeException {
        return json -> field(json, fieldName, valueDecoder);
    }

    static <T extends @Nullable Object> T field(
            Json json,
            String fieldName,
            JsonDecoder<? extends T> valueDecoder
    ) throws JsonDecodeException {
        var jsonObject = object(json);
        var value = jsonObject.get(fieldName);
        if (value == null) {
            throw JsonDecodeException.atField(
                    fieldName,
                    JsonDecodeException.of(
                            "no value for field",
                            json
                    )
            );
        }
        else {
            try {
                return valueDecoder.decode(value);
            } catch (JsonDecodeException e) {
                throw JsonDecodeException.atField(
                        fieldName,
                        e
                );
            }  catch (Exception e) {
                throw JsonDecodeException.atField(fieldName, JsonDecodeException.of(e, value));
            }
        }
    }

    static <T extends @Nullable Object> JsonDecoder<T> nullableField(java.lang.String fieldName, JsonDecoder<? extends T> valueDecoder, T defaultValue) throws JsonDecodeException {
        return json -> nullableField(json, fieldName, valueDecoder, defaultValue);
    }

    static <T extends @Nullable Object> T nullableField(Json json, java.lang.String fieldName, JsonDecoder<? extends T> valueDecoder, T defaultValue) throws JsonDecodeException {
        var decoder = nullable(valueDecoder)
                .map(o -> o.map(i -> (T) i))
                .map(o -> o.orElse(defaultValue));
        return field(json, fieldName, decoder);
    }

    static <T extends @Nullable Object> JsonDecoder<Optional<T>> nullableField(java.lang.String fieldName, JsonDecoder<? extends T> valueDecoder) throws JsonDecodeException {
        return json -> nullableField(json, fieldName, valueDecoder);
    }

    static <T extends @Nullable Object> Optional<T> nullableField(Json json, java.lang.String fieldName, JsonDecoder<? extends T> valueDecoder) throws JsonDecodeException {
        return nullableField(json, fieldName, valueDecoder.map(Optional::of), Optional.empty());
    }

    static <T extends @Nullable Object> JsonDecoder<T> optionalField(java.lang.String fieldName, JsonDecoder<? extends T> valueDecoder, T defaultValue) throws JsonDecodeException {
        return json -> optionalField(json, fieldName, valueDecoder, defaultValue);
    }

    static <T extends @Nullable Object> T optionalField(Json json, java.lang.String fieldName, JsonDecoder<? extends T> valueDecoder, T defaultValue) throws JsonDecodeException {
        var jsonObject = object(json);
        var value = jsonObject.get(fieldName);
        if (value == null) {
            return defaultValue;
        }
        else {
            try {
                return valueDecoder.decode(value);
            } catch (JsonDecodeException e) {
                throw JsonDecodeException.atField(
                        fieldName,
                        e
                );
            } catch (Exception e) {
                throw JsonDecodeException.atField(fieldName, JsonDecodeException.of(e, value));
            }
        }
    }

    static <T extends @Nullable Object> JsonDecoder<Optional<T>> optionalField(java.lang.String fieldName, JsonDecoder<? extends T> valueDecoder) throws JsonDecodeException {
        return json -> optionalField(json, fieldName, valueDecoder);
    }

    static <T extends @Nullable Object> Optional<T> optionalField(Json json, java.lang.String fieldName, JsonDecoder<? extends T> valueDecoder) throws JsonDecodeException {
        return optionalField(json, fieldName, valueDecoder.map(Optional::of), Optional.empty());
    }

    static <T extends @Nullable Object> JsonDecoder<Optional<T>> optionalNullableField(java.lang.String fieldName, JsonDecoder<? extends T> valueDecoder) throws JsonDecodeException {
        return json -> optionalNullableField(json, fieldName, valueDecoder);
    }

    static <T extends @Nullable Object> Optional<T> optionalNullableField(Json json, java.lang.String fieldName, JsonDecoder<? extends T> valueDecoder) throws JsonDecodeException {
        return optionalField(json, fieldName, nullable(valueDecoder), Optional.empty());
    }

    static <T extends @Nullable Object> JsonDecoder<T> optionalNullableField(java.lang.String fieldName, JsonDecoder<? extends T> valueDecoder, T defaultValue) throws JsonDecodeException {
        return json -> optionalNullableField(json, fieldName, valueDecoder, defaultValue);
    }

    static <T extends @Nullable Object> T optionalNullableField(Json json, java.lang.String fieldName, JsonDecoder<? extends T> valueDecoder, T defaultValue) throws JsonDecodeException {
        var decoder = nullable(valueDecoder)
                .map(opt -> opt.orElse(null))
                .map(value -> value == null ? defaultValue : value);

        return optionalField(
                json,
                fieldName,
                decoder,
                defaultValue
        );
    }

    static <T extends @Nullable Object> JsonDecoder<T> optionalNullableField(
            java.lang.String fieldName,
            JsonDecoder<? extends T> valueDecoder,
            T whenFieldMissing,
            T whenFieldNull
    ) throws JsonDecodeException {
        return json -> optionalNullableField(
                json,
                fieldName,
                valueDecoder,
                whenFieldMissing,
                whenFieldNull
        );
    }

    static <T extends @Nullable Object> T optionalNullableField(
            Json json,
            String fieldName,
            JsonDecoder<? extends T> valueDecoder,
            T whenFieldMissing,
            T whenFieldNull
    ) throws JsonDecodeException {
        var decoder = nullable(valueDecoder)
                .map(opt -> opt.orElse(null))
                .map(value -> value == null ? whenFieldNull : value);

        return optionalField(
                json,
                fieldName,
                decoder,
                whenFieldMissing
        );
    }

    static <T extends @Nullable Object> JsonDecoder<T> index(int index, JsonDecoder<? extends T> valueDecoder) throws JsonDecodeException {
        return json -> index(json, index, valueDecoder);
    }

    static <T extends @Nullable Object> T index(Json json, int index, JsonDecoder<? extends T> valueDecoder) throws JsonDecodeException {
        var jsonArray = array(json);
        if (index >= jsonArray.size()) {
            throw JsonDecodeException.atIndex(
                    index,
                    JsonDecodeException.of(
                            "expected array index to be in bounds",
                            json
                    )
            );
        }
        else {
            try {
                return valueDecoder.decode(jsonArray.get(index));
            } catch (JsonDecodeException e) {
                throw JsonDecodeException.atIndex(
                        index,
                        e
                );
            } catch (Exception e) {
                throw JsonDecodeException.atIndex(index, JsonDecodeException.of(e, jsonArray.get(index)));
            }
        }
    }

    static <T> JsonDecoder<Optional<T>> nullable(JsonDecoder<? extends T> decoder) {
        return json -> JsonDecoder.oneOf(
                json,
                decoder.map(Optional::of),
                JsonDecoder.of(JsonDecoder::null_).map(__ -> Optional.empty())
        );
    }

    static <T extends @Nullable Object> JsonDecoder<T> nullable(JsonDecoder<? extends T> decoder, T defaultValue) {
        return json -> JsonDecoder.oneOf(
                json,
                decoder,
                JsonDecoder.of(JsonDecoder::null_).map(__ -> defaultValue)
        );
    }

    static <T> JsonDecoder<T> oneOf(JsonDecoder<? extends T> decoderA, JsonDecoder<? extends T> decoderB) throws JsonDecodeException {
        return json -> oneOf(json, decoderA, decoderB);
    }

    static <T> T oneOf(Json json, JsonDecoder<? extends T> decoderA, JsonDecoder<? extends T> decoderB) throws JsonDecodeException {
        try {
            return decoderA.decode(json);
        } catch (JsonDecodeException e1) {
            try {
                return decoderB.decode(json);
            }
            catch (JsonDecodeException e2) {
                var errors = new ArrayList<JsonDecodeException>();
                if (e1 instanceof JsonDecodeException.OneOf oneOf) {
                    errors.addAll(oneOf.errors());
                }
                else {
                    errors.add(e1);
                }

                if (e2 instanceof JsonDecodeException.OneOf oneOf) {
                    errors.addAll(oneOf.errors());
                }
                else {
                    errors.add(e2);
                }

                throw JsonDecodeException.oneOf(Collections.unmodifiableList(errors));
            }
            catch (Exception e2) {
                var errors = new ArrayList<JsonDecodeException>();
                if (e1 instanceof JsonDecodeException.OneOf oneOf) {
                    errors.addAll(oneOf.errors());
                }
                else {
                    errors.add(e1);
                }

                errors.add(JsonDecodeException.of(e2, json));

                throw JsonDecodeException.oneOf(Collections.unmodifiableList(errors));
            }
        }
    }


    @SafeVarargs
    @SuppressWarnings("varargs")
    static <T> JsonDecoder<T> oneOf(JsonDecoder<? extends T> decoderA, JsonDecoder<? extends T>... decoders) throws JsonDecodeException {
        return json -> oneOf(json, decoderA, decoders);
    }

    @SafeVarargs
    static <T> T oneOf(Json json, JsonDecoder<? extends T> decoderA, JsonDecoder<? extends T>... decoders) throws JsonDecodeException {
        try {
            return decoderA.decode(json);
        } catch (JsonDecodeException e1) {
            var errors = new ArrayList<JsonDecodeException>();
            if (e1 instanceof JsonDecodeException.OneOf oneOf) {
                errors.addAll(oneOf.errors());
            }
            else {
                errors.add(e1);
            }

            for (var decoder : decoders) {
                try {
                    return decoder.decode(json);
                } catch (JsonDecodeException e2) {
                    if (e2 instanceof JsonDecodeException.OneOf oneOf) {
                        errors.addAll(oneOf.errors());
                    } else {
                        errors.add(e2);
                    }
                } catch (Exception e2) {
                    errors.add(JsonDecodeException.of(e2, json));
                }
            }

            throw JsonDecodeException.oneOf(Collections.unmodifiableList(errors));
        }
    }
}
