package dev.mccue.json;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CollectorTest {
    @Test
    public void testArrayCollector() {
        assertEquals(
                JsonArray.of(Json.of(1), Json.of(2), Json.of(3)),
                Stream.of(1, 2, 3)
                        .map(Json::of)
                        .collect(Json.arrayCollector())
        );

        assertEquals(
                Json.emptyArray(),
                Stream.<Json>of()
                        .collect(Json.arrayCollector())
        );
    }

    @Test
    public void testObjectCollector() {
        assertEquals(
                JsonObject.of(Map.of(
                        "a", Json.of("b"),
                        "c", Json.of("d")
                )),
                Stream.of('a', 'c')
                        .collect(Json.objectCollector(
                                String::valueOf,
                                c -> Json.of(String.valueOf((char) (c + 1)))
                        ))
        );

        assertEquals(
                Json.emptyObject(),
                Stream.of()
                        .collect(Json.objectCollector(
                                __ -> "",
                                __ -> Json.ofNull()
                        ))
        );
    }
}
