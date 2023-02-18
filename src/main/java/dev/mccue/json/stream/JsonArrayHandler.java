package dev.mccue.json.stream;

public interface JsonArrayHandler extends JsonValueHandler {
    void onArrayEnd();
}