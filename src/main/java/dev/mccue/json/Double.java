package dev.mccue.json;

import java.io.Serial;
import java.math.BigDecimal;
import java.math.BigInteger;

final class Double extends Json.Number {
    private final double doubleValue;

    Double(double doubleValue) {
        if (java.lang.Double.isInfinite(doubleValue)) {
            throw new IllegalArgumentException("JSON cannot encode an infinite double");
        }
        if (java.lang.Double.isNaN(doubleValue)) {
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
        }
        else {
            throw new ArithmeticException(doubleValue + " cannot fit into an int");
        }
    }

    @Override
    public long longValueExact() {
        if (((long) doubleValue) == doubleValue) {
            return (int) doubleValue;
        }
        else {
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

    @Serial
    private java.lang.Object writeReplace() {
        return new JsonSerializationProxy(Json.writeString(this));
    }

    @Serial
    private java.lang.Object readResolve() {
        throw new IllegalStateException();
    }
}
