package dev.mccue.json;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
