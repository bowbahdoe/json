package dev.mccue.json.stream;

import dev.mccue.json.Json;
import dev.mccue.json.JsonNumber;

public interface JsonValueHandler {
    JsonObjectHandler onObjectStart();

    JsonArrayHandler onArrayStart();

    void onNumber(JsonNumber number);

    void onString(String value);

    void onNull();

    void onTrue();

    void onFalse();
}