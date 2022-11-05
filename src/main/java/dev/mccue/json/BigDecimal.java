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
    java.math.BigDecimal bigDecimalValue() {
        return bigDecimalValue;
    }

    @Override
    java.math.BigInteger bigIntegerValue() {
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

final class Double extends Json.Number {
    private final double doubleValue;

    Double(double doubleValue) {
        this.doubleValue = doubleValue;
    }

    @Override
    public java.math.BigDecimal bigDecimalValue() {
        return java.math.BigDecimal.valueOf(doubleValue);
    }

    @Override
    java.math.BigInteger bigIntegerValue() {
        return java.math.BigInteger.valueOf((long) doubleValue);
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
    public boolean equals(java.lang.Object o) {
        return (this == o) || (
                o instanceof Double otherDouble &&
                this.doubleValue == otherDouble.doubleValue
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
}

final class Long extends Json.Number {
    private final long longValue;

    Long(long longValue) {
        this.longValue = longValue;
    }

    @Override
    public java.math.BigDecimal bigDecimalValue() {
        return java.math.BigDecimal.valueOf(longValue);
    }

    @Override
    java.math.BigInteger bigIntegerValue() {
        return java.math.BigInteger.valueOf(longValue);
    }



    @Override
    public int intValue() {
        return (int) longValue;
    }

    @Override
    public long longValue() {
        return longValue;
    }

    @Override
    public float floatValue() {
        return (float) longValue;
    }

    @Override
    public double doubleValue() {
        return (double) longValue;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        return (this == o) || (
                o instanceof Long otherLong &&
                        this.longValue == otherLong.longValue
        );
    }

    @Override
    public int hashCode() {
        return java.lang.Long.hashCode(this.longValue);
    }

    @Override
    public java.lang.String toString() {
        return java.lang.Long.toString(longValue);
    }
}

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
    java.math.BigInteger bigIntegerValue() {
        return bigIntegerValue;
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
