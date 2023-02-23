package dev.mccue.json;

import dev.mccue.json.internal.ValueCandidate;
import dev.mccue.json.serialization.JsonSerializationProxy;
import dev.mccue.json.stream.JsonGenerator;

import java.io.Serial;

/**
 * Represents true in the json data model.
 */
@ValueCandidate
public final class JsonTrue implements JsonBoolean {
    @Serial
    private static final long serialVersionUID = 1L;

    private static final JsonTrue INSTANCE = new JsonTrue();

    private JsonTrue() {}

    /**
     * Returns an instance of {@link JsonTrue}. Guaranteed to be comparable with ==,
     * but not to be safe for identity sensitive operations.
     *
     * @return An instance of {@link JsonTrue}
     */
    public static JsonTrue instance() {
        return INSTANCE;
    }

    /**
     * Returns true.
     *
     * @return true.
     */
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

