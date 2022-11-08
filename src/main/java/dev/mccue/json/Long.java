package dev.mccue.json;

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
    public java.math.BigInteger bigIntegerValue() {
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
