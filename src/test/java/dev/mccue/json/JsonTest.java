package dev.mccue.json;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
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

        var l = new ArrayList<Json>();
        l.add(null);
        assertEquals(
                JsonArray.of(JsonNull.instance()),
                Json.of(l)
        );
    }

    @Test
    public void testOfCollectionEncodable() {
        assertEquals(
                JsonArray.of(Json.ofFalse()),
                Json.of(List.of(false), JsonBoolean::of)
        );

        assertEquals(
                JsonArray.of(Json.ofNull()),
                Json.of(List.of(false), __ -> null)
        );

        assertEquals(
                JsonArray.of(Json.ofNull()),
                Json.of(List.of(false), __ -> JsonNull.instance())
        );
    }

    @Test
    public void testOfMap() {
        var m = new HashMap<String, JsonEncodable>();
        m.put("a", null);
        m.put("b", Json.of(1));

        assertEquals(
                JsonObject.of(Map.of(
                        "a", JsonNull.instance(),
                                "b", Json.of(1)
                        )),
                Json.of(m)
        );
    }

    @Test
    public void testOfMapEncoder() {
        var m = new HashMap<String, Integer>();
        m.put("a", 6);
        m.put("b", 7);

        m.put("c", null);

        assertEquals(
                JsonObject.of(Map.of(
                        "a", Json.of(6),
                        "b", Json.of(7),
                        "c", JsonNull.instance()
                )),
                Json.of(m, Json::of)
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

    @Test
    public void testTrueRepr() {
        assertEquals("true", JsonTrue.instance().toString());
    }

    @Test
    public void testFalseRepr() {
        assertEquals("false", JsonFalse.instance().toString());
    }

    @Test
    public void testNullRepr() {
        assertEquals("null", JsonNull.instance().toString());
    }
}
