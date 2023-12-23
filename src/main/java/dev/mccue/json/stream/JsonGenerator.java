package dev.mccue.json.stream;

import dev.mccue.json.JsonNumber;

import java.math.BigDecimal;
import java.math.BigInteger;

public interface JsonGenerator {
    void writeObjectStart();

    void writeObjectEnd();

    void writeArrayStart();

    void writeArrayEnd();

    void writeFieldName(String value);

    void writeString(String value);

    void writeNumber(JsonNumber value);

    void writeTrue();

    void writeFalse();

    void writeNull();

    default void write(JsonEvent event) {
        switch (event) {
            case JsonEvent.ObjectStart __ -> this.writeObjectStart();
            case JsonEvent.ObjectEnd __ -> this.writeObjectEnd();
            case JsonEvent.ArrayStart __ -> this.writeArrayStart();
            case JsonEvent.ArrayEnd __ -> this.writeArrayEnd();
            case JsonEvent.Field field -> this.writeFieldName(field.name());
            case JsonEvent.String string -> this.writeString(string.value());
            case JsonEvent.Number number -> this.writeNumber(number.value());
            case JsonEvent.True __ -> this.writeTrue();
            case JsonEvent.False __ -> this.writeFalse();
            case JsonEvent.Null __ -> this.writeNull();
        }
    }

    default void writeNumber(float value) {
        writeNumber(JsonNumber.of(value));
    }

    default void writeNumber(double value) {
        writeNumber(JsonNumber.of(value));
    }

    default void writeNumber(int value) {
        writeNumber(JsonNumber.of(value));
    }

    default void writeNumber(long value) {
        writeNumber(JsonNumber.of(value));
    }

    default void writeNumber(BigInteger value) {
        writeNumber(JsonNumber.of(value));
    }

    default void writeNumber(BigDecimal value) {
        writeNumber(JsonNumber.of(value));
    }

    default void writeNumberField(String fieldName, JsonNumber number) {
        this.writeFieldName(fieldName);
        this.writeNumber(number);
    }

    default void writeNumberField(String fieldName, float number) {
        this.writeFieldName(fieldName);
        this.writeNumber(number);
    }

    default void writeNumberField(String fieldName, double number) {
        this.writeFieldName(fieldName);
        this.writeNumber(number);
    }

    default void writeNumberField(String fieldName, int number) {
        this.writeFieldName(fieldName);
        this.writeNumber(number);
    }

    default void writeNumberField(String fieldName, long number) {
        this.writeFieldName(fieldName);
        this.writeNumber(number);
    }

    default void writeNumberField(String fieldName, BigInteger number) {
        this.writeFieldName(fieldName);
        this.writeNumber(number);
    }

    default void writeNumberField(String fieldName, BigDecimal number) {
        this.writeFieldName(fieldName);
        this.writeNumber(number);
    }

    default void writeStringField(String fieldName, String value) {
        this.writeFieldName(fieldName);
        this.writeString(value);
    }

    default void writeNullField(String fieldName) {
        this.writeFieldName(fieldName);
        this.writeNull();
    }
}
