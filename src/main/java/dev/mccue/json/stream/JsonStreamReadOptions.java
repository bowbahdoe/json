package dev.mccue.json.stream;

public record JsonStreamReadOptions(
        boolean useBigDecimals
) {
    public JsonStreamReadOptions() {
        this(false);
    }

    public JsonStreamReadOptions withUseBigDecimals(boolean useBigDecimals) {
        return new JsonStreamReadOptions(useBigDecimals);
    }
}




