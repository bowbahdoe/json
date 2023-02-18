package dev.mccue.json;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class JsonBigDecimalTest {
    @Test
    public void noNullBigDecimal() {
        assertThrows(NullPointerException.class, () -> JsonNumber.of((BigDecimal) null));
    }
}
