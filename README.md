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
    <scope>provided</scope>
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

## Reading and Writing



### Read from a String

```java

```

### Write to a String


## Encoding and Decoding


### Full Example

```java 
record Dog(
        String name,
        int age
) {
    static Dog fromJson(Json json) {
        return new Dog(
                JsonDecoder.field(json, "name", JsonDecoder::string),
                JsonDecoder.field(json, "age", JsonDecoder::int_)
        );
    }
}

public class Main {
    public static void main(String[] args) {
        var jsonString = """
            {
                "name": "Bop Bop",
                "age": 1
            }
            """;

        var json = Json.readString(jsonString);
        var bopBop = Dog.fromJson(json);

        System.out.println(bopBop);
    }
}
```
