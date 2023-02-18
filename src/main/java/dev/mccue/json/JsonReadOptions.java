package dev.mccue.json;

import java.util.Objects;

/**
 * @param eofBehavior What to do if an attempted read reaches an EOF without any Json being read.
 * @param useBigDecimals Whether to use BigDecimals when reading decimal numbers
 */
public record JsonReadOptions(
        EOFBehavior eofBehavior,
        boolean useBigDecimals
) {
    public JsonReadOptions {
        Objects.requireNonNull(eofBehavior, "eofBehavior must not be null");
    }

    public JsonReadOptions() {
        this(EOFBehavior.THROW_EXCEPTION, false);
    }

    public JsonReadOptions withEOFBehavior(EOFBehavior eofBehavior) {
        return new JsonReadOptions(eofBehavior, useBigDecimals);
    }

    public JsonReadOptions withUseBigDecimals(boolean useBigDecimals) {
        return new JsonReadOptions(eofBehavior, useBigDecimals);
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
