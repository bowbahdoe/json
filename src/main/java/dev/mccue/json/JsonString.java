package dev.mccue.json;

import dev.mccue.json.internal.StringImpl;

public sealed interface JsonString extends Json, CharSequence permits
        StringImpl {

    static JsonString of(String value) {
        return new StringImpl(value);
    }
}