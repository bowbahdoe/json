package dev.mccue.json;

import java.util.Objects;
import java.util.stream.IntStream;

record String(@Override java.lang.String value) implements Json.String {
    String {
        Objects.requireNonNull(value, "Toml.String value must be nonnull");
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
}
