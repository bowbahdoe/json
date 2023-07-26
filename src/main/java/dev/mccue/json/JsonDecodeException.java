package dev.mccue.json;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 *    An exception to be thrown when failing to "decode" some JSON into
 *    another shape.
 * </p>
 *
 * @author <a href="ethan@mccue.dev">Ethan McCue</a>
 */
public sealed abstract class JsonDecodeException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    JsonDecodeException() {
        super();
    }

    JsonDecodeException(String message) {
        super(message);
    }

    JsonDecodeException(Throwable cause) {
        super(cause);
    }

    public static AtField atField(String fieldName, JsonDecodeException error) {
        return new AtField(fieldName, error);
    }

    public static AtIndex atIndex(int index, JsonDecodeException error) {
        return new AtIndex(index, error);
    }

    public static OneOf oneOf(List<JsonDecodeException> errors) {
        return new OneOf(errors);
    }

    public static Failure of(String message, Json value) {
        return new Failure(message, value);
    }

    public static Failure of(Throwable cause, Json value) {
        return new Failure(cause, value);
    }

    public static final class AtField extends JsonDecodeException {
        @Serial
        private static final long serialVersionUID = 1L;
        private final String fieldName;
        private final JsonDecodeException error;

        private AtField(String fieldName, JsonDecodeException error) {
            Objects.requireNonNull(fieldName, "fieldName must not be null");
            Objects.requireNonNull(error, "error must not be null");
            this.fieldName = fieldName;
            this.error = error;
        }

        public String fieldName() {
            return this.fieldName;
        }

        public JsonDecodeException error() { return this.error; }

        @Override
        public String getMessage() {
            return getMessage(this);
        }
    }

    public static final class AtIndex extends JsonDecodeException {
        @Serial
        private static final long serialVersionUID = 1L;
        private final int index;
        private final JsonDecodeException error;

        private AtIndex(int index, JsonDecodeException error) {
            Objects.requireNonNull(error);
            this.index = index;
            this.error = error;
        }

        public int index() {
            return this.index;
        }

        public JsonDecodeException error() {
            return this.error;
        }

        @Override
        public String getMessage() {
            return getMessage(this);
        }
    }

    public static final class OneOf extends JsonDecodeException {
        @Serial
        private static final long serialVersionUID = 1L;

        @SuppressWarnings("serial")
        private final List<JsonDecodeException> errors;

        private OneOf(List<JsonDecodeException> errors) {
            if (errors.size() == 0) {
                throw new IllegalArgumentException("errors must be non-empty");
            }
            Objects.requireNonNull(errors, "errors of errors must not be null");
            errors.forEach(error -> Objects.requireNonNull(error, "every error must not be null"));
            this.errors = List.copyOf(errors);
        }

        public List<JsonDecodeException> errors() {
            return errors;
        }

        @Override
        public String getMessage() {
            return getMessage(this);
        }
    }

    public static final class Failure extends JsonDecodeException {
        @Serial
        private static final long serialVersionUID = 1L;
        private final Json value;

        private Failure(String reason, Json value) {
            super(reason);
            this.value = value;
        }

        private Failure(Throwable cause, Json value) {
            super(cause);
            this.value = value;
        }

        public Json value() {
            return value;
        }

        @Override
        public String getMessage() {
            return super.getMessage();
        }
    }

    private static String indent(String string) {
        return String.join("\n    ", string.split("\n"));
    }

    private static String getMessageHelp(JsonDecodeException error, ArrayList<String> context) {
        if (error instanceof AtField atField) {
            var fieldName = atField.fieldName();
            var err = atField.error();

            boolean isSimple;
            if (fieldName.isEmpty()) {
                isSimple = false;
            }
            else {
                isSimple = Character.isAlphabetic(fieldName.charAt(0));
                for (int i = 1; i < fieldName.length(); i++) {
                    isSimple = isSimple && (Character.isAlphabetic(fieldName.charAt(i)) || Character.isDigit(fieldName.charAt(i)));
                }
            }

            fieldName = isSimple ? "." + fieldName : "[" + fieldName + "]";

            context.add(fieldName);

            return getMessageHelp(err, context);
        }
        else if (error instanceof AtIndex atIndex) {
            var indexName = "[" + atIndex.index() + "]";
            context.add(indexName);
            return getMessageHelp(atIndex.error(), context);
        }
        else if (error instanceof OneOf oneOf) {
            if (oneOf.errors().isEmpty()) {
                return "Ran into oneOf with no possibilities" + (context.isEmpty() ? "!" : " at json" + String.join("", context));
            }
            else if (oneOf.errors().size() == 1) {
                return getMessageHelp(oneOf.errors().get(0), context);
            }
            else {
                var starter = (context.isEmpty() ? "oneOf" : "oneOf at json" + String.join("", context));
                var introduction = starter + " failed in the following " + oneOf.errors().size() + " ways:";
                var msg = new StringBuilder(introduction + "\n\n");
                for (int i = 0; i < oneOf.errors().size(); i++) {
                    msg.append("\n\n(");
                    msg.append(i + 1);
                    msg.append(") ");
                    msg.append(indent(getMessage(oneOf.errors().get(i))));
                    if (i != oneOf.errors().size() - 1) {
                        msg.append("\n\n");
                    }
                }

                return msg.toString();
            }
        }
        else if (error instanceof Failure failure) {
            var msg = failure.getMessage();
            var json = failure.value;

            var introduction  = (
                    context.isEmpty()
                            ? "Problem with the given value:\n\n    "
                            :  "Problem with the value at json" + String.join("", context) +  ":\n\n    "
            );

            return introduction + indent(Json.writeString(json, new JsonWriteOptions().withIndentation(4))) + "\n\n" + msg;
        }
        else {
            throw new IllegalStateException();
        }
    }

    protected static String getMessage(JsonDecodeException error) {
        return getMessageHelp(error, new ArrayList<>());
    }
}
