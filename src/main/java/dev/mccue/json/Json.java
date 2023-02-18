package dev.mccue.json;

import dev.mccue.json.internal.*;
import dev.mccue.json.stream.JsonStreamReadOptions;
import dev.mccue.json.stream.JsonValueHandler;
import dev.mccue.json.stream.JsonWriteable;

import java.io.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Immutable tree representation of Json.
 */
public sealed interface Json
        extends Serializable, JsonEncodable, JsonWriteable
        permits JsonBoolean, JsonNull, JsonString, JsonNumber, JsonArray, JsonObject {
    static Json of(JsonEncodable value) {
        if (value == null) {
            return JsonNull.instance();
        }
        else {
            var asJson = value.toJson();
            if (asJson == null) {
                return JsonNull.instance();
            }
            else {
                return asJson;
            }
        }
    }

    static Json of(BigDecimal value) {
        return value == null ? JsonNull.instance() : new BigDecimalImpl(value);
    }

    static Json of(double value) {
        return new DoubleImpl(value);
    }

    static Json of(long value) {
        return new LongImpl(value);
    }

    static Json of(float value) {
        return new DoubleImpl(value);
    }

    static Json of(int value) {
        return new LongImpl(value);
    }

    static Json of(java.lang.Double value) {
        return value == null ? JsonNull.instance() : new DoubleImpl(value);
    }

    static Json of(java.lang.Long value) {
        return value == null ? JsonNull.instance() : new LongImpl(value);
    }

    static Json of(Float value) {
        return value == null ? JsonNull.instance() : new DoubleImpl(value);
    }

    static Json of(Integer value) {
        return value == null ? JsonNull.instance() : new LongImpl(value);
    }

    static Json of(java.math.BigInteger value) {
        return value == null ? JsonNull.instance() : new BigIntegerImpl(value);
    }

    static Json of(java.lang.String value) {
        return value == null ? JsonNull.instance() : new StringImpl(value);
    }

    static Json ofNull() {
        return JsonNull.instance();
    }

    static Json ofTrue() {
        return JsonBoolean.of(true);
    }

    static Json ofFalse() {
        return JsonBoolean.of(false);
    }


    static Json of(boolean b) {
        return JsonBoolean.of(b);
    }

    static Json of(java.lang.Boolean b) {
        return b == null ? JsonNull.instance() : JsonBoolean.of(b);
    }

    static Json of(Collection<? extends JsonEncodable> jsonList) {
        return jsonList == null
                ? JsonNull.instance()
                : new ArrayImpl(
                        jsonList.stream()
                                .map(json -> json == null ? JsonNull.instance() : json.toJson())
                                .toList()
                );
    }

    static Json of(Map<java.lang.String, ? extends JsonEncodable> jsonMap) {
        return jsonMap == null
                ? JsonNull.instance()
                : new ObjectImpl(
                        jsonMap
                                .entrySet()
                                .stream()
                                .collect(Collectors.toUnmodifiableMap(
                                        Map.Entry::getKey,
                                        entry -> entry.getValue() == null
                                                ? JsonNull.instance()
                                                : entry.getValue().toJson()
                                ))
                );
    }

    static JsonObject.Builder objectBuilder() {
        return JsonObject.builder();
    }

    static JsonObject.Builder objectBuilder(Map<java.lang.String, ? extends JsonEncodable> object) {
        if (object instanceof JsonObject o) {
            return new ObjectBuilder(new LinkedHashMap<>(o));
        }
        else {
            var objectEntries = new LinkedHashMap<String, Json>();
            for (var entry : object.entrySet()) {
                objectEntries.put(entry.getKey(), Json.of(entry.getValue()));
            }
            return new ObjectBuilder(objectEntries);
        }
    }

    static JsonArray.Builder arrayBuilder() {
        return JsonArray.builder();
    }

    static JsonArray.Builder arrayBuilder(Collection<? extends JsonEncodable> object) {
        if (object instanceof JsonArray o) {
            return new ArrayBuilderImpl(new ArrayList<>(o));
        }
        else {
            return new ArrayBuilderImpl(new ArrayList<>(object.stream()
                    .map(Json::of)
                    .toList()));
        }
    }

    static JsonArray emptyArray() {
        return ArrayImpl.EMPTY;
    }

    static JsonObject emptyObject() {
        return ObjectImpl.EMPTY;
    }

    @Override
    default Json toJson() {
        return this;
    }

    static Json readString(CharSequence jsonText) throws JsonReadException {
        return readString(jsonText, new JsonReadOptions());
    }

    static Json readString(CharSequence jsonText, JsonReadOptions options) throws JsonReadException {
        try {
            return JsonReaderMethods.read(new PushbackReader(
                    new StringReader(jsonText.toString()), JsonReaderMethods.MINIMUM_PUSHBACK_BUFFER_SIZE
            ), options);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    static Json read(Reader reader, JsonReadOptions options) throws IOException, JsonReadException {
        return JsonReaderMethods.read(
                new PushbackReader(reader, JsonReaderMethods.MINIMUM_PUSHBACK_BUFFER_SIZE),
                options
        );
    }

    static Json read(Reader reader) throws IOException, JsonReadException {
        return read(reader, new JsonReadOptions());
    }

    static JsonReader reader(Reader reader, JsonReadOptions options) {
        var pushbackReader = new PushbackReader(
                reader,
                JsonReaderMethods.MINIMUM_PUSHBACK_BUFFER_SIZE
        );
        return () -> {
            try {
                return JsonReaderMethods.read(pushbackReader, options);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        };
    }

    static JsonReader reader(Reader reader) {
        return reader(reader, new JsonReadOptions()
                .withEOFBehavior(JsonReadOptions.EOFBehavior.RETURN_NULL));
    }

    static void readStream(Reader reader, JsonValueHandler handler, JsonStreamReadOptions options) throws IOException, JsonReadException {
        JsonReaderMethods.readStream(
                new PushbackReader(reader, JsonReaderMethods.MINIMUM_PUSHBACK_BUFFER_SIZE),
                false,
                options,
                handler
        );
    }

    static void readStream(Reader reader, JsonValueHandler handler) throws IOException, JsonReadException {
        JsonReaderMethods.readStream(
                new PushbackReader(reader, JsonReaderMethods.MINIMUM_PUSHBACK_BUFFER_SIZE),
                false,
                new JsonStreamReadOptions(),
                handler
        );
    }

    static java.lang.String writeString(Json json) {
        return writeString(json, new JsonWriteOptions());
    }

    static java.lang.String writeString(Json json, JsonWriteOptions options) {
        var sw = new StringWriter();
        try {
            write(json, sw, options);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return sw.toString();
    }

    static void write(Json json, Writer writer, JsonWriteOptions options) throws IOException {
        new JsonWriter().write(json, writer, options);
    }

    static void write(Json json, Writer writer) throws IOException {
        new JsonWriter().write(json, writer, new JsonWriteOptions());
    }
}
