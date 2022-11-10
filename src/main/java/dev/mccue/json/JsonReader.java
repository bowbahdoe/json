package dev.mccue.json;


import java.io.IOException;
import java.io.PushbackReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

final class JsonReader {
    /**
     * Expects to be called with the head of the stream AFTER the
     * initial "\\u".  Reads the next four characters from the stream.
     */
    private char readHexChar(PushbackReader stream) throws IOException {
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

    private char readEscapedChar(PushbackReader stream) throws IOException {
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

    private java.lang.String slowReadString(PushbackReader stream, java.lang.String alreadyRead) throws IOException {
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

    private Json.String readQuotedString(PushbackReader stream) throws IOException {
        // Expects to be called with the head of the stream AFTER the
        // opening quotation mark.
        var buffer = new char[64];
        int read = stream.read(buffer, 0, 64);
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
                    return new dev.mccue.json.String(new java.lang.String(buffer, 0, i));
                }
                case '\\': {
                    var off = i;
                    var len = read - off;
                    stream.unread(buffer, off, len);
                    return new dev.mccue.json.String(slowReadString(stream, new java.lang.String(buffer, 0, i)));
                }
                default:
                    if (i == endIndex) {
                        stream.unread(c);
                        return new dev.mccue.json.String(slowReadString(stream, new java.lang.String(buffer, 0, i)));
                    }
                    else {
                        i++;
                    }
            }
        }
    }

    private Json.Number readInteger(java.lang.String string) {
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

    private Json.Number readDecimal(java.lang.String string, boolean bigDecimal) {
        if (bigDecimal) {
            return new dev.mccue.json.BigDecimal(new BigDecimal(string));
        }
        else {
            return new dev.mccue.json.Double(java.lang.Double.parseDouble(string));
        }
    }

    private Json.Number readNumber(PushbackReader stream, boolean bigDecimal) throws IOException {
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
                        case '1', '2', '3', '4', '5', '6', '7', '8', '9' -> {
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

    private int nextToken(PushbackReader stream) throws IOException {
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

    private Json.Array readArrayHelper(PushbackReader stream, Json.ReadOptions options) throws IOException {
        var result = Json.Array.builder();
        while (true) {
            result.add(read(stream, true, options));
            switch ((char) nextToken(stream)) {
                case ']':
                    return result.build();
                case ',':
                    continue;
                default:
                    throw new JsonReadException("JSON error (invalid array)");
            }
        }

    }
    private Json.Array readArray(PushbackReader stream, Json.ReadOptions options) throws IOException {
        var c = nextToken(stream);
        switch (c) {
            case ']':
                return Json.Array.of(List.of());
            case ',':
                throw new JsonReadException("JSON error (invalid array)");
            default: {
                stream.unread(c);
                return readArrayHelper(stream, options);
            }
        }
    }

    private Optional<Json.String> readKey(PushbackReader stream) throws IOException {
        var c = nextToken(stream);
        if (c == '\"') {
            var key = readQuotedString(stream);
            if ((int) ':' == nextToken(stream)) {
                return Optional.of(key);
            }
            else {
                throw new JsonReadException("JSON error (missing `:` in object)");
            }
        }
        else {
            if (c == '}') {
                return Optional.empty();
            }
            else {
                throw new JsonReadException("JSON error (non-string key in object), found `" + (char) c + "`, expected `\"`");
            }
        }
    }

    private Json.Object readObject(PushbackReader stream, Json.ReadOptions options) throws IOException {
        var result = Json.Object.builder();

        while (true) {
            var key = readKey(stream).orElse(null);
            if (key != null) {
                var value = read(stream, true, options);
                result.put(key.value(), value);
                switch (nextToken(stream)) {
                    case ',':
                        continue;
                    case '}':
                        return result.build();
                    default:
                        throw new JsonReadException("JSON error (missing entry in object)");
                }
            }
            else {
                var r = result.build();
                if (r.isEmpty()) {
                    return r;
                }
                else {
                    throw new JsonReadException("JSON error empty entry in object is not allowed");
                }
            }
        }
    }

    Json read(PushbackReader stream, boolean eofError, Json.ReadOptions options) throws IOException {
        int c = nextToken(stream);
        switch (c) {
            case '-', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> {
                stream.unread(c);
                return readNumber(stream, options.useBigDecimals()); // (:bigdec options)
            }
            case '"' -> {
                return readQuotedString(stream);
            }
            case 'n' -> {
                if (stream.read() == 'u' && stream.read() == 'l' && stream.read() == 'l') {
                    return Json.Null.instance();
                }
                else {
                    throw new JsonReadException("JSON error (expected null)");
                }
            }
            case 't' -> {
                if (stream.read() == 'r' && stream.read() == 'u' && stream.read() == 'e') {
                    return Json.Boolean.of(true);
                }
                else {
                    throw new JsonReadException("JSON error (expected true)");
                }
            }
            case 'f' -> {
                if (stream.read() == 'a' && stream.read() == 'l' && stream.read() == 's' && stream.read() == 'e') {
                    return Json.Boolean.of(false);
                }
                else {
                    throw new JsonReadException("JSON error (expected false)");
                }
            }
            case '{' -> {
                return readObject(stream, options);
            }
            case '[' -> {
                return readArray(stream, options);
            }
            default -> {
                if (c < 0) {
                    if (eofError || !(options.eofBehavior() instanceof Json.EOFBehavior.DefaultValue eofDefaultValue)) {
                        throw new JsonReadException("JSON error (end-of-file)");
                    }
                    else {
                        return eofDefaultValue.json();
                    }
                }
                else {
                    throw new JsonReadException("JSON error (unexpected character): " + (char) c);
                }
            }
        }
    }
}
