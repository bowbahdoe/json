# json

[![javadoc](https://javadoc.io/badge2/dev.mccue/json/javadoc.svg)](https://javadoc.io/doc/dev.mccue/json)
[![tests](https://github.com/bowbahdoe/json/actions/workflows/test.yml/badge.svg)](https://github.com/bowbahdoe/json/actions/workflows/test.yml)
<img src="./bopbop.png"></img>

A Java JSON Library intended to be easy to learn and simple to teach.

Requires Java 17+.

## Dependency Information

### Maven

```xml
<dependency>
    <groupId>dev.mccue</groupId>
    <artifactId>json</artifactId>
    <version>0.2.0</version>
</dependency>
```

### Gradle

```
dependencies {
    implementation("dev.mccue:json:0.2.0")
}
```

## What this does

The primary goals of this library are
1. Be easy to learn and simple to teach.
2. Have an API for decoding that is reasonably declarative and gives good feedback
   on unexpected input.
3. Make use of modern Java features.

The non-goals of this library are

1. Provide an API for data-binding.
2. Support every extension to the JSON spec.
3. Handle documents which cannot fit into memory.

## Examples

### Create Json from a String

<details>
    <summary>Show</summary>

```java
import dev.mccue.json.Json;
import dev.mccue.json.JsonObject;

public class Main {
   public static void main(String[] args) {
      Json line = Json.of("rainbow connection");

      System.out.println(line);
   }
}
```
</details>

### Create Json from Numbers

<details>
    <summary>Show</summary>

```java
import dev.mccue.json.Json;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

public class Main {
   public static void main(String[] args) {
      List<Json> numbers = List.of(
              Json.of(1),
              Json.of(2L),
              Json.of(3.5),
              Json.of(new BigInteger("4")),
              Json.of(new BigDecimal("5.5"))
      );


      System.out.println(numbers);
   }
}
```
</details>

### Create a JsonObject

<details>
    <summary>Show</summary>

```java
import dev.mccue.json.Json;
import dev.mccue.json.JsonObject;

public class Main {
   public static void main(String[] args) {
      JsonObject swedishChef = Json.objectBuilder()
              .put("name", "chef")
              .put("nationality", "swedish")
              .put("lines", 1)
              .build();

      System.out.println(swedishChef);
   }
}
```
</details>

### Create a JsonArray

<details>
    <summary>Show</summary>

```java
import dev.mccue.json.Json;
import dev.mccue.json.JsonArray;

public class Main {
   public static void main(String[] args) {
      JsonArray lonelyNumbers = Json.arrayBuilder()
              .add(1)
              .add(2)
              .build();

      System.out.println(lonelyNumbers);
   }
}
```
</details>

### Create a nested structure

<details>
    <summary>Show</summary>

```java
import dev.mccue.json.Json;
import dev.mccue.json.JsonObject;

public class Main {
   public static void main(String[] args) {
      JsonObject kermit = Json.objectBuilder()
              .put("name", "kermit")
              .put("wife", Json.objectBuilder()
                      .put("name", "ms piggy"))
              .put("children", Json.arrayBuilder()
                      .add(Json.objectBuilder()
                              .put("species", "frog")
                              .put("gender", "male"))
                      .add(Json.objectBuilder()
                              .put("species", "pig")
                              .put("gender", "female")))
              .put("commitmentIssues", true)
              .build();

      System.out.println(kermit);
   }
}
```
</details>

### Read from a String

<details>
    <summary>Show</summary>



```java
import dev.mccue.json.Json;

public class Main {
    public static void main(String[] args) {
        Json parsed = Json.readString("""
                {
                    "name": "Bop Bop",
                    "age": 1,
                    "cute": true
                }
                """);

        System.out.println(parsed);
    }
}
```
</details>

### Read from a file

<details>
    <summary>Show</summary>

```java
import dev.mccue.json.Json;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws IOException {
        Json parsed;
        try (var reader = Files.newBufferedReader(Path.of("in.json"))) {
            parsed = Json.read(reader);
        }

        System.out.println(parsed);
    }
}
```

</details>

### Read multiple top level forms

<details>
    <summary>Show</summary>

```java
import dev.mccue.json.Json;

import java.io.StringReader;

public class Main {
    public static void main(String[] args) {
        String source = """
                { "name": "gonzo" }
                { "name": "kermit" }
                { "name": "ms. piggy" }
                """;

        var reader = Json.reader(new StringReader(source));

        for (var muppet : reader) {
            System.out.println(muppet);
        }
    }
}
```

</details>

### Write to a String

<details>
    <summary>Show</summary>

```java
import dev.mccue.json.Json;

public class Main {
    public static void main(String[] args) {
        Json bopBop = Json.objectBuilder()
                .put("name", "Bop Bop")
                .put("age", 1)
                .put("cute", true)
                .build();
        
        String written = Json.writeString(bopBop);

        System.out.println(written);
    }
}
```

```
{"name":"Bop Bop","age":1,"cute":true}
```

</details>

### Write to a String with indentation

<details>
    <summary>Show</summary>

```java
import dev.mccue.json.Json;
import dev.mccue.json.JsonWriteOptions;

public class Main {
    public static void main(String[] args) {
        Json bopBop = Json.objectBuilder()
                .put("name", "Bop Bop")
                .put("age", 1)
                .put("cute", true)
                .build();
        
        String written = Json.writeString(
                bopBop,
                new JsonWriteOptions()
                        .withIndentation(4)
        );

        System.out.println(written);
    }
}
```

```
{
    "name": "Bop Bop",
    "age": 1,
    "cute": true
}
```

</details>

### Write to a file

<details>
    <summary>Show</summary>

```java 
import dev.mccue.json.Json;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws IOException {
        Json bopBop = Json.objectBuilder()
                .put("name", "Bop Bop")
                .put("age", 1)
                .put("cute", true)
                .build();

        try (var writer = Files.newBufferedWriter(
                Path.of("out.json")
        )) {
            Json.write(bopBop, writer);
        }
    }
}
```

</details>

### Encode a basic object

<details>
    <summary>Show</summary>

```java
import dev.mccue.json.Json;
import dev.mccue.json.JsonEncodable;
import dev.mccue.json.JsonWriteOptions;

record Muppet(String name, boolean canSing)
        implements JsonEncodable {

    @Override
    public Json toJson() {
        return Json.objectBuilder()
                .put("name", this.name)
                .put("canSing", this.canSing)
                .build();
    }
}

public class Main {
    public static void main(String[] args) {
        var animal = new Muppet("animal", false);
        System.out.println(Json.writeString(
                animal,
                new JsonWriteOptions()
                        .withIndentation(4)
        ));
    }
}
```

```
{
    "name": "animal",
    "canSing": false
}
```

</details>

### Encode nested objects

<details>
    <summary>Show</summary>

```java
import dev.mccue.json.Json;
import dev.mccue.json.JsonEncodable;
import dev.mccue.json.JsonWriteOptions;

import java.util.List;

record Muppet(String name)
        implements JsonEncodable {

    @Override
    public Json toJson() {
        return Json.objectBuilder()
                .put("name", this.name)
                .build();
    }
}

record Movie(String title, List<Muppet> cast)
        implements JsonEncodable {

    @Override
    public Json toJson() {
        return Json.objectBuilder()
                .put("title", this.title)
                .put("cast", this.cast)
                .build();
    }
}

public class Main {
    public static void main(String[] args) {
        var kermit = new Muppet("kermit");
        var gonzo = new Muppet("gonzo");
        var rizzo = new Muppet("rizzo");

        var treasureIsland = new Movie(
                "Treasure Island",
                List.of(kermit, gonzo, rizzo)
        );

        System.out.println(Json.writeString(
                treasureIsland,
                new JsonWriteOptions()
                        .withIndentation(4)
        ));
    }
}
```

```
{
    "title": "Treasure Island",
    "cast": [
        {
            "name": "kermit"
        },
        {
            "name": "gonzo"
        },
        {
            "name": "rizzo"
        }
    ]
}
```

</details>

### Encode objects of classes that you didn't make

<details>
    <summary>Show</summary>

```java
public class Main {
    static Json encodeInstant(Instant instant) {
        return Json.of(DateTimeFormatter.ISO_INSTANT.format(instant));
    }

    public static void main(String[] args) {
        Json instant = encodeInstant(Instant.now());
        System.out.println(Json.writeString(instant));
    }
}
```

</details>

### Decode a basic object

<details>
    <summary>Show</summary>

```java
import dev.mccue.json.Json;
import dev.mccue.json.JsonDecoder;

record Muppet(String name) {
    static Muppet fromJson(Json json) {
        var name = JsonDecoder.field(json, "name", JsonDecoder::string);
        return new Muppet(name);
    }
}

public class Main {
    public static void main(String[] args) {
        var jsonString = """
                [
                    {
                        "name": "kermit"
                    },
                    {
                        "name": "gonzo"
                    },
                    {
                        "name": "rizzo"
                    }
                ]
                """;
        var json = Json.readString(jsonString);

        var muppets = JsonDecoder.array(json, Muppet::fromJson);

        System.out.println(muppets);
    }
}
```

</details>

### Decode an object with optional fields.
<details>
    <summary>Show</summary>

```java
import dev.mccue.json.Json;
import dev.mccue.json.JsonDecoder;

record Muppet(String name, String role) {
    static Muppet fromJson(Json json) {
        var name = JsonDecoder.field(json, "name", JsonDecoder::string);
        var role = JsonDecoder.optionalField(json, "role", JsonDecoder::string, "sidekick");
        return new Muppet(name, role);
    }
}


public class Main {
    public static void main(String[] args) {
        var jsonString = """
                [
                    {
                        "name": "kermit",
                        "role": "captain"
                    },
                    {
                        "name": "gonzo"
                    },
                    {
                        "name": "rizzo"
                    }
                ]
                """;
        var json = Json.readString(jsonString);
        var muppets = JsonDecoder.array(json, Muppet::fromJson);

        System.out.println(muppets);
    }
}
```

</details>

### Decode nested objects

<details>
    <summary>Show</summary>

```java
import dev.mccue.json.Json;
import dev.mccue.json.JsonDecoder;

import java.util.List;

record Muppet(String name) {
    static Muppet fromJson(Json json) {
        return new Muppet(JsonDecoder.field(json, "name", JsonDecoder::string));
    }

}

record Movie(String title, List<Muppet> cast) {
    static Movie fromJson(Json json) {
        return new Movie(
                JsonDecoder.field(json, "title", JsonDecoder::string),
                JsonDecoder.field(json, "cast", JsonDecoder.array(Muppet::fromJson))
        );
    }
}

public class Main {
    public static void main(String[] args) {
        var jsonString = """
                {
                    "title": "Treasure Island",
                    "cast": [
                        {
                            "name": "kermit"
                        },
                        {
                            "name": "gonzo"
                        },
                        {
                            "name": "rizzo"
                        }
                    ]
                }
                """;
        var json = Json.readString(jsonString);
        var movie = Movie.fromJson(json);

        System.out.println(movie);
    }
}
```

</details>

### Decode an enum

<details>
    <summary>Show</summary>

```java
import dev.mccue.json.Json;
import dev.mccue.json.JsonDecodeException;
import dev.mccue.json.JsonDecoder;

import java.util.Arrays;
import java.util.List;

enum Location {
   CALIFORNIA,
   RHODE_ISLAND,
   SASKATCHEWAN,
   NEW_YORK;

   static Location fromJson(Json json) {
      return switch (JsonDecoder.string(json)) {
         case "CALIFORNIA" -> CALIFORNIA;
         case "RHODE_ISLAND" -> RHODE_ISLAND;
         case "SASKATCHEWAN" -> SASKATCHEWAN;
         case "NEW_YORK" -> NEW_YORK;
         default -> throw JsonDecodeException.of(
                 "Expected one of " + Arrays.toString(values()),
                 json
         );
      };
   }
}

public class Main {
   public static void main(String[] args) {
      Json locationsJson = Json.readString("""
              [
                  "CALIFORNIA",
                  "SASKATCHEWAN"
              ]
              """);

      List<Location> locations = JsonDecoder.array(
              locationsJson,
              Location::fromJson
      );

      System.out.println(locations);
   }
}
```

</details>

### Decode json into bean

<details>
    <summary>Show</summary>

⚠️ This example is just intended to show how you can use decoders to make objects
that have different construction methods. Don't mindlessly add getters
and setters to your classes!
```java
import dev.mccue.json.Json;
import dev.mccue.json.JsonDecoder;

import java.util.List;

class Fozzie {
    private String joke;
    private String punchline;
    private List<String> hecklers;

    public Fozzie() {}

    public String getJoke() {
        return joke;
    }

    public void setJoke(String joke) {
        this.joke = joke;
    }

    public String getPunchline() {
        return punchline;
    }

    public void setPunchline(String punchline) {
        this.punchline = punchline;
    }

    public List<String> getHecklers() {
        return hecklers;
    }

    public void setHecklers(List<String> hecklers) {
        this.hecklers = hecklers;
    }

    @Override
    public String toString() {
        return "Fozzie[" +
                "joke=" + joke +
                ", punchline=" + punchline  +
                ", hecklers=" + hecklers +
                ']';
    }
}

public class Main {
    static Fozzie fozzieFromJson(Json json) {
        var fozzie = new Fozzie();
        fozzie.setJoke(JsonDecoder.field(json, "joke", JsonDecoder::string));
        fozzie.setPunchline(JsonDecoder.field(json, "punchline", JsonDecoder::string));
        fozzie.setHecklers(JsonDecoder.field(json, "hecklers", JsonDecoder.array(JsonDecoder::string)));
        return fozzie;
    }

    public static void main(String[] args) {
        Json fozzieJson = Json.readString("""
                {
                    "joke": "What do you get when you cross the Atlantic with the titanic?",
                    "punchline": "Halfway! Wacka Wacka!",
                    "hecklers": ["Statler", "Waldorf"]
                }
                """);

        Fozzie fozzie = fozzieFromJson(fozzieJson);

        System.out.println(fozzie);
    }
}
```

</details>