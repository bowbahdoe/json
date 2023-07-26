package dev.mccue.json;

import dev.mccue.json.internal.ValueCandidate;
import dev.mccue.json.serialization.JsonSerializationProxy;
import dev.mccue.json.stream.JsonGenerator;

import java.io.Serial;

/**
 * Represents null in the json data model.
 *
 * @author <a href="ethan@mccue.dev">Ethan McCue</a>
 */
@ValueCandidate
public final class JsonNull implements Json {
    @Serial
    private static final long serialVersionUID = 1L;
    private static final JsonNull INSTANCE = new JsonNull();

    private JsonNull() {}

    /**
     * Returns an instance of {@link JsonNull}. Guaranteed to be comparable with ==,
     * but not to be safe for identity sensitive operations.
     *
     * @return An instance of {@link JsonNull}
     */
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