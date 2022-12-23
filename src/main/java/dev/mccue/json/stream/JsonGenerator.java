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
        if (event instanceof JsonEvent.ObjectStart) {
            this.writeObjectStart();
        }
        else if (event instanceof JsonEvent.ObjectEnd) {
            this.writeObjectEnd();
        }
        else if (event instanceof JsonEvent.ArrayStart) {
            this.writeArrayStart();
        }
        else if (event instanceof JsonEvent.ArrayEnd) {
            this.writeArrayEnd();
        }
        else if (event instanceof JsonEvent.Field field) {
            this.writeFieldName(field.name());
        }
        else if (event instanceof JsonEvent.String) {
            this.writeNull();
        }
        else if (event instanceof JsonEvent.Number) {
            this.writeNull();
        }
        else if (event instanceof JsonEvent.True) {
            this.writeTrue();
        }
        else if (event instanceof JsonEvent.False) {
            this.writeFalse();
        }
        else if (event instanceof JsonEvent.Null) {
            this.writeNull();
        }
        else {
            throw new IllegalStateException();
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
