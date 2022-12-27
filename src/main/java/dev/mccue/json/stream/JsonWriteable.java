package dev.mccue.json.stream;

import dev.mccue.json.stream.JsonGenerator;

/**
 * Indicates that the marked class can be written in a streaming
 * fashion to a JsonGenerator.
 */
public interface JsonWriteable {
    void write(JsonGenerator generator);
}
