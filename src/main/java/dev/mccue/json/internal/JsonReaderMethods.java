package dev.mccue.json.internal;


import dev.mccue.json.Json;
import dev.mccue.json.JsonNumber;
import dev.mccue.json.JsonReadException;
import dev.mccue.json.JsonReadOptions;
import dev.mccue.json.stream.JsonArrayHandler;
import dev.mccue.json.stream.JsonStreamReadOptions;
import dev.mccue.json.stream.JsonValueHandler;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.io.PushbackReader;

public final class JsonReaderMethods {
    private JsonReaderMethods() {}

    /**
     * Expects to be called with the head of the stream AFTER the
     * initial "\\u".  Reads the next four characters from the stream.
     */
    private static char readHexChar(PushbackReader stream) throws IOException {
        var a = stream.read();
        var b = stream.read();
        var c = stream.read();
        var d = stream.read();

        if (a < 0 || b < 0 || c < 0 || d < 0) {
            throw JsonReadException.unexpectedEOFInsideUnicodeCharacterEscape();
        }

        var s = String.valueOf(new char[] { (char) a, (char) b, (char) c, (char) d });;

        try {
            return (char) Integer.parseInt(s, 16);
        } catch (NumberFormatException e) {
            throw JsonReadException.invalidUnicodeCharacterEscape(e);
        }

    }

    private static char readEscapedChar(PushbackReader stream) throws IOException {
        // Expects to be called with the head of the stream AFTER the
        // initial backslash.
        var c = stream.read();
        if (c < 0) {
            throw JsonReadException.unexpectedEOFInsideEscapedChar();
        }

        return switch ((char) c) {
            case '"', '\\', '/' -> (char) c;
            case 'b' -> '\b'; // backspace
            case 'f' -> '\f'; // formfeed
            case 'n' -> '\n'; // newline
            case 'r' -> '\r'; // return
            case 't' -> '\t'; // tab
            case 'u' -> readHexChar(stream);
            default -> throw JsonReadException.invalidEscapeCharacter((char) c);
        };
    }

    private static String readQuotedString(PushbackReader stream) throws IOException {
        // Expects to be called with the head of the stream AFTER the
        // opening quotation mark.
        var buffer = new StringBuilder();
        while (true) {
            var c = stream.read();
            if (c < 0) {
                throw JsonReadException.unexpectedEOFInsideString();
            }
            switch ((char) c) {
                case '"':
                    return buffer.toString();
                case '\\':
                    buffer.append(readEscapedChar(stream));
                    break;
                default:
                    buffer.append((char) c);
            }
        }
    }

    private static JsonNumber readInteger(String string) {
        return new JsonIntegerImpl(string);
    }

    private static JsonNumber readDecimal(String string) {
        return new JsonDecimalImpl(string);
    }

