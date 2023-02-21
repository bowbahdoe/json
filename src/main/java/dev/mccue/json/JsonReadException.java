package dev.mccue.json;

import java.io.Serial;

public final class JsonReadException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    private JsonReadException() {
        super();
    }
    private JsonReadException(String message) {
        super(message);
    }

    public static JsonReadException invalidToken() {
        throw new JsonReadException();
    }

    public static JsonReadException expectedTrue() {
        return new JsonReadException("JSON error (expected true)");
    }

    public static JsonReadException expectedFalse() {
        return new JsonReadException("JSON error (expected false)");
    }

    public static JsonReadException expectedNull() {
        return new JsonReadException("JSON error (expected null)");
    }

    public static JsonReadException unexpectedCharacter(char c) {
        return new JsonReadException("JSON error (unexpected character): " + c);
    }
    public static JsonReadException invalidEscapeCharacter(char c) {
        return new JsonReadException("JSON error (invalid escaped char): " + c);
    }


    public static JsonReadException unexpectedEOF() {
        return new JsonReadException("JSON error (end-of-file)");
    }

    public static JsonReadException extraData(char c) {
        return new JsonReadException("JSON error (extra data): " + c);
    }

    public static JsonReadException unexpectedEOFInsideString() {
        return new JsonReadException("JSON error (end-of-file inside string)");
    }

    public static JsonReadException unexpectedEOFInsideEscapedChar() {
        return new JsonReadException("JSON error (end-of-file inside escaped char)");
    }

    public static JsonReadException unexpectedEOFInsideUnicodeCharacterEscape() {
        return new JsonReadException("JSON error (end-of-file inside Unicode character escape)");
    }

    public static JsonReadException invalidArray() {
        return new JsonReadException("JSON error (invalid array)");
    }

    public static JsonReadException invalidNumberLiteral() {
        return new JsonReadException("JSON error (invalid number literal)");
    }

    public static JsonReadException missingEntryInObject() {
        return new JsonReadException("JSON error (missing entry in object)");
    }

    public static JsonReadException emptyEntryInObject() {
        return new JsonReadException("JSON error empty entry in object is not allowed");
    }

    public static JsonReadException missingColonInObject() {
        return new JsonReadException("JSON error (missing `:` in object)");
    }

    public static JsonReadException nonStringKeyInObject(char c) {
        return new JsonReadException("JSON error (non-string key in object), found `" + c + "`, expected `\"`");
    }

    public static JsonReadException nonWhitespaceTrailingContents(char c) {
        return new JsonReadException("JSON error (non-whitespace trailing character): `" + c + "`");
    }
}
