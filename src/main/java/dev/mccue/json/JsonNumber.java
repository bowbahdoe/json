package dev.mccue.json;

import dev.mccue.json.internal.JsonDecimalImpl;
import dev.mccue.json.internal.JsonIntegerImpl;
import dev.mccue.json.stream.JsonGenerator;

import java.io.Serial;
import java.math.BigDecimal;

/**
 * Represents a number in the json data model.
 *
 * @author <a href="ethan@mccue.dev">Ethan McCue</a>
 */
public sealed abstract class JsonNumber
        extends Number
        implements Json permits JsonDecimal, JsonInteger {
    @Serial
    private static final long serialVersionUID = 1L;

    protected JsonNumber() {}

    public abstract BigDecimal bigDecimalValue();

    public abstract java.math.BigInteger bigIntegerValue();

    public abstract int intValueExact();

    public abstract long longValueExact();

    public abstract java.math.BigInteger bigIntegerValueExact();

    public abstract boolean isIntegral();

    public static JsonDecimal of(BigDecimal value) {
        return new JsonDecimalImpl(value.toString());
    }

    public static JsonDecimal of(double value) {
        if (Double.isInfinite(value)) {
            throw new IllegalArgumentException("JSON cannot encode an infinite value");
        }
        if (Double.isNaN(value)) {
            throw new IllegalArgumentException("JSON cannot encode a NaN");
        }
        return new JsonDecimalImpl(BigDecimal.valueOf(value).toString());
    }

    public static JsonInteger of(long value) {
        return new JsonIntegerImpl(BigDecimal.valueOf(value).toString());
    }

    public static JsonDecimal of(float value) {
        if (Float.isInfinite(value)) {
            throw new IllegalArgumentException("JSON cannot encode an infinite value");
        }
        if (Float.isNaN(value)) {
            throw new IllegalArgumentException("JSON cannot encode a NaN");
        }
        return new JsonDecimalImpl(BigDecimal.valueOf(value).toString());
    }

    public static JsonInteger of(int value) {
        return new JsonIntegerImpl(BigDecimal.valueOf(value).toString());
    }

    public static JsonInteger of(java.math.BigInteger value) {
        return new JsonIntegerImpl(new BigDecimal(value).toString());
    }

    @Override
    public void write(JsonGenerator generator) {
        generator.writeNumber(this);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof JsonNumber otherNumber &&
                this.bigDecimalValue().equals(otherNumber.bigDecimalValue());
    }

    @Override
    public int hashCode() {
        return this.bigDecimalValue().hashCode();
    }
}
