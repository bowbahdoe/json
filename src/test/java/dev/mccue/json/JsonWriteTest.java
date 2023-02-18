package dev.mccue.json;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonWriteTest {
    @Test
    public void testWritingInterestingChars() {
        assertEquals(
                "\"\\\\ \\n \\b \\f \\r \\t \\\" \\/\"",
                Json.writeString(Json.of("\\ \n \b \f \r \t \" /"))
        );

        assertEquals(
                "{\"\\\\ \\n \\b \\f \\r \\t \\\" \\/\":\"\\\\ \\n \\b \\f \\r \\t \\\" \\/\"}",
                Json.writeString(Json.objectBuilder(Map.of(
                        "\\ \n \b \f \r \t \" /",
                        Json.of("\\ \n \b \f \r \t \" /")
                )).build())
        );
    }

    @Test
    public void testRoundTripInterestingChars() {
        var interesting = "\\ \n \b \f \r \t \" /";
        assertEquals(
                interesting,
                JsonDecoder.string(Json.readString(Json.writeString(Json.of(interesting))))
        );
    }
}
