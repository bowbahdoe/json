package dev.mccue.json;

import java.io.Serial;

final class TrueImpl implements Json.True {
    static final TrueImpl INSTANCE = new TrueImpl();

    private TrueImpl() {}

    @Override
    public boolean value() {
        return true;
    }

    @Override
    public java.lang.String toString() {
        return "true";
    }

    @Serial
    private java.lang.Object writeReplace() {
        return new JsonSerializationProxy(Json.writeString(this));
    }

    @Serial
    private java.lang.Object readResolve() {
        throw new IllegalStateException();
    }

    @Override
    public void write(JsonGenerator generator) {
        generator.emitTrue();
    }
}
