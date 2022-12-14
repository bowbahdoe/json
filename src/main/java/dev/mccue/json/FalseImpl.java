package dev.mccue.json;

import java.io.Serial;

final class FalseImpl implements Json.False {
    static final FalseImpl INSTANCE = new FalseImpl();

    @Override
    public boolean value() {
        return false;
    }

    @Override
    public java.lang.String toString() {
        return "false";
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
        generator.emitFalse();
    }
}
