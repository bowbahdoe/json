package dev.mccue.json;

import java.io.ObjectInputStream;
import java.io.Serial;

enum Null implements Json.Null {
    INSTANCE;

    @Override
    public java.lang.String toString() {
        return "null";
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
