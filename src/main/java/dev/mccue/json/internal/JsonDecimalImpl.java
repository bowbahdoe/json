package dev.mccue.json.internal;

import dev.mccue.json.Json;
import dev.mccue.json.JsonDecimal;
import dev.mccue.json.serialization.JsonSerializationProxy;

import java.io.Serial;
import java.math.BigDecimal;
import java.math.BigInteger;

public final class JsonDecimalImpl extends JsonDecimal {
    private final String value;

    public JsonDecimalImpl(String value) {
        this.value = value;
    }

    @Override
    public BigDecimal bigDecimalValue() {
        return new BigDecimal(value);
    }

    @Override
    public int intValue() {
        return bigDecimalValue().intValue();
    }

    @Override
    public long longValue() {
        return bigDecimalValue().longValue();
    }

    @Override
    public float floatValue() {
        return bigDecimalValue().floatValue();
    }

    @Override
    public double doubleValue() {
        return bigDecimalValue().doubleValue();
    }

    @Override
    public java.math.BigInteger bigIntegerValue() {
        return bigDecimalValue().toBigInteger();
    }

    @Override
    public int intValueExact() {
        return bigDecimalValue().intValueExact();
    }

    @Override
    public long longValueExact() {
        return bigDecimalValue().longValueExact();
    }

    @Override
    public BigInteger bigIntegerValueExact() {
        return bigDecimalValue().toBigIntegerExact();
    }

    @Override
    public boolean isIntegral() {
        return bigDecimalValue().scale() == 0;
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
