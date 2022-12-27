package dev.mccue.json.internal;


import dev.mccue.json.Json;
import dev.mccue.json.stream.JsonArrayHandler;
import dev.mccue.json.stream.JsonEvent;
import dev.mccue.json.JsonNumber;
import dev.mccue.json.JsonReadException;
import dev.mccue.json.stream.JsonValueHandler;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.NoSuchElementException;

public final class JsonReaderMethods {
    private JsonReaderMethods() {}

    public static final int MINIMUM_PUSHBACK_BUFFER_SIZE = 64;

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

        return (char) Integer.parseInt(s, 16);
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

    private static String slowReadString(PushbackReader stream, String alreadyRead) throws IOException {
        var buffer = new StringBuilder(alreadyRead);
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

    private static String readQuotedString(PushbackReader stream) throws IOException {
        // Expects to be called with the head of the stream AFTER the
        // opening quotation mark.
        var buffer = new char[MINIMUM_PUSHBACK_BUFFER_SIZE];
        int read = stream.read(buffer, 0, MINIMUM_PUSHBACK_BUFFER_SIZE);
        int endIndex = read - 1;
        if (read < 0) {
            throw JsonReadException.unexpectedEOFInsideString();
        }

        int i = 0;
        while (true) {
            var c = buffer[i];
            switch (c) {
                case '"': {
                    var off = i + 1;
                    var len = read - off;
                    stream.unread(buffer, off, len);
                    return new String(buffer, 0, i);
                }
                case '\\': {
                    var off = i;
                    var len = read - off;
                    stream.unread(buffer, off, len);
                    return slowReadString(stream, new String(buffer, 0, i));
                }
                default:
                    if (i == endIndex) {
                        stream.unread(c);
                        return slowReadString(stream, new String(buffer, 0, i));
                    }
                    else {
                        i++;
                    }
            }
        }
    }

    private static JsonNumber readInteger(String string) {
        if (string.length() < 18) { // definitely fits in a Long
            return JsonNumber.of(Long.parseLong(string));
        }
        else {
            try {
                return JsonNumber.of(Long.parseLong(string));
            } catch (NumberFormatException __) {
                return JsonNumber.of(new BigInteger(string));
            }
        }
    }

    private static JsonNumber readDecimal(String string, boolean bigDecimal) {
        if (bigDecimal) {
            return new BigDecimalImpl(new BigDecimal(string));
        }
        else {
            return new DoubleImpl(Double.parseDouble(string));
        }
    }

    private static JsonNumber readNumber(PushbackReader stream, boolean bigDecimal) throws IOException {
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
                        case 9, 10, 13, 32, ',', ']', '}', -1 -> {
                            stream.unread(c);
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
                        case 9, 10, 13, 32, ',', ']', '}', -1 -> {
                            stream.unread(c);
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
                        case 9, 10, 13, 32, ',', ']', '}', -1 -> {
                            stream.unread(c);
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
                        case 9, 10, 13, 32, ',', ']', '}', -1 -> {
                            stream.unread(c);
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
            return readDecimal(buffer.toString(), bigDecimal);
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

    private static /* @Nullable */ String readKey(PushbackReader stream) throws IOException {
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
                    throw JsonReadException.invalidArray();
            }
        }
    }

    private static void readArrayStream(
            PushbackReader stream,
            Json.StreamReadOptions options,
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
                        throw JsonReadException.missingEntryInObject();
                }
            }
            else {
                if (readSomeEntry) {
                    throw JsonReadException.emptyEntryInObject();
                }
                else {
                    objectHandler.objectEnd();
                    return;
                }
            }
        }
    }

    public static void readStream(
            PushbackReader stream,
            boolean throwIfEofEncountered,
            Json.StreamReadOptions options,
            JsonValueHandler valueHandler
    ) throws IOException {
        int c = nextToken(stream);
        switch (c) {
            case '-', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> {
                stream.unread(c);
                valueHandler.onNumber(readNumber(stream, options.useBigDecimals()));
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

    public static Json read(PushbackReader stream, Json.ReadOptions options) throws IOException {
        var handler = new Handlers.BaseTreeValueHandler();
        readStream(stream, false, new Json.StreamReadOptions(options.useBigDecimals()), handler);
        if (handler.result == null && options.eofBehavior() == Json.EOFBehavior.THROW_EXCEPTION) {
            throw JsonReadException.unexpectedEOF();
        }
        else {
            return handler.result;
        }
    }





    static final class EventIterator implements Iterator<JsonEvent> {
        final PushbackReader stream;
        Json.StreamReadOptions options;
        State state;
        JsonEvent next;

        sealed interface State {
            void advance(EventIterator self) throws IOException;
        }

        record Terminated() implements State {
            @Override
            public void advance(EventIterator self) throws IOException {
                self.next = null;
            }
        }

        record ReadObjectValue(State previous) implements State {
            @Override
            public void advance(EventIterator self) throws IOException {

            }
        }

        record ReadObject(boolean readSomeEntry, State previous) implements State {
            ReadObject(State previous) {
                this(false, previous);
            }

            @Override
            public void advance(EventIterator self) throws IOException {
                var key = readKey(self.stream);
                if (key != null) {
                    self.next = new JsonEvent.Field(key);
                }
                else {
                    if (readSomeEntry) {
                        throw JsonReadException.emptyEntryInObject();
                    }
                    else {
                        self.state = previous;
                    }
                }
            }
        }



        record ReadArrayHelper(
                State previous
        ) implements State {

            @Override
            public void advance(EventIterator self) throws IOException {
                char c = (char) nextToken(self.stream);
                switch (c) {
                    case ']' -> {
                        self.next = new JsonEvent.ArrayEnd();
                        self.state = previous;
                    }
                    case ',' -> {
                        self.state = new Root(true, this);
                        self.state.advance(self);
                    }
                    default -> {
                        System.out.println("" + c);
                        throw JsonReadException.invalidArray();
                    }
                }
            }
        }
        record ReadArray(
                State previous
        ) implements State {

            @Override
            public void advance(EventIterator self) throws IOException {
                var c = nextToken(self.stream);
                switch (c) {
                    case ']' -> {
                        self.next = new JsonEvent.ArrayEnd();
                        self.state = previous;
                    }
                    case ',' ->
                            throw JsonReadException.invalidArray();
                    default -> {
                        self.stream.unread(c);
                        self.state = new Root(true, new ReadArrayHelper(previous));
                        self.state.advance(self);
                    }
                }
            }
        }
        @ValueCandidate
        record Root(
                boolean throwIfEofEncountered,
                State previous
        ) implements State {
            Root() {
                this(false, new Terminated());
            }
            @Override
            public void advance(EventIterator self) throws IOException {
                int c = nextToken(self.stream);
                switch (c) {
                    case '-', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> {
                        self.stream.unread(c);
                        self.next = new JsonEvent.Number(
                                readNumber(self.stream, self.options.useBigDecimals())
                        );
                        self.state = previous;
                    }
                    case '"' -> {
                        self.next = new JsonEvent.String(
                                readQuotedString(self.stream)
                        );
                        self.state = previous;
                    }
                    case 'n' -> {
                        if (readUll(self.stream)) {
                            self.next = new JsonEvent.Null();
                            self.state = previous;
                        }
                        else {
                            throw JsonReadException.expectedNull();
                        }
                    }
                    case 't' -> {
                        if (readRue(self.stream)) {
                            self.next = new JsonEvent.True();
                            self.state = previous;
                        }
                        else {
                            throw JsonReadException.expectedTrue();
                        }
                    }
                    case 'f' -> {
                        if (readAlse(self.stream)) {
                            self.next = new JsonEvent.False();
                            self.state = previous;
                        }
                        else {
                            throw JsonReadException.expectedFalse();
                        }
                    }
                    case '{' -> {
                        throw new IllegalStateException("unhandled");
                    }
                    case '[' -> {
                        self.next = new JsonEvent.ArrayStart();
                        self.state = new ReadArray(this.previous);
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
        }

        EventIterator(PushbackReader stream, Json.StreamReadOptions options) {
            this.stream = stream;
            this.options = options;
            this.state = new Root(false, new Terminated());
        }

        @Override
        public boolean hasNext() {
            try {
                if (state instanceof Root) {
                    state.advance(this);
                }
                return this.next != null;
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        @Override
        public JsonEvent next() {
            try {
                if (state instanceof Root) {
                    state.advance(this);
                }
                if (state instanceof Terminated && this.next == null) {
                    throw new NoSuchElementException();
                }
                else {
                    var next = this.next;
                    state.advance(this);
                    return next;
                }
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        public static void main(String[] args) throws IOException {
            var iter = new EventIterator(
                    new PushbackReader(
                            new StringReader("[1, 2, [], [\"a\", \"b\", true], 3]"),
                            MINIMUM_PUSHBACK_BUFFER_SIZE
                    ),
                    new Json.StreamReadOptions()
            );

            while (iter.hasNext()) {
                System.out.println(iter.next());
            }
        }
    }
}
