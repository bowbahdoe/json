package dev.mccue.json;

import dev.mccue.json.internal.ValueCandidate;

/**
 * Represents a boolean in the json data model.
 *
 * @author <a href="ethan@mccue.dev">Ethan McCue</a>
 */
@ValueCandidate
public sealed interface JsonBoolean extends Json, Comparable<JsonBoolean> permits JsonFalse, JsonTrue {
    /**
     * Returns true or false.
     *
     * @return true or false.
     */
    boolean value();

    /**
     * Creates a {@link JsonBoolean} from the given value. Guaranteed to be comparable with ==,
     * but not to be safe for identity sensitive operations.
     *
     * @param value The boolean to wrap.
     * @return A {@link JsonBoolean}.
     */
    static JsonBoolean of(boolean value) {
        return value ? JsonTrue.instance() : JsonFalse.instance();
    }

    @Override
    default int compareTo(JsonBoolean o) {
        return Boolean.compare(this.value(), o.value());
    }
}