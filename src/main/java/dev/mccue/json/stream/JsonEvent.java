package dev.mccue.json.stream;

import dev.mccue.json.JsonNumber;
import dev.mccue.json.internal.ValueCandidate;

@ValueCandidate
public sealed interface JsonEvent {
    @ValueCandidate
    record ObjectStart() implements JsonEvent {}

    @ValueCandidate
    record ObjectEnd() implements JsonEvent {}

    @ValueCandidate
    record ArrayStart() implements JsonEvent {}

    @ValueCandidate
    record ArrayEnd() implements JsonEvent {}

    @ValueCandidate
    record Number(JsonNumber value) implements JsonEvent {}

    @ValueCandidate
    record String(java.lang.String value) implements JsonEvent {}

    @ValueCandidate
    record Null() implements JsonEvent {}

    @ValueCandidate
    record True() implements JsonEvent {}

    @ValueCandidate
    record False() implements JsonEvent {}

    @ValueCandidate
    record Field(java.lang.String name) implements JsonEvent {}
}
