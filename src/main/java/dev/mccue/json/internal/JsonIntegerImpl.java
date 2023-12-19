package dev.mccue.json.internal;

import dev.mccue.json.Json;
import dev.mccue.json.JsonInteger;
import dev.mccue.json.serialization.JsonSerializationProxy;

import java.io.Serial;
import java.math.BigInteger;

public final class JsonIntegerImpl extends JsonInteger {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String value;

    public JsonIntegerImpl(String value) {
        this.value = value;
    }

    @Override
    public java.math.BigDecimal bigDecimalValue() {
        return new java.math.BigDecimal(bigIntegerValue());
    }

    @Override
    public java.math.BigInteger bigIntegerValue() {
        return new BigInteger(value);
    }

    @Override
    public int intValueExact() {
        return bigIntegerValue().intValueExact();
    }

    @Override
    public long longValueExact() {
        return bigIntegerValue().longValueExact();
    }

    @Override
    public java.math.BigInteger bigIntegerValueExact() {
        return bigIntegerValue();
    }

    @Override
    public boolean isIntegral() {
        return true;
    }

    @Override
    public int intValue() {
        return Integer.parseInt(value);
    }

    @Override
    public long longValue() {
        return Long.parseLong(value);
    }

    @Override
    public float floatValue() {
        return Float.parseFloat(value);
    }

    @Override
    public double doubleValue() {
        return Double.parseDouble(value);
    }

    @Override
    public java.lang.String toString() {
        return this.value;
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
