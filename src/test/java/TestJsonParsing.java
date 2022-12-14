import dev.mccue.json.Json;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class TestJsonParsing {
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
}
