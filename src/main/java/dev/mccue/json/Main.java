package dev.mccue.json;

import java.math.BigDecimal;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        System.out.println(
                Json.Array.builder()
                        .add(Json.String.of("ABC"))
                        .add(Json.Object.of(Map.of(
                                Json.String.of("AAA"), Json.Number.of(BigDecimal.valueOf(123))
                        )))
                        .add(Json.Null.instance())
                        .build()
        );
    }
}
