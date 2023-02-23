package dev.mccue.json;

import dev.mccue.json.internal.BigDecimalImpl;
import dev.mccue.json.internal.BigIntegerImpl;
import dev.mccue.json.internal.DoubleImpl;
import dev.mccue.json.internal.LongImpl;
import dev.mccue.json.stream.JsonGenerator;

import java.io.Serial;
import java.math.BigDecimal;

/**
 * Represents a number in the json data model.
 */
public sealed abstract class JsonNumber
        extends Number
        implements Json
        permits BigDecimalImpl, DoubleImpl, LongImpl, BigIntegerImpl {
    @Serial
    private static final long serialVersionUID = 1L;

    protected JsonNumber() {}

    public abstract BigDecimal bigDecimalValue();

    public abstract java.math.BigInteger bigIntegerValue();

    public abstract int intValueExact();

    public abstract long longValueExact();

    public abstract java.math.BigInteger bigIntegerValueExact();

    public abstract boolean isIntegral();

    public static JsonNumber of(BigDecimal value) {
        return new BigDecimalImpl(value);
    }

    public static JsonNumber of(double value) {
        return new DoubleImpl(value);
    }

    public static JsonNumber of(long value) {
        return new LongImpl(value);
    }

    public static JsonNumber of(float value) {
        return new DoubleImpl(value);
    }

    public static JsonNumber of(int value) {
        return new LongImpl(value);
    }

    public static JsonNumber of(java.math.BigInteger value) {
        return new BigIntegerImpl(value);
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
