package dev.mccue.json.internal;

import dev.mccue.json.stream.JsonGenerator;
import dev.mccue.json.JsonNumber;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.Consumer;

abstract class AbstractJsonGenerator implements JsonGenerator {
    protected final Consumer<JsonGenerator> advance;
    protected final JsonWriter.OptionsWithIndentDepth options;
    protected final Appendable out;

    AbstractJsonGenerator(
            Consumer<JsonGenerator> advance,
            JsonWriter.OptionsWithIndentDepth options,
            Appendable out
    ) {
        this.advance = advance;
        this.options = options;
        this.out = out;
    }

    @Override
    public void writeString(String value) {
        try {
            JsonWriter.writeString(value, out, options);
            this.advance.accept(this);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void writeNumber(JsonNumber value) {
        try {
            out.append(value.toString());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void writeTrue() {
        try {
            out.append("true");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void writeFalse() {
        try {
            out.append("false");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void writeNull() {
        try {
            out.append("null");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
