package dev.mccue.json;

import java.io.Serial;

final class NullImpl implements Json.Null {
    @Serial
    private static final long serialVersionUID = 1L;
    static final NullImpl INSTANCE = new NullImpl();

    private NullImpl() {}

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

    @Override
    public void write(JsonGenerator generator) {
        generator.emitNull();
    }
}
