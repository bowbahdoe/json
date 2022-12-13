package dev.mccue.json.event;

import dev.mccue.json.Json;
import dev.mccue.json.internal.ValueBased;

@ValueBased
public sealed interface JsonEvent {
    @ValueBased
    record ObjectStart() implements JsonEvent {
    }

    @ValueBased
    record ObjectEnd() implements JsonEvent {
    }

    @ValueBased
    record FieldName(String name) implements JsonEvent {
    }

    @ValueBased
    record ArrayStart() implements JsonEvent {
    }

    @ValueBased
    record ArrayEnd() implements JsonEvent {
    }

    @ValueBased
    record Number(Json.Number value) implements JsonEvent {
    }

    @ValueBased
    record String(java.lang.String value) implements JsonEvent {
    }

    @ValueBased
    record True() implements JsonEvent {
    }

    @ValueBased
    record False() implements JsonEvent {
    }

    @ValueBased
    record Null() implements JsonEvent {
    }
}
