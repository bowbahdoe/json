package dev.mccue.json;

import dev.mccue.json.internal.ValueCandidate;
import dev.mccue.json.serialization.JsonSerializationProxy;
import dev.mccue.json.stream.JsonGenerator;

import java.io.Serial;

/**
 * Represents false in the json data model.
 */
@ValueCandidate
public final class JsonFalse implements JsonBoolean {
    @Serial
    private static final long serialVersionUID = 1L;
    private static final JsonFalse INSTANCE = new JsonFalse();

    private JsonFalse() {}

    /**
     * Returns an instance of {@link JsonFalse}. Guaranteed to be comparable with ==,
     * but not to be safe for identity sensitive operations.
     *
     * @return An instance of {@link JsonFalse}
     */
    public static JsonFalse instance() {
        return JsonFalse.INSTANCE;
    }

    /**
     * Returns false.
     *
     * @return false.
     */
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
