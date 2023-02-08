package dev.mccue.json.internal;

import dev.mccue.json.Json;
import dev.mccue.json.JsonNumber;
import dev.mccue.json.serialization.JsonSerializationProxy;

import java.io.Serial;
import java.math.BigDecimal;
import java.math.BigInteger;

@ValueCandidate
public final class DoubleImpl extends JsonNumber {
    @Serial
    private static final long serialVersionUID = 1L;

    private final double doubleValue;

    public DoubleImpl(double doubleValue) {
        if (Double.isInfinite(doubleValue)) {
            throw new IllegalArgumentException("JSON cannot encode an infinite double");
        }
        if (Double.isNaN(doubleValue)) {
            throw new IllegalArgumentException("JSON cannot encode a NaN double");
        }

        this.doubleValue = doubleValue;
    }

    @Override
    public java.math.BigDecimal bigDecimalValue() {
        return java.math.BigDecimal.valueOf(doubleValue);
    }

    @Override
    public java.math.BigInteger bigIntegerValue() {
        return java.math.BigInteger.valueOf((long) doubleValue);
    }

    @Override
    public int intValueExact() {
        if (((int) doubleValue) == doubleValue) {
            return (int) doubleValue;
        } else {
            throw new ArithmeticException(doubleValue + " cannot fit into an int");
        }
    }

    @Override
    public long longValueExact() {
        if (((long) doubleValue) == doubleValue) {
            return (int) doubleValue;
        } else {
            throw new ArithmeticException(doubleValue + " cannot fit into an int");
        }
    }

    @Override
    public BigInteger bigIntegerValueExact() {
        return BigDecimal.valueOf(doubleValue).toBigIntegerExact();
    }

    @Override
    public boolean isIntegral() {
        return (((int) doubleValue) == doubleValue);
    }

    @Override
    public int intValue() {
        return (int) doubleValue;
    }

    @Override
    public long longValue() {
        return (long) doubleValue;
    }

    @Override
    public float floatValue() {
        return (float) doubleValue;
    }

    @Override
    public double doubleValue() {
        return doubleValue;
    }

    @Override
    public boolean equals(Object o) {
        return (this == o) || (
                o instanceof JsonNumber otherNumber && otherNumber.isIntegral()
                        && (otherNumber instanceof LongImpl || otherNumber instanceof DoubleImpl ?
                        this.doubleValue == otherNumber.doubleValue() : otherNumber.equals(this))
        );
    }

    @Override
    public int hashCode() {
        return java.lang.Double.hashCode(this.doubleValue);
    }

    @Override
    public java.lang.String toString() {
        return java.lang.Double.toString(this.doubleValue);
    }

    @Serial
    private Object writeReplace() {
        return new JsonSerializationProxy(Json.writeString(this));
    }

    @Serial
    private Object readResolve() {
        throw new IllegalStateException();
    }
}
