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
    <version>0.2.2</version>
</dependency>
```

### Gradle

```
dependencies {
    implementation("dev.mccue:json:0.2.2")
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

## Tutorial

<details>
    <summary>Show</summary>

### The Data Model

JSON is a data format. It looks like the following sample.

```json
{
    "name": "kermit",
    "wife": null,
    "girlfriend": "Ms. Piggy",
    "age": 22,
    "children": [
        {
            "species": "frog",
            "gender": "male"
        },
        {
            "species": "pig",
            "gender": "female"
        }
    ],
    "commitmentIssues": true
}
```

In JSON you represent data using a combination of objects (maps from strings to JSON),
arrays (ordered sequences of JSON), strings, numbers, true, false, and null.

Therefore, one "natural" way to think about the data stored in a JSON document
is as the union of those possibilities.

```
JSON is one of
- a map of string to JSON
- a list of JSON
- a string
- a number
- true
- false
- null
```

The way to represent this in Java is using a sealed interface, which
provides an explicit list of types which are allowed to implement it.

```java 
public sealed interface Json
        permits 
            JsonObject,
            JsonArray,
            JsonString,
            JsonNumber,
            JsonBoolean,
            JsonNull {
}
```

This means that if you have a field or variable which has the type `Json`, you know
that it is either a `JsonObject`, `JsonArray`, `JsonString`, `JsonNumber`, `JsonBoolean`,
or `JsonNull`.

That is the first thing provided by my library. There is a `Json` type
and subtypes representing those different cases.

```java
import dev.mccue.json.*;

public class Main {
    static Json greeting() {
        return JsonString.of("hello");
    }
    
    public static void main(String[] args) {
        Json json = greeting();
        switch (json) {
            case JsonObject object ->
                    System.out.println("An object");
            case JsonArray array ->
                    System.out.println("An array");
            case JsonString str ->
                    System.out.println("A string");
            case JsonNumber number ->
                    System.out.println("A number");
            case JsonBoolean bool ->
                    System.out.println("A boolean");
            case JsonNull __ ->
                    System.out.println("A json null");
        }
    }
}
```

You can create instances
of these subtypes using factory methods on the types themselves.

```java
import dev.mccue.json.*;

import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        JsonObject kermit = JsonObject.of(Map.of(
                "name", JsonString.of("kermit"),
                "age", JsonNumber.of(22),
                "commitmentIssues", JsonBoolean.of(true),
                "wife", JsonNull.instance(),
                "children", JsonArray.of(List.of(
                        JsonString.of("Tiny Tim")
                ))
        ));

        System.out.println(kermit);
    }
}
```

Or by using factory methods on `Json`, which aren't guaranteed to give you
any specific subtype but in exchange will handle converting any stray `null`s to `JsonNull`.

```java
import dev.mccue.json.*;

import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Json kermit = Json.of(Map.of(
                "name", Json.of("kermit"),
                "age", Json.of(22),
                "commitmentIssues", Json.of(true),
                "wife", Json.ofNull(),
                "children", Json.of(List.of(
                        JsonString.of("Tiny Tim")
                ))
        ));

        System.out.println(kermit);
    }
}
```

For `JsonObject` and `JsonArray`, there also use builders available which
can make it so that you don't need to write `Json.of` on every value.

```java
import dev.mccue.json.Json;

public class Main {
    public static void main(String[] args) {
        Json kermit = Json.objectBuilder()
                .put("name", "kermit")
                .put("age", 22)
                .putTrue("commitmentIssues")
                .putNull("wife")
                .put("children", Json.arrayBuilder()
                        .add("Tiny Tim"))
                .build();

        System.out.println(kermit);
    }
}
```

### Writing

Once you have some `Json` you can write it out to a `String` using `Json.writeString`

```java
import dev.mccue.json.Json;

public class Main {
    public static void main(String[] args) {
        Json songJson = Json.objectBuilder()
                .put("title", "Rainbow Connection")
                .put("year", 1979)
                .build();

        String song = Json.writeString(songJson);
        System.out.println(song);
    }
}
```

```json
{"title":"Rainbow Connection","year":1979}
```

If output is meant to be consumed by humans then whitespace can be added
using a customized instance of `JsonWriteOptions`.

```java
import dev.mccue.json.Json;
import dev.mccue.json.JsonWriteOptions;

public class Main {
    public static void main(String[] args) {
        Json songJson = Json.objectBuilder()
                .put("title", "Rainbow Connection")
                .put("year", 1979)
                .build();

        String song = Json.writeString(
                songJson,
                new JsonWriteOptions()
                        .withIndentation(4)
        );
        
        System.out.println(song);
    }
}
```

```json
{
    "title": "Rainbow Connection",
    "year": 1979
}
```

If you want to write JSON to something other than a `String`, you need to
obtain a `Writer` and use `Json.write`.

```java
import dev.mccue.json.Json;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws IOException {
        Json songJson = Json.objectBuilder()
                .put("title", "Rainbow Connection")
                .put("year", 1979)
                .build();


        try (var fileWriter = Files.newBufferedWriter(
                Path.of("song.json"))
        ) {
            Json.write(songJson, fileWriter);
        }
    }
}
```

### Encoding

To turn a class you have defined into JSON, you just need to make a method
which creates an instance of `Json` from the data stored in your class.

```java
import dev.mccue.json.Json;

record Muppet(String name) {
    Json toJson() {
        return Json.objectBuilder()
                .put("name", name)
                .build();
    }
}

public class Main {
    public static void main(String[] args) {
        var beaker = new Muppet("beaker");
        Json beakerJson = beaker.toJson();

        System.out.println(Json.writeString(beakerJson));
    }
}
```

This process is "encoding." You "encode" your data into JSON and then "write"
that JSON to some output.

For classes that you did not define, the logic for the conversion just needs to live somewhere.
Dealer's choice where, but static methods are generally a good call.

```java
import dev.mccue.json.Json;

import java.time.Month;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;

final class TimeEncoders {
    private TimeEncoders() {}

    static Json monthDayToJson(MonthDay monthDay) {
        return Json.of(
                DateTimeFormatter.ofPattern("MM-dd")
                        .format(monthDay)
        );
    }
}

record Muppet(String name, MonthDay birthday) {
    Json toJson() {
        return Json.objectBuilder()
                .put("name", name)
                .put(
                        "birthday", 
                        TimeEncoders.monthDayToJson(birthday)
                )
                .build();
    }
}

public class Main {
    public static void main(String[] args) {
        var elmo = new Muppet(
                "Elmo",
                MonthDay.of(Month.FEBRUARY, 3)
        );
        Json elmoJson = elmo.toJson();

        System.out.println(Json.writeString(elmoJson));
    }
}
```

```json
{"name":"Elmo","birthday":"02-03"}
```

If a class you define has a JSON representation that could be considered "canonical", the interface `JsonEncodable`
can be implemented. This will let you pass an instance of the class directly to `Json.writeString` or `Json.write`.

```java
import dev.mccue.json.Json;
import dev.mccue.json.JsonEncodable;

record Muppet(String name, boolean great)
        implements JsonEncodable {
    @Override
    public Json toJson() {
        return Json.objectBuilder()
                .put("name", name)
                .put("great", great)
                .build();
    }
}

public class Main {
    public static void main(String[] args) {
        var gonzo = new Muppet("Gonzo", true);
        System.out.println(Json.writeString(gonzo));
    }
}
```

### Reading

The inverse of writing JSON is reading it.

If you have some JSON stored in a `String` you can
read it into `Json` using `Json.readString`.

```java
import dev.mccue.json.Json;

public class Main {
    public static void main(String[] args) {
        Json movie = Json.readString("""
                {
                    "title": "Treasure Island",
                    "cast": [
                        {
                            "name": "Kermit",
                            "role": "The Captain",
                            "muppet": true
                        },
                        {
                            "name": "Tim Curry",
                            "role": "Long John Silver",
                            "muppet": false
                        }
                    ]
                
                }
                """);

        System.out.println(movie);
    }
}
```

If that JSON is coming from another source, you need to obtain a `Reader` and use `Json.read`.

```java
import dev.mccue.json.Json;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws IOException {
        // If you were following along, we created this earlier!
        Json song;
        try (Reader fileReader = Files.newBufferedReader(
                Path.of("song.json"))
        ) {
            song = Json.read(fileReader);
        }

        System.out.println(song);
    }
}
```

If the JSON you provide is malformed in some way, a `JsonReadException` will be thrown.

```java
import dev.mccue.json.Json;

public class Main {
    public static void main(String[] args) {
        // Should be in quotes
        Json.readString("fozzie");
    }
}
```

```java
Exception in thread "main" dev.mccue.json.JsonReadException: JSON error (unexpected character): f
	at dev.mccue.json.JsonReadException.unexpectedCharacter(JsonReadException.java:33)
	at dev.mccue.json.internal.JsonReaderMethods.readStream(JsonReaderMethods.java:525)
	at dev.mccue.json.internal.JsonReaderMethods.read(JsonReaderMethods.java:533)
	at dev.mccue.json.internal.JsonReaderMethods.readFullyConsume(JsonReaderMethods.java:543)
	at dev.mccue.json.Json.readString(Json.java:369)
	at dev.mccue.json.Json.readString(Json.java:364)
	at dev.mccue.example.Main.main(Main.java:9)
```

### Decoding

Up to this point, everything has been more or less the same as it is for other "tree-based"
JSON libraries like [org.json](https://github.com/stleary/JSON-java) or [json-simple](https://github.com/fangyidong/json-simple).

This is where that will start to change.

To take some `Json` and turn it into a user defined class, a basic approach would be to use `instanceof` checks to see if
the `Json` is a particular subtype and navigate from there.

```java
import dev.mccue.json.*;

record Muppet(String name, boolean canSpeak) {
    static Muppet fromJson(Json json) {
        if (json instanceof JsonObject object &&
            object.get("name") instanceof JsonString name &&
            object.get("canSpeak") instanceof JsonBoolean canSpeak) {
            return new Muppet(name.toString(), canSpeak.value());
        }
        else {
            throw new RuntimeException("Invalid Muppet");
        }
    }
}

public class Main {
    public static void main(String[] args) {
        var json = Json.readString("""
                {
                    "name": "animal",
                    "canSpeak": false
                }
                """);

        var animal = Muppet.fromJson(json);

        System.out.println(animal);
    }
}
```

This process is "decoding." You "read" your data into JSON and then "decode"
it to some type you define.

The problem with the `instanceof` approach is that you will end up with bad error messages on unexpected data.
In this case the error message would just be `"Invalid Muppet"`. The code to get better errors is tedious to write
and I haven't seen many folks in the wild do it.

To get good errors, you should use the static methods defined in `JsonDecoder`.

```java
package dev.mccue.example;

import dev.mccue.json.*;

record Muppet(String name, boolean canSpeak) {
    static Muppet fromJson(Json json) {
        return new Muppet(
                JsonDecoder.field(
                        json,
                        "name", 
                        JsonDecoder::string
                ),
                JsonDecoder.field(
                        json, 
                        "canSpeak", 
                        JsonDecoder::boolean_
                )
        );
    }
}

public class Main {
    public static void main(String[] args) {
        var json = Json.readString("""
                {
                    "name": "animal",
                    "canSpeak": false
                }
                """);

        var animal = Muppet.fromJson(json);

        System.out.println(animal);
    }
}
```

These handle the fiddly process of checking whether the JSON matches the structure you
expect and throwing an appropriate error.

You should read this declaration as "at the field `name` I expect a string."

```java
JsonDecoder.field(json, "name", JsonDecoder::string)
```

If the JSON is not an object, or doesn't have a value for `name`, or that value
is not a string, you will get a `JsonDecodeException`.

```java
public class Main {
    public static void main(String[] args) {
        var json = Json.readString("""
                {
                    "canSpeak": false
                }
                """);

        var animal = JsonDecoder.field(
                json, 
                "name", 
                JsonDecoder::string
        );

        System.out.println(animal);
    }
}
```
Which will have a message indicating exactly what went wrong and where.

```java 
Problem with the value at json.name:

    {
        "canSpeak": false
    }

no value for field
```

The last argument to `JsonDecoder.field` is the `JsonDecoder` you want to use to interpret the value at that field.
In this case a method reference to `JsonDecoder.string`, which is a method that asserts JSON is a string
and throws if it isn't.

For the methods which take more than one argument, there are overloads
which can be used to get an instance of `JsonDecoder`.

```java
// This will actually decode the json into a list of strings
List<String> items = JsonDecoder.array(json, JsonDecoder::string);

// This will just return a decoder
Decoder<List<String>> decoder = 
        JsonDecoder.array(JsonDecoder::string);
```

This, in conjunction with `JsonDecoder.field` is how you are intended to explore nested paths.

```java
public class Main {
    public static void main(String[] args) {
        var json = Json.readString("""
                {
                    "villains": ["constantine", "doc hopper"]
                }
                """);

        List<String> villains = JsonDecoder.field(
                json,
                "villains",
                JsonDecoder.array(JsonDecoder::string)
        );

        System.out.println(villains);
    }
}
```

To decode JSON into your custom classes, you should add either a constructor or
a static factory method which takes in `Json` and use these decoders to make your objects.

```java
import dev.mccue.json.*;

import java.util.List;

record Actor(String name, String role, boolean muppet) {
    static Actor fromJson(Json json) {
        return new Actor(
                JsonDecoder.field(json, "name", JsonDecoder::string),
                JsonDecoder.field(json, "role", JsonDecoder::string),
                JsonDecoder.optionalField(
                        json, 
                        "muppet",
                        JsonDecoder::boolean_,
                        true
                )
        );
    }
}


record Movie(String title, List<Actor> cast) {
    static Movie fromJson(Json json) {
        return new Movie(
                JsonDecoder.field(json, "title", JsonDecoder::string),
                JsonDecoder.field(
                        json, 
                        "cast", 
                        JsonDecoder.array(Actor::fromJson)
                )
        );
    }
}

public class Main {
    public static void main(String[] args) {
        var json = Json.readString("""
                 {
                     "title": "Treasure Island",
                     "cast": [
                         {
                             "name": "Kermit",
                             "role": "The Captain"
                         },
                         {
                             "name": "Tim Curry",
                             "role": "Long John Silver",
                             "muppet": false
                         }
                     ]
                 }
                 """);

        var movie = Movie.fromJson(json);

        System.out.println(movie);
    }
}
```

### Full Round-Trip

With all of that out of the way, here is how you might define a model,
write it to json, and read it back in.

```java
import dev.mccue.json.*;

import java.util.List;

record Actor(String name, String role, boolean muppet)
    implements JsonEncodable {
    static Actor fromJson(Json json) {
        return new Actor(
                JsonDecoder.field(json, "name", JsonDecoder::string),
                JsonDecoder.field(json, "role", JsonDecoder::string),
                JsonDecoder.optionalField(
                        json,
                        "muppet",
                        JsonDecoder::boolean_,
                        true)
        );
    }

    @Override
    public Json toJson() {
        return Json.objectBuilder()
                .put("name", name)
                .put("role", role)
                .put("muppet", muppet)
                .build();
    }
}


record Movie(String title, List<Actor> cast)
    implements JsonEncodable {
    static Movie fromJson(Json json) {
        return new Movie(
                JsonDecoder.field(json, "title", JsonDecoder::string),
                JsonDecoder.field(
                        json, 
                        "cast", 
                        JsonDecoder.array(Actor::fromJson)
                )
        );
    }

    @Override
    public Json toJson() {
        return Json.objectBuilder()
                .put("title", title)
                .put("cast", cast)
                .build();
    }
}

public class Main {
    public static void main(String[] args) {
        var json = Json.readString("""
                 {
                     "title": "Treasure Island",
                     "cast": [
                         {
                             "name": "Kermit",
                             "role": "The Captain",
                             "muppet": true
                         },
                         {
                             "name": "Tim Curry",
                             "role": "Long John Silver",
                             "muppet": false
                         }
                     ]
                 }
                 """);

        var movie = Movie.fromJson(json);

        var roundTrippedJson = Json.readString(
                Json.writeString(movie.toJson())
        );
        var roundTrippedMovie = Movie.fromJson(roundTrippedJson);

        System.out.println(
                json.equals(roundTrippedJson)
        );

        System.out.println(
                movie.equals(roundTrippedMovie)
        );
    }
}
```
</details>


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
import dev.mccue.json.JsonArray;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

public class Main {
   public static void main(String[] args) {
      JsonArray numbers = JsonArray.of(
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
                    "name": "Tiny Tim",
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
      Json beaker = Json.objectBuilder()
              .put("name", "Beaker")
              .put("milliliters", 5)
              .put("scientist", true)
              .build();

      String written = Json.writeString(beaker);

      System.out.println(written);
   }
}
```

```
{"name":"Beaker","milliliters":5,"scientist":true}
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
        Json beaker = Json.objectBuilder()
                .put("name", "Beaker")
                .put("milliliters", 5)
                .put("scientist", true)
                .build();
        
        String written = Json.writeString(
                beaker,
                new JsonWriteOptions()
                        .withIndentation(4)
        );

        System.out.println(written);
    }
}
```

```
{
    "name": "Beaker",
    "milliliters": 5,
    "scientist": true
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
        Json bunsen = Json.objectBuilder()
                .put("name", "bunsen")
                .put("scientist", true)
                .build();

        try (var writer = Files.newBufferedWriter(
                Path.of("out.json")
        )) {
            Json.write(bunsen, writer);
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
import dev.mccue.json.JsonDecoder;

import java.util.List;

enum Location {
   CALIFORNIA,
   RHODE_ISLAND,
   SASKATCHEWAN,
   NEW_YORK;

   static Location fromJson(Json json) {
      return Location.valueOf(JsonDecoder.string(json));
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

### Decode json with a fixed set of keys

<details>
    <summary>Show</summary>

```java
import dev.mccue.json.Json;
import dev.mccue.json.JsonDecodeException;
import dev.mccue.json.JsonDecoder;

import java.util.HashSet;
import java.util.Set;

record Prison(String location) {
    public static Prison fromJson(Json json) {
        var object = JsonDecoder.object(json);
        var expected = Set.of("location");
        if (!expected.equals(object.keySet())) {
            var extra = new HashSet<>(object.keySet());
            extra.removeAll(expected);
            throw JsonDecodeException.of("Extra Keys: " + extra, json);
        }

        return new Prison(
                JsonDecoder.field(json, "location", JsonDecoder::string)
        );
    }
}
public class Main {
    public static void main(String[] args) {
        Json withExtraKeys = Json.readString(
                """
                        {
                            "location": "Siberia",
                            "escapeMethod": "tunnelling"
                        }
                        """
        );

        var prison = Prison.fromJson(withExtraKeys);
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
        fozzie.setHecklers(JsonDecoder.field(
                json, 
                "hecklers", 
                JsonDecoder.array(JsonDecoder::string)
        ));
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

### Decode from multiple representations

<details>
    <summary>Show</summary>

```java
import dev.mccue.json.Json;
import dev.mccue.json.JsonDecoder;

import java.util.List;

record Person(String firstName, String lastName) {
    static Person fromJsonV1(Json json) {
        var fullName = JsonDecoder.field(json, "name", JsonDecoder::string);
        var split = fullName.split(" ", 2);
        return new Person(split[0], split[1]);
    }

    static Person fromJsonV2(Json json) {
        return new Person(
                JsonDecoder.field(json, "first_name", JsonDecoder::string),
                JsonDecoder.field(json, "last_name", JsonDecoder::string)
        );
    }

    static Person fromJson(Json json) {
        return JsonDecoder.oneOf(
                json,
                Person::fromJsonV2,
                Person::fromJsonV1
        );
    }
}

public class Main {
    public static void main(String[] args) {
        Json peopleJson = Json.readString("""
                [
                    {
                        "name": "Great Gonzo"
                    },
                    {
                        "first_name": "Jim",
                        "last_name": "Henson"
                    }
                ]
                """);

        List<Person> people = JsonDecoder.array(peopleJson, Person::fromJson);

        System.out.println(people);
    }
}
```

```
[Person[firstName=Great, lastName=Gonzo], Person[firstName=Jim, lastName=Henson]]
```

</details>

### Usage from Spring

<details>
    <summary>Show</summary>

#### Step 1. Add a new jackson module as a bean

```java
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import dev.mccue.json.*;
import dev.mccue.json.stream.JsonWriteable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.UncheckedIOException;

@Configuration
public class McCueJsonModule {
    @Bean
    public Module jsonSerializer() {
        var module = new SimpleModule();
        module.addSerializer(new StdSerializer<>(JsonWriteable.class) {
            @Override
            public void serialize(
                    JsonWriteable writeable,
                    JsonGenerator jsonGenerator,
                    SerializerProvider serializerProvider
            ) throws IOException {
                try {
                    writeable.write(new ProxyWriter(jsonGenerator));
                } catch (UncheckedIOException e) {
                    throw e.getCause();
                }
            }
        });
        module.addDeserializer(Json.class, new StdDeserializer<>(Json.class) {
            private Json deserializeTree(JsonNode tree) {
                if (tree.isTextual()) {
                    return JsonString.of(tree.textValue());
                }
                else if (tree.isNull()) {
                    return JsonNull.instance();
                }
                else if (tree.isBoolean()) {
                    return JsonBoolean.of(tree.booleanValue());
                }
                else if (tree.isLong()) {
                    return JsonNumber.of(tree.longValue());
                }
                else if (tree.isDouble()) {
                    return JsonNumber.of(tree.doubleValue());
                }
                else if (tree.isBigDecimal()) {
                    return JsonNumber.of(tree.decimalValue());
                }
                else if (tree.isBigInteger()) {
                    return JsonNumber.of(tree.bigIntegerValue());
                }
                else if (tree.isArray()) {
                    var arrayBuilder = JsonArray.builder();
                    for (var value : tree) {
                        arrayBuilder.add(deserializeTree(value));
                    }
                    return arrayBuilder.build();
                }
                else if (tree.isObject()) {
                    var objectBuilder = JsonObject.builder();
                    var fieldNamesIter = tree.fieldNames();
                    while (fieldNamesIter.hasNext()) {
                        var fieldName = fieldNamesIter.next();
                        objectBuilder.put(fieldName, deserializeTree(tree.get(fieldName)));
                    }
                    return objectBuilder.build();
                }
                else {
                    throw new IllegalStateException("Should have handled all JsonNode types?");
                }
            }

            @Override
            public Json deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
                    throws IOException {
                return deserializeTree(jsonParser.readValueAsTree());
            }
        });
        return module;
    }

    private record ProxyWriter(JsonGenerator jsonGenerator)
            implements dev.mccue.json.stream.JsonGenerator {
        @Override
        public void writeObjectStart() {
            try {
                jsonGenerator.writeStartObject();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        @Override
        public void writeObjectEnd() {
            try {
                jsonGenerator.writeEndObject();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        @Override
        public void writeArrayStart() {
            try {
                jsonGenerator.writeStartArray();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        @Override
        public void writeArrayEnd() {
            try {
                jsonGenerator.writeEndArray();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        @Override
        public void writeFieldName(String s) {
            try {
                jsonGenerator.writeFieldName(s);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        @Override
        public void writeString(String s) {
            try {
                jsonGenerator.writeString(s);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        @Override
        public void writeNumber(JsonNumber jsonNumber) {
            try {
                jsonGenerator.writeNumber(jsonNumber.toString());
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        @Override
        public void writeTrue() {
            try {
                jsonGenerator.writeBoolean(true);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        @Override
        public void writeFalse() {
            try {
                jsonGenerator.writeBoolean(false);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        @Override
        public void writeNull() {
            try {
                jsonGenerator.writeNull();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }
}
```

#### Step 2. Annotate your creation methods with @JsonCreator

```java
import com.fasterxml.jackson.annotation.JsonCreator;
import dev.mccue.json.Json;
import dev.mccue.json.JsonDecoder;
import dev.mccue.json.JsonEncodable;

public record Muppet(String name) implements JsonEncodable {

    @Override
    public Json toJson() {
        return Json.objectBuilder()
                .put("name", name)
                .build();
    }

    @JsonCreator
    public static Muppet fromJson(Json json) {
        return new Muppet(JsonDecoder.field(json, "name", JsonDecoder::string));
    }
}
```


</details>

### Encode and Decode with Kotlin 

<details>
    <summary>Show</summary>

```kotlin
import dev.mccue.json.Json
import dev.mccue.json.JsonDecoder
import dev.mccue.json.JsonEncodable
import dev.mccue.json.JsonWriteOptions

data class Muppet(
   val name: String,
   val scientist: Boolean,
   val lines: String?
) : JsonEncodable {
   override fun toJson(): Json =
      Json.objectBuilder()
         .put("name", name)
         .put("scientist", scientist)
         .put("lines", lines)
         .build()

   companion object {
      fun fromJson(json: Json): Muppet =
         Muppet(
            JsonDecoder.field(json, "name") { JsonDecoder.string(it) },
            JsonDecoder.field(json, "scientist") { JsonDecoder.boolean_(it) },
            JsonDecoder.nullableField(json,
               "lines",
               { JsonDecoder.string(it) },
               null
            )
         )
   }
}

data class Movie(
   val title: String,
   val muppets: List<Muppet>
) : JsonEncodable {
   override fun toJson(): Json {
      return Json.objectBuilder()
         .put("title", title)
         .put("muppets", muppets)
         .build()
   }

   companion object {
      fun fromJson(json: Json): Movie =
         Movie(
            JsonDecoder.field(json, "title") { JsonDecoder.string(it) },
            JsonDecoder.field(json, "muppets", JsonDecoder.array { Muppet.fromJson(it) })
         )
   }
}


fun main(args: Array<String>) {
   val movie = Movie(
      "Most wanted",
      listOf(
         Muppet(
            "kermit",
            false,
            "I'm not Constantine!"
         ),
         Muppet(
            "beaker",
            true,
            null
         ),
         Muppet(
            "bunsen",
            true,
            "I don't mean to be a stickler"
         )
      )
   )

   println(Json.writeString(movie, JsonWriteOptions().withIndentation(4)))

   val movieRoundTripped = Movie.fromJson(Json.readString(Json.writeString(movie)))

   println(movieRoundTripped)
   println(movie)
   println(movie == movieRoundTripped)
}
```

```
{
    "title": "Most wanted",
    "muppets": [
        {
            "name": "kermit",
            "scientist": false,
            "lines": "I'm not Constantine!"
        },
        {
            "name": "beaker",
            "scientist": true,
            "lines": null
        },
        {
            "name": "bunsen",
            "scientist": true,
            "lines": "I don't mean to be a stickler"
        }
    ]
}
Movie(title=Most wanted, muppets=[Muppet(name=kermit, scientist=false, lines=I'm not Constantine!), Muppet(name=beaker, scientist=true, lines=null), Muppet(name=bunsen, scientist=true, lines=I don't mean to be a stickler)])
Movie(title=Most wanted, muppets=[Muppet(name=kermit, scientist=false, lines=I'm not Constantine!), Muppet(name=beaker, scientist=true, lines=null), Muppet(name=bunsen, scientist=true, lines=I don't mean to be a stickler)])
true
```

</details>

### Encode and Decode with Scala 3

<details>
    <summary>Show</summary>

```scala
import dev.mccue.json.{Json, JsonDecoder, JsonEncodable, JsonWriteOptions}

import scala.jdk.CollectionConverters._

case class Muppet(name: String, scientist: Boolean, lines: Option[String]) extends JsonEncodable {
  override def toJson: Json =
    Json.objectBuilder()
      .put("name", name)
      .put("scientist", scientist)
      .put("lines", lines.orNull)
      .build
}

object Muppet {
  def fromJson(json: Json): Muppet =
    Muppet(
      JsonDecoder.field(json, "name", JsonDecoder.string _),
      JsonDecoder.field(json, "scientist", JsonDecoder.boolean_ _),
      JsonDecoder.nullableField(json, "name", JsonDecoder.string _)
        .map(Option(_))
        .orElse(None)
    )
}

case class Movie(title: String, muppets: Seq[Muppet]) extends JsonEncodable {
  override def toJson: Json =
    Json.objectBuilder()
      .put("title", title)
      .put("muppets", muppets.asJava)
      .build()
}

object Movie {
  def fromJson(json: Json): Movie =
    Movie(
      JsonDecoder.field(json, "title", JsonDecoder.string _),
      JsonDecoder.field(json, "muppets", JsonDecoder.array(Muppet.fromJson _))
        .asScala
        .toSeq
    )
}


@main
def main(): Unit = {
  val movie = Movie(
    "Most wanted",
    Seq(
      Muppet(
        "kermit",
        false,
        Some("I'm not Constantine!")
      ),
      Muppet(
        "beaker",
        true,
        None
      ),
      Muppet(
        "bunsen",
        true,
        Some("I don't mean to be a stickler")
      )
    )
  )

  println(Json.writeString(movie, JsonWriteOptions().withIndentation(4)))

  val movieRoundTripped = Movie.fromJson(Json.readString(Json.writeString(movie)))

  println(movieRoundTripped)
  println(movie)
  println(movie == movieRoundTripped)
}
```

``` 
{
    "title": "Most wanted",
    "muppets": [
        {
            "name": "kermit",
            "scientist": false,
            "lines": "I'm not Constantine!"
        },
        {
            "name": "beaker",
            "scientist": true,
            "lines": null
        },
        {
            "name": "bunsen",
            "scientist": true,
            "lines": "I don't mean to be a stickler"
        }
    ]
}
Movie(Most wanted,List(Muppet(kermit,false,Some(kermit)), Muppet(beaker,true,Some(beaker)), Muppet(bunsen,true,Some(bunsen))))
Movie(Most wanted,List(Muppet(kermit,false,Some(I'm not Constantine!)), Muppet(beaker,true,None), Muppet(bunsen,true,Some(I don't mean to be a stickler))))
false
```
</details>

### Decode into sealed trait with Scala 3

<details>
    <summary>Show</summary>

```scala
import dev.mccue.json.{Json, JsonDecodeException, JsonDecoder, JsonEncodable, JsonWriteOptions}

import scala.jdk.CollectionConverters.*

sealed trait MessageBody {
  def messageId: Int
}

object MessageBody {
  def fromJson(json: Json): MessageBody = {
    val id = JsonDecoder.field(json, "type", JsonDecoder.string _)
    id match
      case "init" =>
        Init(
          JsonDecoder.field(json, "msg_id", JsonDecoder.int_ _),
          JsonDecoder.field(json, "node_id", JsonDecoder.string _),
          JsonDecoder.field(json, "node_ids", JsonDecoder.array(JsonDecoder.string _))
            .asScala
            .toSeq
        )
      case "init_ok" =>
        InitOk(
          JsonDecoder.field(json, "msg_id", JsonDecoder.int_ _),
          JsonDecoder.field(json, "in_reply_to", JsonDecoder.int_ _)
        )
      case _ =>
        throw JsonDecodeException.atField("type", JsonDecodeException.of(
          "expected one of \"init\", \"init_ok\"",
          json
        ))
  }
}

case class Init(messageId: Int, nodeId: String, nodeIds: Seq[String]) extends MessageBody {}
case class InitOk(messageId: Int, inReplyTo: Int) extends MessageBody {}

case class Envelope(src: Option[String], dest: Option[String], body: MessageBody) {}

object Envelope {
  def fromJson(json: Json): Envelope = {
    Envelope(
      JsonDecoder.optionalField(json, "src", JsonDecoder.string _)
        .map(Option(_))
        .orElse(None),

      JsonDecoder.optionalField(json, "dest", JsonDecoder.string _)
        .map(Option(_))
        .orElse(None),

      JsonDecoder.field(json, "body", MessageBody.fromJson _)
    )
  }
}
```

</details>