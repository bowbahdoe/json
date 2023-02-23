package dev.mccue.json.stream;


public interface JsonObjectHandler {
    JsonValueHandler onField(String fieldName);

    void onObjectEnd();
}

