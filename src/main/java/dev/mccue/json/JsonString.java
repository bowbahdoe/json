package dev.mccue.json;

import dev.mccue.json.internal.StringImpl;
import dev.mccue.json.internal.ValueCandidate;

/**
 * Represents a string in the json data model.
 *
 * @author <a href="ethan@mccue.dev">Ethan McCue</a>
 */
@ValueCandidate
public sealed interface JsonString extends Json, CharSequence permits
        StringImpl {

    /**
     * Creates a {@link JsonString} from the given value. Will throw on
     * null input.
     *
     * @param value The {@link String} to turn into a {@link JsonString}
     * @return The newly created {@link JsonString}
     */
    static JsonString of(String value) {
        return new StringImpl(value);
    }

    /**
     * <p>
     *    The toString representation of a {@link JsonString}
     *    is <i>not</i> safe to be used as json for output. It will
     *    be the exact same representation as the underlying {@link String}.
     * </p>
     *
     * <p>
     *     You should use {@link Json#write(JsonEncodable, java.io.Writer)} or
     *     {@link Json#writeString(JsonEncodable)} to turn a {@link JsonString}
     *     into json output.
     * </p>
     *
     * @return The underlying {@link String} for this object.
     */
    @Override
    String toString();
}