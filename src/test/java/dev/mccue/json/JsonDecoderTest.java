package dev.mccue.json;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class JsonDecoderTest {
    @Test
    public void objectDecoderIsOrdered() {
        var entries = new ArrayList<Map.Entry<String, Json>>();
        for (int i = 0; i < 10000; i++) {
            entries.add(Map.entry(Double.toString(Math.random()), Json.of(Math.random())));
        }

        var objBuilder = Json.objectBuilder();
        for (var entry : entries) {
            objBuilder.put(entry.getKey(), entry.getValue());
        }
        var obj = objBuilder.build();

        var decoded = JsonDecoder.object(obj, x -> x);
        assertEquals(
                new ArrayList<>(decoded.entrySet()),
                new ArrayList<>(obj.entrySet())
        );
    }

    @Test
    public void objectDecoderNeedsObject() {
        assertThrows(JsonDecodeException.class, () -> JsonDecoder.object(Json.of(123)));
    }

    @Test
    public void testDecodeStructure() {
        record Pet(String name, long age) {
            static Pet fromJson(Json json) {
                return new Pet(
                        JsonDecoder.field(json, "name", JsonDecoder::string),
                        JsonDecoder.field(json, "age", JsonDecoder::long_)
                );
            }
        }

        record Person(String name, int age, List<Pet> pets) {
            static Person fromJson(Json json) {
                return new Person(
                        JsonDecoder.field(json, "name", JsonDecoder::string),
                        JsonDecoder.field(json, "age", JsonDecoder::int_),
                        JsonDecoder.optionalField(json, "pets", JsonDecoder.array(Pet::fromJson), List.of())
                );
            }
        }

        var json = Json.readString("""
                {
                    "people": [
                        {
                            "name": "bob",
                            "age": 2
                        },
                        {
                            "name": "joe",
                            "age": 24,
                            "pets": [ {"name": "fred", "age": 10 } ]
                        }
                    ]
                }
                """);

        var people = JsonDecoder.field(json, "people", JsonDecoder.array(Person::fromJson));

        assertEquals(
                people,
                List.of(
                        new Person("bob", 2, List.of()),
                        new Person("joe", 24, List.of(
                                new Pet("fred", 10L)
                        ))
                )
        );
    }

    @Test
    public void nullableDecoderTest() {
        assertEquals(
                Optional.of("abc"),
                JsonDecoder.nullable(JsonDecoder::string)
                    .decode(JsonString.of("abc"))
        );
        assertEquals(
                Optional.empty(),
                JsonDecoder.nullable(JsonDecoder::string)
                        .decode(JsonNull.instance())
        );
        assertThrows(
                JsonDecodeException.class,
                () -> JsonDecoder.nullable(JsonDecoder::string)
                        .decode(JsonArray.of())
        );

        assertEquals(
                "abc",
                JsonDecoder.nullable(JsonDecoder::string, null)
                        .decode(JsonString.of("abc"))
        );
        assertNull(JsonDecoder.nullable(JsonDecoder::string, null)
                .decode(JsonNull.instance()));
        assertEquals(
                "def",
                JsonDecoder.nullable(JsonDecoder::string, "def")
                        .decode(JsonNull.instance())
        );
        assertThrows(
                JsonDecodeException.class,
                () -> JsonDecoder.nullable(JsonDecoder::string, null)
                        .decode(JsonArray.of())
        );
    }

    @Test
    public void booleanDecoderTest() {
        assertTrue(JsonDecoder.boolean_(JsonTrue.instance()));
        assertFalse(JsonDecoder.boolean_(JsonFalse.instance()));
        assertThrows(
                JsonDecodeException.class,
                () -> JsonDecoder.boolean_(JsonString.of("abc"))
        );
    }

    @Test
    public void stringDecoderTest() {
        assertEquals("abc", JsonDecoder.string(JsonString.of("abc")));
        assertThrows(
                JsonDecodeException.class,
                () -> JsonDecoder.string(JsonNull.instance())
        );
    }

    @Test
    public void intDecoderTest() {
        assertEquals(1, JsonDecoder.int_(Json.of(1)));
        assertEquals(2, JsonDecoder.int_(Json.of(2L)));
        assertEquals(3, JsonDecoder.int_(Json.of(new BigInteger("3"))));
        assertEquals(4, JsonDecoder.int_(Json.of(new BigDecimal("4"))));
        assertThrows(
                JsonDecodeException.class,
                () -> JsonDecoder.int_(Json.of(Long.MAX_VALUE))
        );
        assertThrows(
                JsonDecodeException.class,
                () -> JsonDecoder.int_(Json.of("abc"))
        );
    }

    @Test
    public void longDecoderTest() {
        assertEquals(1L, JsonDecoder.long_(Json.of(1)));
        assertEquals(2L, JsonDecoder.long_(Json.of(2L)));
        assertEquals(3L, JsonDecoder.long_(Json.of(new BigInteger("3"))));
        assertEquals(4L, JsonDecoder.long_(Json.of(new BigDecimal("4"))));
        assertThrows(
                JsonDecodeException.class,
                () -> JsonDecoder.long_(Json.of(new BigDecimal("43242523525235235255")))
        );
        assertThrows(
                JsonDecodeException.class,
                () -> JsonDecoder.long_(Json.of("abc"))
        );
    }

    @Test
    public void floatDecoderTest() {
        assertEquals(1f, JsonDecoder.float_(Json.of(1)));
        assertEquals(2f, JsonDecoder.float_(Json.of(2L)));
        assertEquals(3f, JsonDecoder.float_(Json.of(new BigInteger("3"))));
        assertEquals(4f, JsonDecoder.float_(Json.of(new BigDecimal("4"))));
        assertThrows(
                JsonDecodeException.class,
                () -> JsonDecoder.long_(Json.of("abc"))
        );
    }

    @Test
    public void doubleDecoderTest() {
        assertEquals(1L, JsonDecoder.double_(Json.of(1)));
        assertEquals(2L, JsonDecoder.double_(Json.of(2L)));
        assertEquals(3L, JsonDecoder.double_(Json.of(new BigInteger("3"))));
        assertEquals(4L, JsonDecoder.double_(Json.of(new BigDecimal("4"))));
        assertThrows(
                JsonDecodeException.class,
                () -> JsonDecoder.double_(Json.of("abc"))
        );
    }

    @Test
    public void nullDecoderTest() {
        assertNull(JsonDecoder.null_(JsonNull.instance()));
        assertEquals(5, JsonDecoder.null_(JsonNull.instance(), 5));
        assertThrows(
                JsonDecodeException.class,
                () -> JsonDecoder.null_(Json.of("abc"))
        );
    }
}
