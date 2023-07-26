package dev.mccue.json;

import dev.mccue.json.internal.ValueCandidate;

/**
 * Options to use when writing Json.
 *
 * @param escapeUnicode If true, non-ascii characters are escaped as \\uXXXX
 * @param escapeJavascriptSeparators If true (default) the Unicode characters U+2028 and U+2029 will
 *                                   be escaped as \\u2028 and \\u2029 even if :escape-unicode is
 *                                   false. (These two characters are valid in pure JSON but are not
 *                                   valid in JavaScript strings.).
 * @param escapeSlash If true (default) the slash / is escaped as \\/
 * @param indentation How many spaces to indent after an object or array start.
 *
 * @author <a href="ethan@mccue.dev">Ethan McCue</a>
 */
@ValueCandidate
public record JsonWriteOptions(
        boolean escapeUnicode,
        boolean escapeJavascriptSeparators,
        boolean escapeSlash,
        int indentation
) {
    public JsonWriteOptions {
        if (indentation < 0) {
            throw new IllegalArgumentException("indent must not be less than zero.");
        }
    }
    public JsonWriteOptions() {
        this(true, true, true, 0);
    }

    public JsonWriteOptions withEscapeUnicode(boolean escapeUnicode) {
        return new JsonWriteOptions(escapeUnicode, escapeJavascriptSeparators, escapeSlash, indentation);
    }

    public JsonWriteOptions withEscapeJavascriptSeparators(boolean escapeJavascriptSeparators) {
        return new JsonWriteOptions(escapeUnicode, escapeJavascriptSeparators, escapeSlash, indentation);
    }

    public JsonWriteOptions withEscapeSlash(boolean escapeSlash) {
        return new JsonWriteOptions(escapeUnicode, escapeJavascriptSeparators, escapeSlash, indentation);
    }

    public JsonWriteOptions withIndentation(int indentation) {
        return new JsonWriteOptions(escapeUnicode, escapeJavascriptSeparators, escapeSlash, indentation);
    }
}