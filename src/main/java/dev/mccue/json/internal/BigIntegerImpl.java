package dev.mccue.json.internal;

import dev.mccue.json.Json;
import dev.mccue.json.JsonNumber;
import dev.mccue.json.serialization.JsonSerializationProxy;

import java.io.Serial;
import java.util.Objects;

@ValueCandidate
public final class BigIntegerImpl extends JsonNumber {
    @Serial
    private static final long serialVersionUID = 1L;
    private final java.math.BigInteger bigIntegerValue;

    public BigIntegerImpl(java.math.BigInteger bigIntegerValue) {
        this.bigIntegerValue = Objects.requireNonNull(bigIntegerValue, "value must not be null");
    }

    @Override
    public java.math.BigDecimal bigDecimalValue() {
        return new java.math.BigDecimal(bigIntegerValue);
    }

    @Override
    public java.math.BigInteger bigIntegerValue() {
        return bigIntegerValue;
    }

    @Override
    public int intValueExact() {
        return bigIntegerValue().intValueExact();
    }

    @Override
    public long longValueExact() {
        return bigIntegerValue.longValueExact();
    }

    @Override
    public java.math.BigInteger bigIntegerValueExact() {
        return bigIntegerValue;
    }

    @Override
    public boolean isIntegral() {
        return true;
    }

    @Override
    public int intValue() {
        return bigIntegerValue.intValue();
    }

    @Override
    public long longValue() {
        return bigIntegerValue.longValue();
    }

    @Override
    public float floatValue() {
        return bigIntegerValue.floatValue();
    }

    @Override
    public double doubleValue() {
        return bigIntegerValue.doubleValue();
    }

    @Override
    public boolean equals(Object o) {
        return (this == o) || (
                o instanceof JsonNumber otherNumber &&
                        this.bigIntegerValue.equals(otherNumber.bigIntegerValue())
        );
    }

    @Override
    public int hashCode() {
        return this.bigIntegerValue.hashCode();
    }

    @Override
    public java.lang.String toString() {
        return this.bigIntegerValue.toString();
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
