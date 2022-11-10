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
}
