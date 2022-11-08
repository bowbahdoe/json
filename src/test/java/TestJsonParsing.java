import dev.mccue.json.Json;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class TestJsonParsing {
    @Test
    public void parseAThousandNulls() throws IOException {
        try (var is = this.getClass().getClassLoader()
                .getResourceAsStream("1000-null.json")) {
            assert is != null;
            var contents  = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            var json = Json.parse(contents);

            Json.Null[] nulls = new Json.Null[1000];
            Arrays.fill(nulls, Json.Null.instance());
            assertEquals(Json.Array.of(nulls), json);
        }
    }

    @Test
    public void parseA100kFile() throws IOException {
        try (var is = this.getClass().getClassLoader()
                .getResourceAsStream("json100k.json")) {
            assert is != null;
            var contents  = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            var json = Json.parse(contents);

            System.out.println(json);
        }
    }

    @Test
    public void testNumberEquals() {

    }
}
