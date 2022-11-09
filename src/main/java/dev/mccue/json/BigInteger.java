package dev.mccue.json;

import java.util.Objects;

final class BigInteger extends Json.Number {
    private final java.math.BigInteger bigIntegerValue;

    public BigInteger(java.math.BigInteger bigIntegerValue) {
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
    public boolean equals(java.lang.Object o) {
        return (this == o) || (
                o instanceof BigInteger otherBigInteger &&
                        this.bigIntegerValue.equals(otherBigInteger.bigIntegerValue)
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
}
