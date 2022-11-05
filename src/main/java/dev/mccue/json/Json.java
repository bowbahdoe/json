package dev.mccue.json;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public sealed interface Json {
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
        abstract BigDecimal bigDecimalValue();

        abstract java.math.BigInteger bigIntegerValue();

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
        sealed interface Builder permits ArrayBuilder {
            Builder add(Json value);
            Builder addAll(Collection<? extends Json> value);
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

        sealed interface Builder permits ObjectBuilder {
            Builder put(java.lang.String key, Json value);
            Builder putAll(Map<java.lang.String, ? extends Json> values);

            Object build();
        }
    }

    static Json parse(CharSequence jsonText) {
        var parser = new JsonReader();

        try {
            return parser.read(new PushbackReader(new StringReader(jsonText.toString()), 64), false, new JsonReader.Options());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }


    static Json read(CharSequence jsonText, JsonReader.Options options) {
        var parser = new JsonReader();

        try {
            return parser.read(new PushbackReader(new StringReader(jsonText.toString()), 64), false, options);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    static void write(Json json, Appendable out, JsonReader.Options options) {

    }
}








