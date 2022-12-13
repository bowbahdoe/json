package dev.mccue.json.stream;

public interface JsonObjectHandler {
    JsonValueHandler onField(java.lang.String fieldName);

    void objectEnd();
}
