package dev.mccue.json;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public sealed interface Json {
    sealed interface String extends Json, CharSequence permits dev.mccue.json.String {
        java.lang.String value();

        static String of(java.lang.String value) {
            return new dev.mccue.json.String(value);
        }
    }
    sealed interface Number extends Json permits dev.mccue.json.Number {
        BigDecimal value();

        static Number of(BigDecimal value) {
            return new dev.mccue.json.Number(value);
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

    sealed interface Object extends Json, Map<String, Json> permits dev.mccue.json.Object {
        static Object of(Map<String, Json> value) {
            return new dev.mccue.json.Object(value);
        }

        static Builder builder() {
            return new ObjectBuilder();
        }

        sealed interface Builder permits ObjectBuilder {
            Builder put(String key, Json value);
            Builder putAll(Map<String, ? extends Json> values);
            Object build();
        }
    }
}








