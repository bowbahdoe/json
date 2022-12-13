package dev.mccue.json;

import dev.mccue.json.internal.ValueBased;
import dev.mccue.json.stream.JsonArrayHandler;
import dev.mccue.json.stream.JsonObjectHandler;
import dev.mccue.json.stream.JsonValueHandler;

import java.util.function.Consumer;

final class Handlers {
    private Handlers() {}

    @ValueBased
    record TreeObjectHandler(Json.Object.Builder builder, Consumer<Json.Object> onObject) implements JsonObjectHandler {
        TreeObjectHandler(Consumer<Json.Object> onObject) {
            this(Json.objectBuilder(), onObject);
        }

        @Override
        public JsonValueHandler onField(java.lang.String fieldName) {
            return new TreeValueHandler(value -> builder.put(fieldName, value));
        }

        @Override
        public void objectEnd() {
            onObject.accept(builder.build());
        }
    }

    @ValueBased
    record TreeArrayHandler(Json.Array.Builder builder, Consumer<Json.Array> onArray) implements JsonArrayHandler {
        TreeArrayHandler(Consumer<Json.Array> onArray) {
            this(Json.arrayBuilder(), onArray);
        }

        @Override
        public void onArrayEnd() {
            onArray.accept(builder.build());
        }

        @Override
        public JsonObjectHandler onObjectStart() {
            return null;
        }

        @Override
        public JsonArrayHandler onArrayStart() {
            return new TreeArrayHandler(builder::add);
        }

        @Override
        public void onNumber(Json.Number number) {
            builder.add(number);
        }

        @Override
        public void onString(java.lang.String value) {
            builder.add(Json.String.of(value));
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
    @ValueBased
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
        public void onNumber(Json.Number number) {
            onValue.accept(number);
        }

        @Override
        public void onString(java.lang.String value) {
            onValue.accept(Json.String.of(value));
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

        BaseTreeValueHandler(Json.EOFBehavior eofBehavior) {
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
        public void onNumber(Json.Number number) {
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
