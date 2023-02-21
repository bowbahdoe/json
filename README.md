# json

<img src="./bopbop.png"></img>

A Java JSON Library intended to be easy to learn and simple to teach.

Requires Java 17+.

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

### Goals

### Non-Goals


## Usage

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

##