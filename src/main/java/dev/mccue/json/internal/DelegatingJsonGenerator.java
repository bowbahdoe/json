package dev.mccue.json.internal;

import dev.mccue.json.Json;
import dev.mccue.json.stream.JsonGenerator;
import dev.mccue.json.JsonNumber;

class DelegatingJsonGenerator implements JsonGenerator {
    private JsonGenerator delegate;

    DelegatingJsonGenerator(
            Json.WriteOptions writeOptions,
            Appendable out,
            JsonGenerator delegate
    ) {
        this.delegate = delegate;
    }

    public void setDelegate(JsonGenerator delegate) {
        this.delegate = delegate;
    }

    @Override
    public void writeObjectStart() {
        this.delegate.writeObjectStart();
    }

    @Override
    public void writeObjectEnd() {
        this.delegate.writeObjectEnd();
    }

    @Override
    public void writeArrayStart() {
        this.delegate.writeArrayStart();
    }

    @Override
    public void writeArrayEnd() {
        this.delegate.writeArrayEnd();
    }

    @Override
    public void writeFieldName(String value) {
        this.delegate.writeFieldName(value);
    }

    @Override
    public void writeString(String value) {
        this.delegate.writeString(value);
    }

    @Override
    public void writeNumber(JsonNumber value) {
        this.delegate.writeNumber(value);
    }

    @Override
    public void writeTrue() {
        this.delegate.writeTrue();
    }

    @Override
    public void writeFalse() {
        this.delegate.writeFalse();
    }

    @Override
    public void writeNull() {
        this.delegate.writeNull();
    }
}