    private static JsonNumber readNumber(PushbackReader stream) throws IOException {
        var buffer = new StringBuilder();

        enum Stage {
            MINUS,
            INT_ZERO,
            INT_DIGIT,
            FRAC_POINT,
            FRAC_FIRST,
            FRAC_DIGIT,
            EXP_SYMBOL,
            EXP_FIRST,
            EXP_DIGIT
        }

        boolean isDecimal;
        var stage = Stage.MINUS;

        loop:
        while (true) {
            var c = stream.read();
            switch (stage) {
                case MINUS:
                    switch (c) {
                        case '-' -> {
                            buffer.append((char) c);
                            stage = Stage.INT_ZERO;
                            continue;
                        }
                        case '0' -> {
                            buffer.append((char) c);
                            stage = Stage.FRAC_POINT;
                            continue;
                        }
                        case '1', '2', '3', '4', '5', '6', '7', '8', '9' -> {
                            buffer.append((char) c);
                            stage = Stage.INT_DIGIT;
                            continue;
                        }
                        default ->
                                throw JsonReadException.invalidNumberLiteral();
                    }
                case INT_ZERO:
                    switch (c) {
                        case '0' -> {
                            buffer.append((char) c);
                            stage = Stage.FRAC_POINT;
                            continue;
                        }
                        case '1', '2', '3', '4', '5', '6', '7', '8', '9' -> {
                            buffer.append((char) c);
                            stage = Stage.INT_DIGIT;
                            continue;
                        }
                        default ->
                                throw JsonReadException.invalidNumberLiteral();
                    }
                case INT_DIGIT:
                    // at this point, there is at least one digit
                    switch (c) {
                        case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> {
                            buffer.append((char) c);
                            continue;
                        }
                        case '.' -> {
                            buffer.append((char) c);
                            stage = Stage.FRAC_FIRST;
                            continue;
                        }
                        case 'e', 'E' -> {
                            buffer.append((char) c);
                            stage = Stage.EXP_SYMBOL;
                            continue;
                        }
                        case 9, 10, 13, 32, ',', ']', '}' -> {
                            stream.unread(c);
                            isDecimal = false;
                            break loop;
                        }
                        case -1 -> {
                            isDecimal = false;
                            break loop;
                        }
                        default -> {
                            throw JsonReadException.invalidNumberLiteral();
                        }
                    }
                case FRAC_POINT:
                    switch (c) {
                        case '.' -> {
                            buffer.append((char) c);
                            stage = Stage.FRAC_FIRST;
                            continue;
                        }
                        case 'e', 'E' -> {
                            buffer.append((char) c);
                            stage = Stage.EXP_SYMBOL;
                            continue;
                        }
                        case 9, 10, 13, 32, ',', ']', '}' -> {
                            stream.unread(c);
                            isDecimal = false;
                            break loop;
                        }
                        case -1 -> {
                            isDecimal = false;
                            break loop;
                        }
                        default -> {
                            // Disallow zero-padded numbers or invalid characters
                            throw JsonReadException.invalidNumberLiteral();
                        }
                    }
                case FRAC_FIRST:
                    // previous character is a "."
                    switch (c) {
                        case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> {
                            buffer.append((char) c);
                            stage = Stage.FRAC_DIGIT;
                            continue;
                        }
                        default -> {
                            throw JsonReadException.invalidNumberLiteral();
                        }
                    }
                case FRAC_DIGIT:
                    // any number of following digits
                    switch (c) {
                        case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> {
                            buffer.append((char) c);
                            continue;
                        }
                        case 'e', 'E' -> {
                            buffer.append((char) c);
                            stage = Stage.EXP_SYMBOL;
                            continue;
                        }
                        case 9, 10, 13, 32, ',', ']', '}' -> {
                            stream.unread(c);
                            isDecimal = true;
                            break loop;
                        }
                        case  -1 -> {
                            isDecimal = true;
                            break loop;
                        }
                        default -> {
                            // Disallow zero-padded numbers or invalid characters
                            throw JsonReadException.invalidNumberLiteral();
                        }
                    }
                case EXP_SYMBOL:
                    switch (c) {
                        case '-', '+' -> {
                            buffer.append((char) c);
                            stage = Stage.EXP_FIRST;
                            continue;
                        }
                        case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> {
                            buffer.append((char) c);
                            stage = Stage.EXP_DIGIT;
                            continue;
                        }
                        default ->
                                throw JsonReadException.invalidNumberLiteral();
                    }
                case EXP_FIRST:
                    switch (c) {
                        case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> {
                            buffer.append((char) c);
                            stage = Stage.EXP_DIGIT;
                            continue;
                        }
                        default ->
                                throw JsonReadException.invalidNumberLiteral();
                    }
                case EXP_DIGIT:
                    switch (c) {
                        case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> {
                            buffer.append((char) c);
                        }
                        case 9, 10, 13, 32, ',', ']', '}' -> {
                            stream.unread(c);
                            isDecimal = true;
                            break loop;
                        }
                        case -1 -> {
                            isDecimal = true;
                            break loop;
                        }
                        default -> {
                            // Disallow zero-padded numbers or invalid characters
                            throw JsonReadException.invalidNumberLiteral();
                        }
                    }
            }
        }

        if (isDecimal) {
            return readDecimal(buffer.toString());
        }
        else {
            return readInteger(buffer.toString());
        }
    }

    private static int nextToken(PushbackReader stream) throws IOException {
        int c = stream.read();
        while (true) {
            if (32 < c) {
                return c;
            }
            else {
                switch (c) {
                    case 9, 10, 13, 32 -> {
                        c = stream.read();
                    }
                    case -1 -> {
                        return -1;
                    }
                    default ->
                            throw JsonReadException.invalidToken();
                }
            }
        }
    }

    private static @Nullable String readKey(PushbackReader stream) throws IOException {
        var c = nextToken(stream);
        if (c == '\"') {
            var key = readQuotedString(stream);
            if ((int) ':' == nextToken(stream)) {
                return key;
            }
            else {
                throw JsonReadException.missingColonInObject();
            }
        }
        else {
            if (c == '}') {
                return null;
            }
            else {
                throw JsonReadException.nonStringKeyInObject((char) c);
            }
        }
    }

