package dev.mccue.json;

public sealed interface JsonBoolean extends Json, Comparable<JsonBoolean> permits JsonFalse, JsonTrue {
    boolean value();

    static JsonBoolean of(boolean value) {
        return value ? JsonTrue.instance() : JsonFalse.instance();
    }

    @Override
    default int compareTo(JsonBoolean o) {
        return Boolean.compare(this.value(), o.value());
    }
}