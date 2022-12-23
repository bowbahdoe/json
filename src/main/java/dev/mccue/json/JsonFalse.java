package dev.mccue.json;

import dev.mccue.json.internal.ValueCandidate;
import dev.mccue.json.serialization.JsonSerializationProxy;
import dev.mccue.json.stream.JsonGenerator;

import java.io.Serial;

@ValueCandidate
public final class JsonFalse implements JsonBoolean {
    @Serial
    private static final long serialVersionUID = 1L;
    static final JsonFalse INSTANCE = new JsonFalse();

    private JsonFalse() {}

    public static JsonFalse instance() {
        return JsonFalse.INSTANCE;
    }

    @Override
    public boolean value() {
        return false;
    }

    @Override
    public java.lang.String toString() {
        return "false";
    }

    @Serial
    private java.lang.Object writeReplace() {
        return new JsonSerializationProxy(Json.writeString(this));
    }

    @Serial
    private Object readResolve() {
        throw new IllegalStateException();
    }

    @Override
    public void write(JsonGenerator generator) {
        generator.writeFalse();
    }
}
