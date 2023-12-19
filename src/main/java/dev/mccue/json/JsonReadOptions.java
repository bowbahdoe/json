package dev.mccue.json;

import java.util.Objects;

/**
 * Options for customizing the process of reading Json.
 *
 * @param eofBehavior What to do if an attempted read reaches an EOF without any Json being read.
 *
 * @author <a href="ethan@mccue.dev">Ethan McCue</a>
 */
public record JsonReadOptions(
        EOFBehavior eofBehavior
) {
    public JsonReadOptions {
        Objects.requireNonNull(eofBehavior, "eofBehavior must not be null");
    }

    public JsonReadOptions() {
        this(EOFBehavior.THROW_EXCEPTION);
    }

    public JsonReadOptions withEOFBehavior(EOFBehavior eofBehavior) {
        return new JsonReadOptions(eofBehavior);
    }

    /**
     * Behavior to exhibit when an EOF is reached and no Json is read.
     */
    public enum EOFBehavior {
        /**
         * Throw an exception.
         */
        THROW_EXCEPTION,
        /**
         * Return a "true null", not a Json null.
         */
        RETURN_NULL
    }
}
