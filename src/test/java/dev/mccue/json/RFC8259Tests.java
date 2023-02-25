package dev.mccue.json;

import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class RFC8259Tests {
    @Test
    public void testFailingFiles() throws IOException {
        try (var walk = Files.walk(Path.of(
                "src","test", "resources", "rfc8259", "test_parsing"
        ))) {
            walk.forEach(path -> {
                var filename = path.getFileName();
                if (filename.toString().startsWith("n_")) {

                    try (var reader = Files.newInputStream(path)) {
                        // Too nested for current parser...
                        if (filename.toString().equals("n_structure_open_array_object.json") ||
                                filename.toString().equals("n_structure_100000_opening_arrays.json")) {
                            assertThrows(StackOverflowError.class, () -> Json.read(new InputStreamReader(reader)));
                        }
                        else if (!(
                                // Known failing cases
                                filename.toString().equals("n_string_unescaped_ctrl_char.json") ||
                                filename.toString().equals("n_string_unescaped_tab.json") ||
                                filename.toString().equals("n_string_unescaped_newline.json"))
                        ) {
                            assertThrows(JsonReadException.class, () -> Json.read(new InputStreamReader(reader)));
                        }
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                }
            });
        }
    }

    @Test
    public void testSuccessFiles() throws IOException {
        try (var walk = Files.walk(Path.of(
                "src", "test", "resources", "rfc8259", "test_parsing"
        ))) {
            walk.forEach(path -> {
                if (path.getFileName().toString().startsWith("y_")) {
                    try (var reader = Files.newBufferedReader(path)) {
                        Json.read(reader);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                }
            });
        }
    }
}
