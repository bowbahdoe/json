package dev.mccue.json;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class JsonArrayTest {
    @Test
    public void noNullJsonArray() {
        assertThrows(NullPointerException.class, () -> JsonArray.of((Json[]) null));
        assertThrows(NullPointerException.class, () -> JsonArray.of((List<Json>) null));
    }

    @Test
    public void noNullJsonArrayElements() {
        var e = new ArrayList<Json>();
        e.add(null);
        assertThrows(NullPointerException.class, () -> JsonArray.of(e));
    }

    @Test
    public void arrayBuilderTest() {
        for (var builder : List.of(
                Json.arrayBuilder(),
                Json.arrayBuilder(List.of()),
                JsonArray.builder(),
                JsonArray.builder(0),
                JsonArray.builder(1),
                JsonArray.builder(5),
                JsonArray.builder(100)
        )) {

            var array = builder
                    .add((Boolean) true)
                    .add((Boolean) false)
                    .add((Boolean) null)
                    .addTrue()
                    .addFalse()
                    .addNull()
                    .add(true)
                    .add(false)
                    .add(1)
                    .add(1L)
                    .add(1.0f)
                    .add(1.0)
                    .add((Integer) 1)
                    .add((Integer) null)
                    .add((Long) 1L)
                    .add((Long) null)
                    .add((Float) 1.0f)
                    .add((Float) null)
                    .add((Double) 1.0)
                    .add((Double) null)
                    .add("")
                    .add((String) null)
                    .add(BigDecimal.ONE)
                    .add((BigDecimal) null)
                    .add(BigInteger.ONE)
                    .add((BigInteger) null)
                    .addAll(List.of(Json.of("a"), Json.of("b"), Json.of("c")))
                    .add(() -> Json.of("d"));

            var expected = JsonArray.of(
                    Json.ofTrue(),
                    Json.ofFalse(),
                    Json.ofNull(),
                    Json.ofTrue(),
                    Json.ofFalse(),
                    Json.ofNull(),
                    Json.ofTrue(),
                    Json.ofFalse(),
                    Json.of(1),
                    Json.of(1L),
                    Json.of(1.0f),
                    Json.of(1.0),
                    Json.of(1),
                    Json.ofNull(),
                    Json.of(1L),
                    Json.ofNull(),
                    Json.of(1.0f),
                    Json.ofNull(),
                    Json.of(1.0),
                    Json.ofNull(),
                    Json.of(""),
                    Json.ofNull(),
                    Json.of(BigDecimal.ONE),
                    Json.ofNull(),
                    Json.of(BigInteger.ONE),
                    Json.ofNull(),
                    Json.of("a"),
                    Json.of("b"),
                    Json.of("c"),
                    Json.of("d")
            );
            assertEquals(
                    expected,
                    array.build()
            );
            assertEquals(
                    expected,
                    array.toJson()
            );
        }
    }

    @Test
    public void mutatingMethodsThrow() {
        for (var array : List.of(
                Json.emptyArray(),
                JsonArray.of(new ArrayList<>())
        )) {

            assertThrows(UnsupportedOperationException.class, () -> array.add(Json.of(1)));
            assertThrows(UnsupportedOperationException.class, () -> array.add(1, Json.of(1)));
            assertThrows(UnsupportedOperationException.class, () -> array.replaceAll((__) -> Json.of(1)));
            assertThrows(UnsupportedOperationException.class, () -> array.remove(Json.of(1)));
            assertThrows(UnsupportedOperationException.class, () -> array.removeAll(List.of(Json.emptyArray())));
            assertThrows(UnsupportedOperationException.class, () -> array.removeIf((__) -> true));
            assertThrows(UnsupportedOperationException.class, () -> array.sort((_a, _b) -> 0));
            assertThrows(UnsupportedOperationException.class, array::clear);
            assertThrows(UnsupportedOperationException.class, () -> array.addAll(List.of()));
            assertThrows(UnsupportedOperationException.class, () -> array.addAll(1, List.of()));
            assertThrows(UnsupportedOperationException.class, () -> array.set(1, Json.ofNull()));
            assertThrows(UnsupportedOperationException.class, () -> array.retainAll(List.of(Json.ofNull())));
            assertThrows(UnsupportedOperationException.class, () -> array.remove(0));
        }
    }
}
