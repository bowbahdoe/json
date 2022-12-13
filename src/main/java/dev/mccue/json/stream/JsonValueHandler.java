package dev.mccue.json.stream;

import dev.mccue.json.Json;

public interface JsonValueHandler {
    JsonObjectHandler onObjectStart();

    JsonArrayHandler onArrayStart();

    void onNumber(Json.Number number);

    void onString(java.lang.String value);

    void onNull();

    void onTrue();

    void onFalse();
}
