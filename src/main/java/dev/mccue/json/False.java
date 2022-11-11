package dev.mccue.json;

import java.io.Serial;

enum False implements Json.Boolean {
    INSTANCE;

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
}
