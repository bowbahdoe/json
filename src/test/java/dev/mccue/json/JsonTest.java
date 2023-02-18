package dev.mccue.json;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonTest {
    @Test
    public void testNullOfMethods() {
        assertEquals(JsonNull.instance(), Json.of((JsonEncodable) null));
        assertEquals(JsonNull.instance(), Json.of((BigDecimal) null));
        assertEquals(JsonNull.instance(), Json.of((Double) null));
        assertEquals(JsonNull.instance(), Json.of((Long) null));
        assertEquals(JsonNull.instance(), Json.of((Float) null));
        assertEquals(JsonNull.instance(), Json.of((Integer) null));
        assertEquals(JsonNull.instance(), Json.of((Integer) null));
        assertEquals(JsonNull.instance(), Json.of((BigInteger) null));
        assertEquals(JsonNull.instance(), Json.of((String) null));
        assertEquals(JsonNull.instance(), Json.of((List<Json>) null));
        assertEquals(JsonNull.instance(), Json.of((Boolean) null));
        assertEquals(JsonNull.instance(), Json.of((List<JsonEncodable>) null));
        assertEquals(JsonNull.instance(), Json.of((Map<String, Json>) null));
        assertEquals(JsonNull.instance(), Json.of(() -> null));
    }

    @Test
    public void testOfCollection() {
        assertEquals(
                JsonArray.of(Json.ofFalse(), Json.of(1), Json.of("abc")),
                Json.of(List.of((JsonEncodable) Json::ofFalse, Json.of(1), Json.of("abc")))
        );
    }

    @Test
    public void testOfEncodable() {
        assertEquals(Json.objectBuilder()
                .put("abc", 123)
                .build(),
                Json.of(() -> Json.objectBuilder()
                        .put("abc", 123)
                        .build()));
    }

    @Test
    public void testOfNumbers() {
        assertEquals(
                Json.of(1.2),
                JsonNumber.of(1.2)
        );
        assertEquals(
                Json.of(1.2f),
                JsonNumber.of(1.2f)
        );
        assertEquals(
                Json.of(12),
                JsonNumber.of(12)
        );
        assertEquals(
                Json.of(12L),
                JsonNumber.of(12L)
        );
        assertEquals(
                Json.of(new BigDecimal("111231231231231.233122")),
                JsonNumber.of(new BigDecimal("111231231231231.233122"))
        );
        assertEquals(
                Json.of(new BigInteger("111231231231231233122")),
                JsonNumber.of(new BigInteger("111231231231231233122"))
        );
    }

    @Test
    public void testOfString() {
        assertEquals(Json.of("abc"), JsonString.of("abc"));
    }
}
