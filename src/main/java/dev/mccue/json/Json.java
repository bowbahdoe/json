package dev.mccue.json;

import dev.mccue.json.internal.*;
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
        return value == null ? JsonNull.instance() : value.toJson();
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
            return new ObjectBuilder(new HashMap<>(o));
        }
        else {
            return new ObjectBuilder(new HashMap<>(object.entrySet()
                    .stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> Json.of(entry.getValue())
                    ))));
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
        return readString(jsonText, new ReadOptions());
    }

    static Json readString(CharSequence jsonText, ReadOptions options) throws JsonReadException {
        try {
            return JsonReaderMethods.read(new PushbackReader(
                    new StringReader(jsonText.toString()), JsonReaderMethods.MINIMUM_PUSHBACK_BUFFER_SIZE
            ), options);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    static Json read(Reader reader, ReadOptions options) throws IOException, JsonReadException {
        return JsonReaderMethods.read(
                new PushbackReader(reader, JsonReaderMethods.MINIMUM_PUSHBACK_BUFFER_SIZE),
                options
        );
    }

    static Json read(Reader reader) throws IOException, JsonReadException {
        return read(reader, new ReadOptions());
    }

    static JsonReader reader(Reader reader, ReadOptions options) {
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
        return reader(reader, new ReadOptions()
                .withEOFBehavior(EOFBehavior.RETURN_NULL));
    }

    static void readStream(Reader reader, JsonValueHandler handler, StreamReadOptions options) throws IOException, JsonReadException {
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
                new StreamReadOptions(),
                handler
        );
    }

    static java.lang.String writeString(Json json) {
        return writeString(json, new WriteOptions());
    }

    static java.lang.String writeString(Json json, WriteOptions options) {
        var sw = new StringWriter();
        try {
            write(json, sw, options);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return sw.toString();
    }

    static void write(Json json, Writer writer, WriteOptions options) throws IOException {
        new JsonWriter().write(json, writer, options);
    }

    static void write(Json json, Writer writer) throws IOException {
        new JsonWriter().write(json, writer, new WriteOptions());
    }

    /**
     * @param escapeUnicode If true, non-ascii characters are escaped as \\uXXXX
     * @param escapeJavascriptSeparators If true (default) the Unicode characters U+2028 and U+2029 will
     *                                   be escaped as \\u2028 and \\u2029 even if :escape-unicode is
     *                                   false. (These two characters are valid in pure JSON but are not
     *                                   valid in JavaScript strings.).
     * @param escapeSlash If true (default) the slash / is escaped as \\/
     */
    @ValueCandidate
    record WriteOptions(
            boolean escapeUnicode,
            boolean escapeJavascriptSeparators,
            boolean escapeSlash,
            int indentation
    ) {
        public WriteOptions {
            if (indentation < 0) {
                throw new IllegalArgumentException("indent must not be less than zero.");
            }
        }
        public WriteOptions() {
            this(true, true, true, 0);
        }

        public WriteOptions withEscapeUnicode(boolean escapeUnicode) {
            return new WriteOptions(escapeUnicode, escapeJavascriptSeparators, escapeSlash, indentation);
        }

        public WriteOptions withEscapeJavascriptSeparators(boolean escapeJavascriptSeparators) {
            return new WriteOptions(escapeUnicode, escapeJavascriptSeparators, escapeSlash, indentation);
        }

        public WriteOptions withEscapeSlash(boolean escapeSlash) {
            return new WriteOptions(escapeUnicode, escapeJavascriptSeparators, escapeSlash, indentation);
        }

        public WriteOptions withIndentation(int indentation) {
            return new WriteOptions(escapeUnicode, escapeJavascriptSeparators, escapeSlash, indentation);
        }
    }

    /**
     * Behavior to exhibit when an EOF is reached and no Json is read.
     */
    enum EOFBehavior {
        /**
         * Throw an exception.
         */
        THROW_EXCEPTION,
        /**
         * Return a "true null", not a Json null.
         */
        RETURN_NULL
    }

    /**
     * @param eofBehavior What to do if an attempted read reaches an EOF without any Json being read.
     * @param useBigDecimals Whether to use BigDecimals when reading decimal numbers
     */
    record ReadOptions(
            EOFBehavior eofBehavior,
            boolean useBigDecimals
    ) {
        public ReadOptions {
            Objects.requireNonNull(eofBehavior, "eofBehavior must not be null");
        }

        public ReadOptions() {
            this(EOFBehavior.THROW_EXCEPTION, false);
        }

        public ReadOptions withEOFBehavior(EOFBehavior eofBehavior) {
            return new ReadOptions(eofBehavior, useBigDecimals);
        }

        public ReadOptions withUseBigDecimals(boolean useBigDecimals) {
            return new ReadOptions(eofBehavior, useBigDecimals);
        }
    }

    record StreamReadOptions(
            boolean useBigDecimals
    ) {
        public StreamReadOptions() {
            this(false);
        }
        public StreamReadOptions withUseBigDecimals(boolean useBigDecimals) {
            return new StreamReadOptions(useBigDecimals);
        }
    }

    record EventReadOptions(
            boolean useBigDecimals
    ) {
        public EventReadOptions() {
            this(false);
        }
        public EventReadOptions withUseBigDecimals(boolean useBigDecimals) {
            return new EventReadOptions(useBigDecimals);
        }
    }





}
