package dev.mccue.json.stream;

/**
 * Indicates that the marked class can be written in a streaming
 * fashion to a JsonGenerator.
 */
public interface JsonWriteable {
    /**
     * Writes this object to the given generator.
     * @param generator The generator to write to.
     */
    void write(JsonGenerator generator);
}
