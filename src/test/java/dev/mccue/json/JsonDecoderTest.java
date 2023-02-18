package dev.mccue.json;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
}
