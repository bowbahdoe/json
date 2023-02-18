package dev.mccue.json;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;


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
}
