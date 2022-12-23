package dev.mccue.json;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class JsonDoubleTest {
    @Test
    public void cannotMakePositiveInfinity() {
        assertThrows(
                IllegalArgumentException.class,
                () -> Json.of(Double.POSITIVE_INFINITY)
        );
    }

    @Test
    public void cannotMakeNegativeInfinity() {
        assertThrows(
                IllegalArgumentException.class,
                () -> Json.of(Double.NEGATIVE_INFINITY)
        );
    }

    @Test
    public void cannotMakeNaN() {
        assertThrows(
                IllegalArgumentException.class,
                () -> Json.of(Double.NaN)
        );
    }
}
