package dev.mccue.json;

public interface JsonGenerator {
    void emit(Json.Event event);

    default void emitObjectStart() {
        emit(new Json.Event.ObjectStart());
    }

    default void emitObjectEnd() {
        emit(new Json.Event.ObjectEnd());
    }

    default void emitArrayStart() {
        emit(new Json.Event.ArrayStart());
    }

    default void emitFieldName(String value) {
        emit(new Json.Event.FieldName(value));
    }

    default void emitArrayEnd() {
        emit(new Json.Event.ArrayEnd());
    }

    default void emitNull() {
        emit(new Json.Event.Null());
    }

    default void emitString(String value) {
        emit(new Json.Event.String(value));
    }

    default void emitNumber(Json.Number value) {
        emit(new Json.Event.Number(value));
    }

    default void emitTrue() {
        emit(new Json.Event.True());
    }

    default void emitFalse() {
        emit(new Json.Event.False());
    }
}
