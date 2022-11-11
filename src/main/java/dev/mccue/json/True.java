package dev.mccue.json;

import java.io.Serial;

enum True implements Json.Boolean {
    INSTANCE;

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
}
