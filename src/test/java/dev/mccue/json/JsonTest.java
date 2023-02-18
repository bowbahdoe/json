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
}
