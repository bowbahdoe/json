package dev.mccue.json.internal;

import dev.mccue.json.*;
import dev.mccue.json.internal.ArrayBuilderImpl;
import dev.mccue.json.internal.ObjectBuilder;
import dev.mccue.json.internal.ValueCandidate;
import dev.mccue.json.stream.JsonArrayHandler;
import dev.mccue.json.stream.JsonObjectHandler;
import dev.mccue.json.stream.JsonValueHandler;

import java.util.function.Consumer;

final class Handlers {
    private Handlers() {}

    @ValueCandidate
    record TreeObjectHandler(JsonObject.Builder builder, Consumer<JsonObject> onObject) implements JsonObjectHandler {
        TreeObjectHandler(Consumer<JsonObject> onObject) {
            this(Json.objectBuilder(), onObject);
        }

        @Override
        public JsonValueHandler onField(java.lang.String fieldName) {
            return new TreeValueHandler(value -> builder.put(fieldName, value));
        }

        @Override
        public void onObjectEnd() {
            onObject.accept(((ObjectBuilder) builder).buildInternal());
        }
    }

    @ValueCandidate
    record TreeArrayHandler(
            JsonArray.Builder builder,
            Consumer<JsonArray> onArray
    ) implements JsonArrayHandler {
        TreeArrayHandler(Consumer<JsonArray> onArray) {
            this(Json.arrayBuilder(), onArray);
        }

        @Override
        public void onArrayEnd() {
            onArray.accept(((ArrayBuilderImpl) builder).buildInternal());
        }

        @Override
        public JsonObjectHandler onObjectStart() {
            return new TreeObjectHandler(builder::add);
        }

        @Override
        public JsonArrayHandler onArrayStart() {
            return new TreeArrayHandler(builder::add);
        }

        @Override
        public void onNumber(JsonNumber number) {
            builder.add(number);
        }

        @Override
        public void onString(java.lang.String value) {
            builder.add(JsonString.of(value));
        }

        @Override
        public void onNull() {
            builder.add(Json.ofNull());
        }

        @Override
        public void onTrue() {
            builder.add(Json.ofTrue());
        }

        @Override
        public void onFalse() {
            builder.add(Json.ofFalse());
        }
    }

    /**
     * Basic handler that reads values into an immutable tree.
     */
    @ValueCandidate
    record TreeValueHandler(Consumer<Json> onValue) implements JsonValueHandler {

        @Override
        public JsonObjectHandler onObjectStart() {
            return new TreeObjectHandler(onValue::accept);
        }

        @Override
        public JsonArrayHandler onArrayStart() {
            return new TreeArrayHandler(onValue::accept);
        }

        @Override
        public void onNumber(JsonNumber number) {
            onValue.accept(number);
        }

        @Override
        public void onString(String value) {
            onValue.accept(JsonString.of(value));
        }

        @Override
        public void onNull() {
            onValue.accept(Json.ofNull());
        }

        @Override
        public void onTrue() {
            onValue.accept(Json.ofTrue());
        }

        @Override
        public void onFalse() {
            onValue.accept(Json.ofFalse());
        }
    }

    static final class BaseTreeValueHandler implements JsonValueHandler {
        Json result;
        final TreeValueHandler delegate;

        BaseTreeValueHandler() {
            this.result = null;
            this.delegate = new TreeValueHandler(value -> this.result = value);
        }

        @Override
        public JsonObjectHandler onObjectStart() {
            return delegate.onObjectStart();
        }

        @Override
        public JsonArrayHandler onArrayStart() {
            return delegate.onArrayStart();
        }

        @Override
        public void onNumber(JsonNumber number) {
            delegate.onNumber(number);
        }

        @Override
        public void onString(java.lang.String value) {
            delegate.onString(value);
        }

        @Override
        public void onNull() {
            delegate.onNull();
        }

        @Override
        public void onTrue() {
            delegate.onTrue();
        }

        @Override
        public void onFalse() {
            delegate.onFalse();
        }
    }
}
