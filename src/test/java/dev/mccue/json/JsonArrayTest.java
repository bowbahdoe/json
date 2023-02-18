package dev.mccue.json;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

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
}
