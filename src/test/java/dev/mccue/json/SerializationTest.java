package dev.mccue.json;

import dev.mccue.json.serialization.JsonSerializationProxy;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class SerializationTest {
    @Test
    public void testSerialization() throws IOException, ClassNotFoundException {
        /*
        try (var fis = Objects.requireNonNull(
                TestSerialization.class.getResourceAsStream("/serialization_example_1.json")
        )) {
            var json = Json.read(new InputStreamReader(fis));


            try (var fos = new FileOutputStream("serialization_example_1.ser");
                 var oos = new ObjectOutputStream(fos)) {
                oos.writeObject(json);
            }

        }
        */

        try (var fis = Objects.requireNonNull(SerializationTest.class.getResourceAsStream("/serialization_example_1.json"));
             var fis2 = Objects.requireNonNull(SerializationTest.class.getResourceAsStream("/serialization_example_1.ser"));
             var ois = new ObjectInputStream(fis2)) {
            var json = Json.read(new InputStreamReader(fis));
            assertEquals(json, ois.readObject());
        }
    }

    @Test
    public void testNonsenseForm() throws IOException, ClassNotFoundException {
        byte[] data;
        try (var baos = new ByteArrayOutputStream();
             var oos = new ObjectOutputStream(baos)) {
            oos.writeObject(new JsonSerializationProxy(""));
            oos.writeObject(new JsonSerializationProxy("{\"a\":"));
            data = baos.toByteArray();
        }

        try (var bais = new ByteArrayInputStream(data);
             var ois = new ObjectInputStream(bais)) {
            assertThrows(JsonReadException.class, ois::readObject);
            assertThrows(JsonReadException.class, ois::readObject);
        }
    }

    @Test
    public void testMalicousStream() throws IOException, ClassNotFoundException {
        byte[] jsonNull = {
                -84, -19, 0, 5, 115, 114, 0, 23, 100, 101, 118, 46, 109, 99, 99, 117, 101, 46, 106, 115, 111, 110, 46, 74, 115, 111, 110, 78, 117, 108, 108, 0, 0, 0, 0, 0, 0, 0, 1, 2, 0, 0, 120, 112
        };

        byte[] jsonTrue = {
                -84, -19, 0, 5, 115, 114, 0, 23, 100, 101, 118, 46, 109, 99, 99, 117, 101, 46, 106, 115, 111, 110, 46, 74, 115, 111, 110, 84, 114, 117, 101, 0, 0, 0, 0, 0, 0, 0, 1, 2, 0, 0, 120, 112
        };

        byte[] jsonFalse = {
                -84, -19, 0, 5, 115, 114, 0, 24, 100, 101, 118, 46, 109, 99, 99, 117, 101, 46, 106, 115, 111, 110, 46, 74, 115, 111, 110, 70, 97, 108, 115, 101, 0, 0, 0, 0, 0, 0, 0, 1, 2, 0, 0, 120, 112
        };

        /*byte[] data;
        try (var baos = new ByteArrayOutputStream();
             var oos = new ObjectOutputStream(baos)) {
            oos.writeObject(JsonFalse.instance());
            data = baos.toByteArray();
        }

        System.out.println(Arrays.toString(data));*/


        try (var ois = new ObjectInputStream(new ByteArrayInputStream(jsonNull))) {
            assertThrows(IllegalStateException.class, ois::readObject);
        }

        try (var ois = new ObjectInputStream(new ByteArrayInputStream(jsonTrue))) {
            assertThrows(IllegalStateException.class, ois::readObject);
        }

        try (var ois = new ObjectInputStream(new ByteArrayInputStream(jsonFalse))) {
            assertThrows(IllegalStateException.class, ois::readObject);
        }
    }

    private Object roundTrip(Object o) throws IOException, ClassNotFoundException {
        byte[] data;
        try (var baos = new ByteArrayOutputStream();
             var oos = new ObjectOutputStream(baos)) {
            oos.writeObject(o);
            data = baos.toByteArray();
        }

        try (var bais = new ByteArrayInputStream(data);
             var ois = new ObjectInputStream(bais)) {
            return ois.readObject();
        }
    }

    @Test
    public void roundTripTrue() throws IOException, ClassNotFoundException {
        assertEquals(JsonTrue.instance(), roundTrip(JsonTrue.instance()));
        assertSame(JsonTrue.instance(), roundTrip(JsonTrue.instance()));
    }

    @Test
    public void roundTripFalse() throws IOException, ClassNotFoundException {
        assertEquals(JsonFalse.instance(), roundTrip(JsonFalse.instance()));
        assertSame(JsonFalse.instance(), roundTrip(JsonFalse.instance()));
    }

    @Test
    public void roundTripNull() throws IOException, ClassNotFoundException {
        assertEquals(JsonNull.instance(), roundTrip(JsonNull.instance()));
        assertSame(JsonNull.instance(), roundTrip(JsonNull.instance()));
    }

    @Test
    public void roundTripString() throws IOException, ClassNotFoundException {
        assertEquals(JsonString.of("abc"), roundTrip(JsonString.of("abc")));
    }

    @Test
    public void roundTripArray() throws IOException, ClassNotFoundException {
        var o = JsonArray.of(
                JsonString.of("abc"),
                JsonNull.instance()
        );
        assertEquals(o, roundTrip(o));
    }

    @Test
    public void roundTripObject() throws IOException, ClassNotFoundException {
        var o = JsonObject.of(Map.of(
                "abc", JsonString.of("def"),
                "ghi", JsonNull.instance(),
                "kjl", JsonArray.of()
        ));
        assertEquals(o, roundTrip(o));
    }
}
