package dev.mccue.json.serialization;

import dev.mccue.json.Json;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * Serialization proxy for the JSON tree structure represented by this
 * library.
 *
 * <p>
 *     The string contained within this record should be json. If it is not
 *     it will simply fail to read back in.
 * </p>
 *
 * <p>
 *     This exists so that the internal representation used for JSON can be evolved
 *     freely.
 * </p>
 *
 * @param json A string representation of the JSON being serialized.
 * @author <a href="ethan@mccue.dev">Ethan McCue</a>
 */
public record JsonSerializationProxy(String json) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public JsonSerializationProxy {
        Objects.requireNonNull(json);
    }

    @Serial
    public Object readResolve() {
        return Json.readString(json);
    }
}
