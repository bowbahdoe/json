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

## Explanation


## Reading and Writing

<details>
    <summary>Show</summary>


### Read from a String

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

</details>

```
{"name":"Bop Bop","age":1,"cute":true}
```

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

## Encoding and Decoding

## Encode a basic object

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

## Encode nested objects

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
        implements JsonEncodable{

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

