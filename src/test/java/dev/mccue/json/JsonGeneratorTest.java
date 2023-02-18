package dev.mccue.json;

import dev.mccue.json.stream.JsonGenerator;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonGeneratorTest {
    record T2(String method, Object arg) {}

    static class Gen implements JsonGenerator {
        ArrayList<Object> called = new ArrayList<>();
        @Override
        public void writeObjectStart() {
            called.add("writeObjectStart");
        }

        @Override
        public void writeObjectEnd() {
            called.add("writeObjectEnd");
        }

        @Override
        public void writeArrayStart() {
            called.add("writeArrayStart");
        }

        @Override
        public void writeArrayEnd() {
            called.add("writeArrayEnd");
        }

        @Override
        public void writeFieldName(String value) {
            called.add(new T2("writeFieldName", value));
        }

        @Override
        public void writeString(String value) {
            called.add(new T2("writeString", value));
        }

        @Override
        public void writeNumber(JsonNumber value) {
            called.add(new T2("writeNumber", value));
        }

        @Override
        public void writeTrue() {
            called.add("writeTrue");
        }

        @Override
        public void writeFalse() {
            called.add("writeFalse");
        }

        @Override
        public void writeNull() {
            called.add("writeNull");
        }
    }

    @Test
    public void testJsonGeneratorNull() {
        var gen = new Gen();
        JsonNull.instance().write(gen);
        assertEquals(gen.called, List.of("writeNull"));
    }

    @Test
    public void testJsonGeneratorTrue() {
        var gen = new Gen();
        JsonTrue.instance().write(gen);
        assertEquals(gen.called, List.of("writeTrue"));
    }

    @Test
    public void testJsonGeneratorFalse() {
        var gen = new Gen();
        JsonFalse.instance().write(gen);
        assertEquals(gen.called, List.of("writeFalse"));
    }

    @Test
    public void testJsonGeneratorArray() {
        var gen = new Gen();
        JsonArray.of().write(gen);
        assertEquals(gen.called, List.of("writeArrayStart", "writeArrayEnd"));
    }

    @Test
    public void testJsonGeneratorObject() {
        var gen = new Gen();
        JsonObject.of(Map.of()).write(gen);
        assertEquals(gen.called, List.of("writeObjectStart", "writeObjectEnd"));
    }

    @Test
    public void testJsonGeneratorString() {
        var gen = new Gen();
        JsonString.of("abc").write(gen);
        assertEquals(gen.called, List.of(new T2("writeString", "abc")));
    }

    @Test
    public void testJsonGeneratorNumber() {
        var gen = new Gen();
        JsonNumber.of(123).write(gen);
        assertEquals(gen.called, List.of(new T2("writeNumber", JsonNumber.of(123))));
    }

    @Test
    public void testJsonGeneratorEncodable() {
        var gen = new Gen();
        JsonEncodable e = () -> Json.of(123);
        e.write(gen);
        assertEquals(gen.called, List.of(new T2("writeNumber", JsonNumber.of(123))));
    }

    @Test
    public void testJsonGeneratorComplex() {
        var gen = new Gen();
        Json.readString("""
                                        {
                            "abc": 123,
                            "def": {
                                "ghi": [
                                    "jkl",
                                    "mno"
                                ]
                            },
                            "qrs": ["tuv"]
                        }
                
                """).write(gen);
        assertEquals(gen.called, List.of(
                "writeObjectStart",
                new T2("writeFieldName", "abc"),
                new T2("writeNumber", JsonNumber.of(123)),
                new T2("writeFieldName", "def"),
                "writeObjectStart",
                new T2("writeFieldName", "ghi"),
                "writeArrayStart",
                new T2("writeString", "jkl"),
                new T2("writeString", "mno"),
                "writeArrayEnd",
                "writeObjectEnd",
                new T2("writeFieldName", "qrs"),
                "writeArrayStart",
                new T2("writeString", "tuv"),
                "writeArrayEnd",
                "writeObjectEnd"
        ));
    }
}
