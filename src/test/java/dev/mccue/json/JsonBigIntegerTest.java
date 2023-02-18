package dev.mccue.json;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class JsonBigIntegerTest {
    @Test
    public void noNullBigInt() {
        assertThrows(NullPointerException.class, () -> JsonNumber.of((BigInteger) null));
    }
}
