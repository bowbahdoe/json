package dev.mccue.json.stream;

import dev.mccue.json.Json;

public interface JsonArrayHandler extends JsonValueHandler {
    void onArrayEnd();
}