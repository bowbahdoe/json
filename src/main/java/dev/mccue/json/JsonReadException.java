package dev.mccue.json;

import java.io.Serial;

public final class JsonReadException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;
    JsonReadException() {
        super();
    }
    JsonReadException(java.lang.String message) {
        super(message);
    }

    static JsonReadException expectedTrue() {
        return new JsonReadException("JSON error (expected true)");
    }

    static JsonReadException expectedFalse() {
        return new JsonReadException("JSON error (expected false)");
    }

    static JsonReadException expectedNull() {
        return new JsonReadException("JSON error (expected null)");
    }

    static JsonReadException unexpectedCharacter(char c) {
        return new JsonReadException("JSON error (unexpected character): " + c);
    }
    static JsonReadException invalidEscapeCharacter(char c) {
        return new JsonReadException("Invalid escaped char: " + c);
    }


    static JsonReadException unexpectedEOF() {
        return new JsonReadException("JSON error (end-of-file)");
    }

    static JsonReadException unexpectedEOFInsideString() {
        return new JsonReadException("JSON error (end-of-file inside string)");
    }

    static JsonReadException unexpectedEOFInsideEscapedChar() {
        return new JsonReadException("JSON error (end-of-file inside escaped char)");
    }

    static JsonReadException unexpectedEOFInsideUnicodeCharacterEscape() {
        return new JsonReadException("JSON error (end-of-file inside Unicode character escape)");
    }

    static JsonReadException invalidArray() {
        return new JsonReadException("JSON error (invalid array)");
    }

    static JsonReadException invalidNumberLiteral() {
        return new JsonReadException("JSON error (invalid array)");
    }

    static JsonReadException missingEntryInObject() {
        return new JsonReadException("JSON error (missing entry in object)");
    }

    static JsonReadException emptyEntryInObject() {
        return new JsonReadException("JSON error empty entry in object is not allowed");
    }

    static JsonReadException missingColonInObject() {
        return new JsonReadException("JSON error (missing `:` in object)");
    }

    static JsonReadException nonStringKeyInObject(char c) {
        return new JsonReadException("JSON error (non-string key in object), found `" + c + "`, expected `\"`");
    }
}
