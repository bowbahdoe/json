package dev.mccue.json.internal;

import dev.mccue.json.Json;
import dev.mccue.json.JsonNumber;
import dev.mccue.json.serialization.JsonSerializationProxy;

import java.io.Serial;
import java.math.BigInteger;
import java.util.Objects;

@ValueCandidate
public final class BigDecimalImpl extends JsonNumber {
    @Serial
    private static final long serialVersionUID = 1L;
    private final java.math.BigDecimal bigDecimalValue;

    public BigDecimalImpl(java.math.BigDecimal bigDecimalValue) {
        this.bigDecimalValue = Objects.requireNonNull(
                bigDecimalValue,
                "bigDecimalValue must not be null."
        );
    }

    @Override
    public int intValue() {
        return bigDecimalValue.intValue();
    }

    @Override
    public long longValue() {
        return bigDecimalValue.longValue();
    }

    @Override
    public float floatValue() {
        return bigDecimalValue.floatValue();
    }

    @Override
    public double doubleValue() {
        return bigDecimalValue.doubleValue();
    }

    @Override
    public java.math.BigDecimal bigDecimalValue() {
        return bigDecimalValue;
    }

    @Override
    public java.math.BigInteger bigIntegerValue() {
        return bigDecimalValue.toBigInteger();
    }

    @Override
    public int intValueExact() {
        return bigDecimalValue.intValueExact();
    }

    @Override
    public long longValueExact() {
        return bigDecimalValue.longValueExact();
    }

    @Override
    public BigInteger bigIntegerValueExact() {
        return bigDecimalValue.toBigIntegerExact();
    }

    @Override
    public boolean isIntegral() {
        return bigDecimalValue.scale() == 0;
    }

    @Override
    public boolean equals(Object o) {
        return (this == o) || (
                o instanceof BigDecimalImpl otherBigDecimal &&
                        this.bigDecimalValue.equals(otherBigDecimal.bigDecimalValue)
        );
    }

    @Override
    public int hashCode() {
        return this.bigDecimalValue.hashCode();
    }

    @Override
    public java.lang.String toString() {
        return this.bigDecimalValue.toString();
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

