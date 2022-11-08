package dev.mccue.json;

import java.util.Objects;

final class BigDecimal extends Json.Number {
    private final java.math.BigDecimal bigDecimalValue;

    BigDecimal(java.math.BigDecimal bigDecimalValue) {
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
    public boolean equals(java.lang.Object o) {
        return (this == o) || (
                o instanceof BigDecimal otherBigDecimal &&
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
}

