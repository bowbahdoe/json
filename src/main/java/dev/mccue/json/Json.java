package dev.mccue.json;

import java.io.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Immutable tree representation of Json.
 */
public sealed interface Json extends Serializable, ToJson {
    static Json of(ToJson value) {
        return value == null ? Json.Null.instance() : value.toJson();
    }

    static Json of(BigDecimal value) {
        return value == null ? Json.Null.instance() : new dev.mccue.json.BigDecimal(value);
    }

    static Json of(double value) {
        return new dev.mccue.json.Double(value);
    }

    static Json of(long value) {
        return new dev.mccue.json.Long(value);
    }

    static Json of(float value) {
        return new dev.mccue.json.Double(value);
    }

    static Json of(int value) {
        return new dev.mccue.json.Long(value);
    }

    static Json of(java.lang.Double value) {
        return value == null ? Json.Null.instance() : new dev.mccue.json.Double(value);
    }

    static Json of(java.lang.Long value) {
        return value == null ? Json.Null.instance() : new dev.mccue.json.Long(value);
    }

    static Json of(Float value) {
        return value == null ? Json.Null.instance() : new dev.mccue.json.Double(value);
    }

    static Json of(Integer value) {
        return value == null ? Json.Null.instance() : new dev.mccue.json.Long(value);
    }

    static Json of(java.math.BigInteger value) {
        return value == null ? Json.Null.instance() : new dev.mccue.json.BigInteger(value);
    }

    static Json of(java.lang.String value) {
        return value == null ? Json.Null.instance() : new dev.mccue.json.String(value);
    }

    static Json ofNull() {
        return Json.Null.instance();
    }

    static Json ofTrue() {
        return Json.Boolean.of(true);
    }

    static Json ofFalse() {
        return Json.Boolean.of(false);
    }


    static Json of(boolean b) {
        return Json.Boolean.of(b);
    }

    static Json of(java.lang.Boolean b) {
        return b == null ? Json.Null.instance() : Json.Boolean.of(b);
    }

    static Json of(Collection<? extends ToJson> jsonList) {
        return jsonList == null
                ? Json.Null.instance()
                : new dev.mccue.json.Array(
                        jsonList.stream()
                                .map(json -> json == null ? Json.Null.instance() : json.toJson())
                                .toList()
                );
    }

    static Json of(Map<java.lang.String, ? extends ToJson> jsonMap) {
        return jsonMap == null
                ? Json.Null.instance()
                : new dev.mccue.json.Object(
                        jsonMap
                                .entrySet()
                                .stream()
                                .collect(Collectors.toUnmodifiableMap(
                                        Map.Entry::getKey,
                                        entry -> entry.getValue() == null
                                                ? Json.Null.instance()
                                                : entry.getValue().toJson()
                                ))
                );
    }

    static Json.Object.Builder objectBuilder() {
        return Json.Object.builder();
    }

