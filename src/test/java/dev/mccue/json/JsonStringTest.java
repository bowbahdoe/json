package dev.mccue.json;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class JsonStringTest {
    @Test
    public void noNullJsonString() {
        assertThrows(NullPointerException.class, () -> JsonString.of(null));
    }
}
