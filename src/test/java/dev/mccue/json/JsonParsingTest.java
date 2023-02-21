package dev.mccue.json;

import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class JsonParsingTest {
    @Test
    public void readNull() {
        assertEquals(
                Json.ofNull(),
                Json.readString("null")
        );
    }

    @Test
    public void readTrue() {
        assertEquals(
                Json.ofTrue(),
                Json.readString("true")
        );
    }

    @Test
    public void readFalse() {
        assertEquals(
                Json.ofFalse(),
                Json.readString("false")
        );
    }

    @Test
    public void readArray() {
        assertEquals(
                Json.of(List.of(
                        Json.ofFalse(),
                        Json.ofTrue(),
                        Json.ofNull()
                )),
                Json.readString("[false, true, null]")
        );
    }

    @Test
    public void readNumber() {
        assertEquals(
                Json.of(123),
                Json.readString("123")
        );
    }

    @Test
    public void readNumberWithEButNoExponent() {
        assertThrows(
                JsonReadException.class,
                () -> Json.readString("123E")
        );
        assertThrows(
                JsonReadException.class,
                () -> Json.readString("123e")
        );
    }

    @Test
    public void readBigNumber() {
        assertEquals(
                Json.of(new BigInteger("1231312341414141414341414141414341414141414341414141414341414141414341414141414")),
                Json.readString("1231312341414141414341414141414341414141414341414141414341414141414341414141414")
        );
    }

    @Test
    public void readString() {
        assertEquals(
                Json.of("abc"),
                Json.readString("\"abc\"")
        );
    }

    @Test
    public void readEmptyArray() {
        assertEquals(
                Json.of(List.of()),
                Json.readString("[]")
        );
    }

    @Test
    public void readEmptyObject() {
        assertEquals(
                Json.of(Map.of()),
                Json.readString("{}")
        );
    }

    @Test
    public void readObject() {
        assertEquals(
                Json.objectBuilder()
                        .put("abc", 123)
                        .put("def", Json.objectBuilder()
                                .put("ghi", Json.arrayBuilder()
                                        .add("jkl")
                                        .add("mno")
                                        .build()
                                )
                                .build())
                        .put("qrs", Json.arrayBuilder()
                                .add("tuv")
                                .build())
                        .put("wx", Json.objectBuilder()
                                .put("y", "z")
                                .build())
                        .put("_", Json.ofNull())
                        .put("__", Json.of(List.of(Json.ofTrue(), Json.ofFalse())))
                        .build(),
                Json.readString("""
                        {
                            "abc": 123,
                            "def": {
                                "ghi": [
                                    "jkl",
                                    "mno"
                                ]
                            },
                            "qrs": ["tuv"],
                            "wx": {"y": "z"},
                            "_": null,
                            "__": [ true, false ]
                        }
                        """)
        );
    }

    @Test
    public void failOnUnclosed() {
        assertThrows(
                JsonReadException.class,
                () -> Json.readString("[{, 1]")
        );
        assertThrows(
                JsonReadException.class,
                () -> Json.readString("[{")
        );
        assertThrows(
                JsonReadException.class,
                () -> Json.readString("{")
        );
    }

    @Test
    public void testDecodingObject() {
        var j =  Json.readString("""
                        {
                            "abc": 123,
                            "def": {
                                "ghi": [
                                    "jkl",
                                    "mno"
                                ]
                            },
                            "qrs": ["tuv"],
                            "wx": {"y": "z"},
                            "_": null,
                            "__": [ true, false ]
                        }
                        """);
        assertEquals(
                (long) JsonDecoder.field(j, "abc", JsonDecoder::long_),
                123
        );

        assertEquals(
                JsonDecoder.field(j, "qrs", JsonDecoder.array((JsonDecoder<String>) JsonDecoder::string)),
                List.of("tuv")
        );

        assertEquals(
                JsonDecoder.field(j,"def", JsonDecoder.field("ghi", JsonDecoder.index(0, JsonDecoder::string))),
                "jkl"
        );

        assertEquals(
                JsonDecoder.field(j,"_", __ -> __),
                JsonNull.instance()
        );

        assertEquals(
                JsonDecoder.field(j,"_", JsonDecoder::null_),
                (JsonObject) null
        );
    }

    @Test
    public void testReadMultipleTopLevelForms() {
        var reader = Json.reader(new StringReader("{} {} \"aaaa\" \"\" {} [1213213123]  {\"aa\": 333}"));
        var forms = reader.stream().toList();
        assertEquals(
                List.of(
                        JsonObject.of(Map.of()),
                        JsonObject.of(Map.of()),
                        JsonString.of("aaaa"),
                        JsonString.of(""),
                        JsonObject.of(Map.of()),
                        JsonArray.of(
                                JsonNumber.of(1213213123)
                        ),
                        JsonObject.of(Map.of(
                                "aa", JsonNumber.of(333)
                        ))
                ),
                forms
        );
    }

    @Test
    public void testExponentialForms() {
        assertEquals(
                Json.readString("123e5"),
                Json.of(123E5)
        );
        assertEquals(
                Json.readString("123E5"),
                Json.of(123E5)
        );
    }

    @Test
    public void testNegativesForms() {
        assertEquals(
                Json.readString("-5"),
                Json.of(-5)
        );
        assertEquals(
                Json.readString("-0.5"),
                Json.of(-0.5)
        );
    }

    @Test
    public void testRoundTrip() {
        var j = Json.readString("""
                        {
                            "abc": 123,
                            "def": {
                                "ghi": [
                                    "jkl",
                                    "mno"
                                ]
                            },
                            "qrs": ["tuv"],
                            "wx": {"y": "z"},
                            "_": null,
                            "__": [ true, false ]
                        }
                        """);
        assertEquals(
                Json.readString(Json.writeString(j)),
                j
        );
    }

    @Test
    public void testEscapeChar() {
        var o = Json.readString("\"\\" + "u0009\"");
        assertEquals(Json.of("\u0009"), o);
    }

    @Test
    public void testExtraInputThrows() {
        assertThrows(JsonReadException.class, () -> Json.readString("{} {}"));
        assertThrows(JsonReadException.class, () -> Json.readString("{} a"));
        assertThrows(JsonReadException.class, () -> Json.read(new StringReader("{} {}")));
    }

    @Test
    public void testExtraWhitespaceIsFine() {
        assertEquals(Json.emptyObject(), Json.readString(("{} " + (char) 9 + (char) 10) + (char) 13 + (char) 32));
    }

    @Test
    public void testReadMultipleTopLevel() {
        var reader = Json.reader(new StringReader("""
                        {
                            "abc": 123,
                            "def": {
                                "ghi": [
                                    "jkl",
                                    "mno"
                                ]
                            },
                            "qrs": ["tuv"],
                            "wx": {"y": "z"},
                            "_": null,
                            "__": [ true, false ]
                        }
                        
                        []
                        
                        { "": [] }
                        """));

        var forms = reader.stream().toList();

        assertEquals(
                List.of(
                        Json.objectBuilder()
                                .put("abc", 123)
                                .put("def", Json.objectBuilder()
                                        .put("ghi", Json.arrayBuilder()
                                                .add("jkl")
                                                .add("mno")
                                                .build()
                                        )
                                        .build())
                                .put("qrs", Json.arrayBuilder()
                                        .add("tuv")
                                        .build())
                                .put("wx", Json.objectBuilder()
                                        .put("y", "z")
                                        .build())
                                .put("_", Json.ofNull())
                                .put("__", Json.of(List.of(Json.ofTrue(), Json.ofFalse())))
                                .build(),
                        Json.emptyArray(),
                        Json.objectBuilder()
                                .put("", Json.emptyArray())
                                .build()
                ),
                forms
        );
    }
}
