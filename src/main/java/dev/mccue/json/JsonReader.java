package dev.mccue.json;


import dev.mccue.json.stream.JsonArrayHandler;
import dev.mccue.json.stream.JsonValueHandler;

import java.io.IOException;
import java.io.PushbackReader;
import java.math.BigDecimal;
import java.math.BigInteger;

final class JsonReader {
    private JsonReader() {}

    static final int MINIMUM_PUSHBACK_BUFFER_SIZE = 64;

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
            throw new JsonReadException("JSON error (end-of-file inside Unicode character escape)");
        }

        var s = java.lang.String.valueOf(new char[] { (char) a, (char) b, (char) c, (char) d });;

        return (char) Integer.parseInt(s, 16);
    }

    private static char readEscapedChar(PushbackReader stream) throws IOException {
        // Expects to be called with the head of the stream AFTER the
        // initial backslash.
        var c = stream.read();
        if (c < 0) {
            throw new JsonReadException("JSON error (end-of-file inside escaped char)");
        }

        return switch ((char) c) {
            case '"', '\\', '/' -> (char) c;
            case 'b' -> '\b'; // backspace
            case 'f' -> '\f'; // formfeed
            case 'n' -> '\n'; // newline
            case 'r' -> '\r'; // return
            case 't' -> '\t'; // tab
            case 'u' -> readHexChar(stream);
            default -> throw new JsonReadException("Invalid escaped char: " + (char) c);
        };
    }

    private static java.lang.String slowReadString(PushbackReader stream, java.lang.String alreadyRead) throws IOException {
        var buffer = new StringBuilder(alreadyRead);
        while (true) {
            var c = stream.read();
            if (c < 0) {
                throw new JsonReadException("JSON error (end-of-file inside string)");
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

    private static java.lang.String readQuotedString(PushbackReader stream) throws IOException {
        // Expects to be called with the head of the stream AFTER the
        // opening quotation mark.
        var buffer = new char[MINIMUM_PUSHBACK_BUFFER_SIZE];
        int read = stream.read(buffer, 0, MINIMUM_PUSHBACK_BUFFER_SIZE);
        int endIndex = read - 1;
        if (read < 0) {
            throw new JsonReadException("JSON error (end-of-file inside string)");
        }

        int i = 0;
        while (true) {
            var c = buffer[i];
            switch (c) {
                case '"': {
                    var off = i + 1;
                    var len = read - off;
                    stream.unread(buffer, off, len);
                    return new java.lang.String(buffer, 0, i);
                }
                case '\\': {
                    var off = i;
                    var len = read - off;
                    stream.unread(buffer, off, len);
                    return slowReadString(stream, new java.lang.String(buffer, 0, i));
                }
                default:
                    if (i == endIndex) {
                        stream.unread(c);
                        return slowReadString(stream, new java.lang.String(buffer, 0, i));
                    }
                    else {
                        i++;
                    }
            }
        }
    }

    private static Json.Number readInteger(java.lang.String string) {
        if (string.length() < 18) { // definitely fits in a Long
            return Json.Number.of(java.lang.Long.parseLong(string));
        }
        else {
            try {
                return Json.Number.of(java.lang.Long.parseLong(string));
            } catch (NumberFormatException __) {
                return Json.Number.of(new BigInteger(string));
            }
        }
    }

    private static Json.Number readDecimal(java.lang.String string, boolean bigDecimal) {
        if (bigDecimal) {
            return new dev.mccue.json.BigDecimal(new BigDecimal(string));
        }
        else {
            return new dev.mccue.json.Double(java.lang.Double.parseDouble(string));
        }
    }

    private static Json.Number readNumber(PushbackReader stream, boolean bigDecimal) throws IOException {
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
            EXP_DIGIT,

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
                        case '1', '2', '3', '4', '5', '6', '7', '8', '9'-> {
                            buffer.append((char) c);
                            stage = Stage.INT_DIGIT;
                            continue;
                        }
                        default ->
                            throw new JsonReadException("JSON error (invalid number literal)");
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
                            throw new JsonReadException("JSON error (invalid number literal)");
                    }
                case INT_DIGIT:
                    // at this point, there is at least one digit
                    switch (c) {
                        case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> {
                            buffer.append((char) c);
                            stage = Stage.INT_DIGIT;
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
                        case 9, 10, 13, 32, ',', ']', '}', -1 -> {
                            stream.unread(c);
                            isDecimal = false;
                            break loop;
                        }
                        default -> {
                            throw new JsonReadException("JSON error (invalid number literal)");
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
                        case 9, 10, 13, 32, ',', ']', '}', -1 -> {
                            stream.unread(c);
                            isDecimal = false;
                            break loop;
                        }
                        default -> {
                            // Disallow zero-padded numbers or invalid characters
                            throw new JsonReadException("JSON error (invalid number literal)");
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
                            throw new JsonReadException("JSON error (invalid number literal)");
                        }
                    }
                case FRAC_DIGIT:
                    // any number of following digits
                    switch (c) {
                        case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> {
                            buffer.append((char) c);
                            stage = Stage.FRAC_DIGIT;
                            continue;
                        }
                        case 'e', 'E' -> {
                            buffer.append((char) c);
                            stage = Stage.EXP_SYMBOL;
                            continue;
                        }
                        case 9, 10, 13, 32, ',', ']', '}', -1 -> {
                            stream.unread(c);
                            isDecimal = true;
                            break loop;
                        }
                        default -> {
                            // Disallow zero-padded numbers or invalid characters
                            throw new JsonReadException("JSON error (invalid number literal)");
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
                        default -> throw new JsonReadException();
                    }
                case EXP_FIRST:
                    switch (c) {
                        case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> {
                            buffer.append((char) c);
                            stage = Stage.EXP_DIGIT;
                            continue;
                        }
                        default -> throw new JsonReadException("JSON error (invalid number literal)");
                    }
                case EXP_DIGIT:
                    switch (c) {
                        case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> {
                            buffer.append((char) c);
                            stage = Stage.EXP_DIGIT;
                            continue;
                        }
                        case 9, 10, 13, 32, ',', ']', '}', -1 -> {
                            stream.unread(c);
                            isDecimal = true;
                            break loop;
                        }
                        default -> {
                            // Disallow zero-padded numbers or invalid characters
                            throw new JsonReadException("JSON error (invalid number literal)");
                        }
                    }
            }
        }

        if (isDecimal) {
            return readDecimal(buffer.toString(), bigDecimal);
        }
        else {
            return readInteger(buffer.toString());
        }
    }

    private static int nextToken(PushbackReader stream) throws IOException {
        var c = stream.read();
        while (true) {
            if (32 < c) {
                return c;
            }
            else {
                 switch (c) {
                     case 9, 10, 13, 32: {
                        c = stream.read();
                        continue;
                     }
                     case -1: {
                         return -1;
                     }
                     default:
                         throw new JsonReadException();
                }
            }
        }
    }

    private static void readArrayHelper(
            PushbackReader stream,
            Json.StreamReadOptions options,
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
                    throw new JsonReadException("JSON error (invalid array)");
            }
        }
    }

    private static void readArray(
            PushbackReader stream,
            Json.StreamReadOptions options,
            JsonValueHandler valueHandler
    ) throws IOException {
        var arrayHandler = valueHandler.onArrayStart();
        var c = nextToken(stream);
        switch (c) {
            case ']':
                arrayHandler.onArrayEnd();
            case ',':
                throw new JsonReadException("JSON error (invalid array)");
            default: {
                stream.unread(c);
                readArrayHelper(stream, options, arrayHandler);
            }
        }
    }

    private static /* @Nullable */ java.lang.String readKey(PushbackReader stream) throws IOException {
        var c = nextToken(stream);
        if (c == '\"') {
            var key = readQuotedString(stream);
            if ((int) ':' == nextToken(stream)) {
                return key;
            }
            else {
                throw new JsonReadException("JSON error (missing `:` in object)");
            }
        }
        else {
            if (c == '}') {
                return null;
            }
            else {
                throw new JsonReadException("JSON error (non-string key in object), found `" + (char) c + "`, expected `\"`");
            }
        }
    }

    private static void readObject(
            PushbackReader stream,
            Json.StreamReadOptions options,
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
                        objectHandler.objectEnd();
                        return;
                    default:
                        throw new JsonReadException("JSON error (missing entry in object)");
                }
            }
            else {
                if (readSomeEntry) {
                    throw new JsonReadException("JSON error empty entry in object is not allowed");
                }
                else {
                    return;
                }
            }
        }
    }

    static void readStream(
            PushbackReader stream,
            boolean eofError,
            Json.StreamReadOptions options,
            JsonValueHandler valueHandler
    ) throws IOException {
        int c = nextToken(stream);
        switch (c) {
            case '-', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> {
                stream.unread(c);
                valueHandler.onNumber(readNumber(stream, options.useBigDecimals())); // (:bigdec options)
            }
            case '"' -> {
                valueHandler.onString(readQuotedString(stream));
            }
            case 'n' -> {
                if (stream.read() == 'u' && stream.read() == 'l' && stream.read() == 'l') {
                    valueHandler.onNull();
                }
                else {
                    throw new JsonReadException("JSON error (expected null)");
                }
            }
            case 't' -> {
                if (stream.read() == 'r' && stream.read() == 'u' && stream.read() == 'e') {
                    valueHandler.onTrue();
                }
                else {
                    throw new JsonReadException("JSON error (expected true)");
                }
            }
            case 'f' -> {
                if (stream.read() == 'a' && stream.read() == 'l' && stream.read() == 's' && stream.read() == 'e') {
                    valueHandler.onFalse();
                }
                else {
                    throw new JsonReadException("JSON error (expected false)");
                }
            }
            case '{' -> {
                readObject(stream, options, valueHandler);
            }
            case '[' -> {
                readArray(stream, options, valueHandler);
            }
            default -> {
                if (c < 0) {
                    if (eofError) {
                        throw new JsonReadException("JSON error (end-of-file)");
                    }
                }
                else {
                    throw new JsonReadException("JSON error (unexpected character): " + (char) c);
                }
            }
        }
    }

    static Json read(PushbackReader stream, Json.ReadOptions options) throws IOException {
        var handler = new Handlers.BaseTreeValueHandler(options.eofBehavior());
        readStream(stream, false, new Json.StreamReadOptions(options.useBigDecimals()), handler);
        if (handler.result == null && options.eofBehavior() == Json.EOFBehavior.THROW_EXCEPTION) {
            throw new JsonReadException("JSON error (end-of-file)");
        }
        else {
            return handler.result;
        }
    }
}
