package dev.mccue.json;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
