package dev.mccue.json;

import dev.mccue.json.internal.ValueCandidate;
import dev.mccue.json.serialization.JsonSerializationProxy;
import dev.mccue.json.stream.JsonGenerator;

import java.io.Serial;

@ValueCandidate
public final class JsonNull implements Json {
    @Serial
    private static final long serialVersionUID = 1L;
    private static final JsonNull INSTANCE = new JsonNull();

    private JsonNull() {}

    public static JsonNull instance() {
        return INSTANCE;
    }

    @Override
    public java.lang.String toString() {
        return "null";
    }

    @Serial
    private Object writeReplace() {
        return new JsonSerializationProxy(Json.writeString(this));
    }

    @Serial
    private Object readResolve() {
        throw new IllegalStateException();
    }

    @Override
    public void write(JsonGenerator generator) {
        generator.writeNull();
    }
}