    static Json.Object.Builder objectBuilder(Map<java.lang.String, ? extends ToJson> object) {
        if (object instanceof dev.mccue.json.Object o) {
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

    static Json.Array.Builder arrayBuilder() {
        return Json.Array.builder();
    }

    static Json.Array.Builder arrayBuilder(Collection<? extends ToJson> object) {
        if (object instanceof dev.mccue.json.Array o) {
            return new ArrayBuilder(new ArrayList<>(o));
        }
        else {
            return new ArrayBuilder(new ArrayList<>(object.stream()
                    .map(Json::of)
                    .toList()));
        }
    }

    @Override
    default Json toJson() {
        return this;
    }

    sealed interface String extends Json, CharSequence permits
            dev.mccue.json.String {
        java.lang.String value();

        static String of(java.lang.String value) {
            return new dev.mccue.json.String(value);
        }
    }

    sealed abstract class Number extends java.lang.Number implements Json permits
            dev.mccue.json.BigDecimal,
            dev.mccue.json.Double,
            dev.mccue.json.Long,
            dev.mccue.json.BigInteger {
        public abstract BigDecimal bigDecimalValue();

        public abstract java.math.BigInteger bigIntegerValue();

        public abstract int intValueExact();

        public abstract long longValueExact();

        public abstract java.math.BigInteger bigIntegerValueExact();

        public abstract boolean isIntegral();

        static Number of(BigDecimal value) {
            return new dev.mccue.json.BigDecimal(value);
        }

        static Number of(double value) {
            return new dev.mccue.json.Double(value);
        }

        static Number of(long value) {
            return new dev.mccue.json.Long(value);
        }

        static Number of(float value) {
            return new dev.mccue.json.Double(value);
        }

        static Number of(int value) {
            return new dev.mccue.json.Long(value);
        }

        static Number of(java.math.BigInteger value) {
            return new dev.mccue.json.BigInteger(value);
        }
    }

    sealed interface Boolean extends Json permits True, False {
        boolean value();

        static Boolean of(boolean value) {
            return value ? True.INSTANCE : False.INSTANCE;
        }
    }

    sealed interface Null extends Json permits dev.mccue.json.Null {
        static Null instance() {
            return dev.mccue.json.Null.INSTANCE;
        }
    }

    sealed interface Array extends Json, List<Json> permits dev.mccue.json.Array {
        static Array of(Json... values) {
            return new dev.mccue.json.Array(Arrays.asList(values));
        }
        static Array of(List<Json> value) {
            return new dev.mccue.json.Array(value);
        }

        static Builder builder() {
            return new ArrayBuilder();
        }

        static Builder builder(int initialCapacity) {
            return new ArrayBuilder(initialCapacity);
        }
        sealed interface Builder permits ArrayBuilder {
            Builder add(Json value);
            Builder addAll(Collection<? extends ToJson> value);
            default Builder add(ToJson value) {
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

            Array build();
        }
    }

    sealed interface Object extends Json, Map<java.lang.String, Json> permits dev.mccue.json.Object {
        static Object of(Map<java.lang.String, Json> value) {
            return new dev.mccue.json.Object(value);
        }

        static Builder builder() {
            return new ObjectBuilder();
        }

        static Builder builder(int initialCapacity) {
            return new ObjectBuilder(initialCapacity);
        }

        sealed interface Builder permits ObjectBuilder {
            Builder put(java.lang.String key, ToJson value);

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

            default Builder put(java.lang.String key, Collection<? extends ToJson> value) {
                return put(key, Json.of(value));
            }

            default Builder put(java.lang.String key, Map<java.lang.String, ? extends ToJson> value) {
                return put(key, Json.of(value));
            }

            default Builder put(java.lang.String key, Json value) {
                return put(key, (ToJson) value);
            }

            default Builder put(java.lang.String key, Json.Object value) {
                return put(key, (ToJson) value);
            }

            default Builder put(java.lang.String key, Json.Array value) {
                return put(key, (ToJson) value);
            }

            Builder putAll(Map<java.lang.String, ? extends ToJson> values);

            Object build();
        }
    }

    static Json readString(CharSequence jsonText) throws JsonReadException {
        return readString(jsonText, new ReadOptions());
    }

    static Json readString(CharSequence jsonText, ReadOptions options) throws JsonReadException {
        var parser = new JsonReader();

        try {
            return parser.read(new PushbackReader(new StringReader(jsonText.toString()), 64), false, options);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    static Json read(Reader reader, ReadOptions options) throws IOException, JsonReadException {
        var parser = new JsonReader();
        return parser.read(new PushbackReader(reader, 64), false, options);
    }

    static Json read(Reader reader) throws IOException, JsonReadException {
        return read(reader, new ReadOptions());
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

    static void write(Json json, Appendable out, WriteOptions options) throws IOException {
        new JsonWriter().write(json, out, options);
    }

    static void write(Json json, Writer writer) throws IOException {
        new JsonWriter().write(json, writer, new WriteOptions());
    }

    static void write(Json json, Appendable out) throws IOException {
        new JsonWriter().write(json, out, new WriteOptions());
    }

    /**
     * @param escapeUnicode If true, non-ascii characters are escaped as \\uXXXX
     * @param escapeJavascriptSeparators If true (default) the Unicode characters U+2028 and U+2029 will
     *                                   be escaped as \\u2028 and \\u2029 even if :escape-unicode is
     *                                   false. (These two characters are valid in pure JSON but are not
     *                                   valid in JavaScript strings.).
     * @param escapeSlash If true (default) the slash / is escaped as \\/
     */
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
}
