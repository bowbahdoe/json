package dev.mccue.json;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class JsonObjectTest {
    @Test
    public void testObjectBuilderOrdered() {
        var entries = new ArrayList<Map.Entry<String, Json>>();
        for (int i = 0; i < 10000; i++) {
            entries.add(Map.entry(Double.toString(Math.random()), Json.of(Math.random())));
        }

        var objBuilder = Json.objectBuilder();
        for (var entry : entries) {
            objBuilder.put(entry.getKey(), entry.getValue());
        }
        var obj = objBuilder.build();

        assertEquals(new ArrayList<>(obj.entrySet()), entries);

        var lhm = new LinkedHashMap<String, Json>();
        for (var entry : entries) {
            lhm.put(entry.getKey(), entry.getValue());
        }
        assertEquals(
                new ArrayList<>(Json.objectBuilder(lhm).build().entrySet()),
                entries
        );
    }

    @Test
    public void mutatingOriginalCollectionDoesNotChangeObject() {
        var m = new HashMap<String, Json>();
        m.put("a", Json.of("b"));
        var o = JsonObject.of(m);

        m.put("b", Json.of("c"));

        assertEquals(o, JsonObject.of(Map.of("a", Json.of("b"))));
    }

    @Test
    public void mutatingMethodsOnObjectThrow() {
        var m = new HashMap<String, Json>();
        m.put("a", Json.of("b"));
        var o = JsonObject.of(m);

        assertThrows(UnsupportedOperationException.class, () -> o.put("a", Json.of("c")));
        assertThrows(UnsupportedOperationException.class, () -> o.replace("a", Json.of("b")));
        assertThrows(UnsupportedOperationException.class, () -> o.replace("a", Json.of("b"), Json.of("c")));
        assertThrows(UnsupportedOperationException.class, () -> o.putAll(Map.of()));
        assertThrows(UnsupportedOperationException.class, () -> o.putIfAbsent("a", Json.of("b")));
        assertThrows(UnsupportedOperationException.class, o::clear);
        assertThrows(UnsupportedOperationException.class, () -> o.compute("a", (__, ___) -> Json.of("a")));
        assertThrows(UnsupportedOperationException.class, () -> o.computeIfAbsent("a", (__) -> Json.of("a")));
        assertThrows(UnsupportedOperationException.class, () -> o.computeIfPresent("a", (__, ___) -> Json.of("a")));
        assertThrows(UnsupportedOperationException.class, () -> o.remove("a"));
        assertThrows(UnsupportedOperationException.class, () -> o.remove("a", Json.of("b")));
    }
}
