package dev.mccue.json.internal;

import dev.mccue.json.*;
import dev.mccue.json.serialization.JsonSerializationProxy;
import dev.mccue.json.stream.JsonGenerator;

import java.io.IOException;
import java.io.Serial;
import java.io.UncheckedIOException;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.IntStream;

@ValueCandidate
public final class StringImpl implements JsonString {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String value;

    public StringImpl(java.lang.String value) {
        this.value = Objects.requireNonNull(value, "Json.String value must be nonnull");

        Function<String, String> escape = s -> {
            var sb = new StringBuilder();
            try {
                JsonWriter.writeString(s, sb, new JsonWriter.OptionsWithIndentDepth(new JsonWriteOptions()));
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
            return sb.toString();
        };
    }

    @Override
    public int length() {
        return this.value.length();
    }

    @Override
    public char charAt(int index) {
        return this.value.charAt(index);
    }

    @Override
    public boolean isEmpty() {
        return this.value.isEmpty();
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return this.value.subSequence(start, end);
    }

    @Override
    public IntStream chars() {
        return this.value.chars();
    }

    @Override
    public IntStream codePoints() {
        return this.value.codePoints();
    }

    @Override
    public java.lang.String toString() {
        return value;
    }

    @Serial
    private Object writeReplace() {
        return new JsonSerializationProxy(Json.writeString(this));
    }

    @Serial
    private Object readResolve() {
        throw new IllegalStateException();
    }

    @Override
    public void write(JsonGenerator generator) {
        generator.writeString(this.toString());
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof StringImpl impl
                && this.value.equals(impl.value);
    }

    @Override
    public int hashCode() {
        return this.value.hashCode();
    }
}
