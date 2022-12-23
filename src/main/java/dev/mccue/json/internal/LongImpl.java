package dev.mccue.json.internal;

import dev.mccue.json.Json;
import dev.mccue.json.JsonNumber;
import dev.mccue.json.serialization.JsonSerializationProxy;

import java.io.Serial;
import java.math.BigInteger;

@ValueCandidate
public final class LongImpl extends JsonNumber {
    @Serial
    private static final long serialVersionUID = 1L;
    private final long longValue;

    public LongImpl(long longValue) {
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
    public int intValueExact() {
        if (longValue <= Integer.MAX_VALUE && longValue >= Integer.MIN_VALUE) {
            return (int) longValue;
        }
        else {
            throw new ArithmeticException(longValue + " cannot fit into an int.");
        }
    }

    @Override
    public long longValueExact() {
        return longValue;
    }

    @Override
    public BigInteger bigIntegerValueExact() {
        return BigInteger.valueOf(longValue);
    }

    @Override
    public boolean isIntegral() {
        return true;
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
    public boolean equals(Object o) {
        return (this == o) || (
                o instanceof LongImpl otherLong &&
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

    @Serial
    private Object writeReplace() {
        return new JsonSerializationProxy(Json.writeString(this));
    }

    @Serial
    private Object readResolve() {
        throw new IllegalStateException();
    }
}
