package dev.mccue.json;

import dev.mccue.json.serialization.JsonSerializationProxy;
import dev.mccue.json.stream.JsonGenerator;

import java.io.Serial;

public final class JsonTrue implements JsonBoolean {
    @Serial
    private static final long serialVersionUID = 1L;
    static final JsonTrue INSTANCE = new JsonTrue();

    private JsonTrue() {}

    public static JsonTrue instance() {
        return INSTANCE;
    }

    @Override
    public boolean value() {
        return true;
    }

    @Override
    public java.lang.String toString() {
        return "true";
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
        generator.writeTrue();
    }
}