    private static boolean readUll(PushbackReader stream) throws IOException {
        return stream.read() == 'u' && stream.read() == 'l' && stream.read() == 'l';
    }

    private static boolean readRue(PushbackReader stream) throws IOException {
        return stream.read() == 'r' && stream.read() == 'u' && stream.read() == 'e';
    }

    private static boolean readAlse(PushbackReader stream) throws IOException {
        return stream.read() == 'a' && stream.read() == 'l' && stream.read() == 's' && stream.read() == 'e';
    }

    private static void readArrayHelperStream(
            PushbackReader stream,
            JsonStreamReadOptions options,
            JsonArrayHandler arrayHandler
    ) throws IOException {
        while (true) {
            readStream(stream, true, options, arrayHandler);
            switch ((char) nextToken(stream)) {
                case ']':
                    arrayHandler.onArrayEnd();
                    return;
                case ',':
                    continue;
                default:
                    throw JsonReadException.invalidArray();
            }
        }
    }

    private static void readArrayStream(
            PushbackReader stream,
            JsonStreamReadOptions options,
            JsonValueHandler valueHandler
    ) throws IOException {
        var arrayHandler = valueHandler.onArrayStart();
        var c = nextToken(stream);
        switch (c) {
            case ']' ->
                    arrayHandler.onArrayEnd();
            case ',' ->
                    throw JsonReadException.invalidArray();
            default -> {
                stream.unread(c);
                readArrayHelperStream(stream, options, arrayHandler);
            }
        }
    }

    private static void readObjectStream(
            PushbackReader stream,
            JsonStreamReadOptions options,
            JsonValueHandler valueHandler
    ) throws IOException {
        boolean readSomeEntry = false;
        var objectHandler = valueHandler.onObjectStart();
        while (true) {
            var key = readKey(stream);
            if (key != null) {
                readSomeEntry = true;
                var fieldHandler = objectHandler.onField(key);
                readStream(stream, true, options, fieldHandler);
                switch (nextToken(stream)) {
                    case ',':
                        continue;
                    case '}':
                        objectHandler.onObjectEnd();
                        return;
                    default:
                        throw JsonReadException.missingEntryInObject();
                }
            }
            else {
                if (readSomeEntry) {
                    throw JsonReadException.emptyEntryInObject();
                }
                else {
                    objectHandler.onObjectEnd();
                    return;
                }
            }
        }
    }

    public static void readStream(
            PushbackReader stream,
            boolean throwIfEofEncountered,
            JsonStreamReadOptions options,
            JsonValueHandler valueHandler
    ) throws IOException {
        int c = nextToken(stream);
        switch (c) {
            case '-', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> {
                stream.unread(c);
                valueHandler.onNumber(readNumber(stream));
            }
            case '"' -> {
                valueHandler.onString(readQuotedString(stream));
            }
            case 'n' -> {
                if (readUll(stream)) {
                    valueHandler.onNull();
                }
                else {
                    throw JsonReadException.expectedNull();
                }
            }
            case 't' -> {
                if (readRue(stream)) {
                    valueHandler.onTrue();
                }
                else {
                    throw JsonReadException.expectedTrue();
                }
            }
            case 'f' -> {
                if (readAlse(stream)) {
                    valueHandler.onFalse();
                }
                else {
                    throw JsonReadException.expectedFalse();
                }
            }
            case '{' -> {
                readObjectStream(stream, options, valueHandler);
            }
            case '[' -> {
                readArrayStream(stream, options, valueHandler);
            }
            default -> {
                if (c < 0) {
                    if (throwIfEofEncountered) {
                        throw JsonReadException.unexpectedEOF();
                    }
                }
                else {
                    throw JsonReadException.unexpectedCharacter((char) c);
                }
            }
        }
    }

    public static Json read(PushbackReader stream, JsonReadOptions options) throws IOException {
        var handler = new Handlers.BaseTreeValueHandler();
        readStream(stream, false, new JsonStreamReadOptions(), handler);
        if (handler.result == null && options.eofBehavior() == JsonReadOptions.EOFBehavior.THROW_EXCEPTION) {
            throw JsonReadException.unexpectedEOF();
        }
        else {
            return handler.result;
        }
    }

    public static Json readFullyConsume(PushbackReader stream, JsonReadOptions options) throws IOException {
        var result = read(stream, options);
        for (int c = stream.read(); c > 0; c = stream.read()) {
            switch (c) {
                case 9, 10, 13, 32 -> {}
                default -> throw JsonReadException.nonWhitespaceTrailingContents((char) c);
            }
        }
        return result;
    }
}
