package dev.mccue.json;

import java.util.Objects;

record BigDecimal(@Override java.math.BigDecimal bigDecimalValue) implements Json.Number {
    BigDecimal {
        Objects.requireNonNull(bigDecimalValue, "bigDecimalValue must not be null.");
    }

    @Override
    public long longValue() {
        return bigDecimalValue.longValue();
    }

    @Override
    public long longValueExact() {
        return bigDecimalValue.longValueExact();
    }
}

record Double(double doubleValue) implements Json.Number {
    @Override
    public java.math.BigDecimal bigDecimalValue() {
        return java.math.BigDecimal.valueOf(doubleValue);
    }

    @Override
    public long longValue() {
        return java.lang.Double.valueOf(doubleValue).longValue();
    }

    @Override
    public long longValueExact() {
        return java.math.BigDecimal.valueOf(doubleValue).longValueExact();
    }
}

record Long(long longValue) implements Json.Number {

    @Override
    public java.math.BigDecimal bigDecimalValue() {
        return java.math.BigDecimal.valueOf(longValue);
    }

    @Override
    public long longValueExact() {
        return longValue;
    }
}

record BigInteger(java.math.BigInteger bigIntegerValue) implements Json.Number {

    public BigInteger {
        Objects.requireNonNull(bigIntegerValue, "value must not be null");
    }

    @Override
    public java.math.BigDecimal bigDecimalValue() {
        return new java.math.BigDecimal(bigIntegerValue);
    }

    @Override
    public long longValue() {
        return bigIntegerValue.longValue();
    }

    @Override
    public long longValueExact() {
        return bigIntegerValue.longValueExact();
    }
}
