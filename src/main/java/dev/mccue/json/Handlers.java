package dev.mccue.json;

import dev.mccue.json.internal.ValueBased;

import java.util.function.Consumer;

final class Handlers {
    private Handlers() {}

    @ValueBased
    record TreeObjectHandler(Json.Object.Builder builder, Consumer<Json.Object> onObject) implements Json.ObjectHandler {
        TreeObjectHandler(Consumer<Json.Object> onObject) {
            this(Json.objectBuilder(), onObject);
        }

        @Override
        public Json.ValueHandler onField(java.lang.String fieldName) {
            return new TreeValueHandler(value -> builder.put(fieldName, value));
        }

        @Override
        public void objectEnd() {
            onObject.accept(((ObjectBuilder) builder).buildInternal());
        }
    }

    @ValueBased
    record TreeArrayHandler(
            Json.Array.Builder builder,
            Consumer<Json.Array> onArray
    ) implements Json.ArrayHandler {
        TreeArrayHandler(Consumer<Json.Array> onArray) {
            this(Json.arrayBuilder(), onArray);
        }

        @Override
        public void onArrayEnd() {
            onArray.accept(((ArrayBuilder) builder).buildInternal());
        }

        @Override
        public Json.ObjectHandler onObjectStart() {
            return new TreeObjectHandler(builder::add);
        }

        @Override
        public Json.ArrayHandler onArrayStart() {
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
    record TreeValueHandler(Consumer<Json> onValue) implements Json.ValueHandler {

        @Override
        public Json.ObjectHandler onObjectStart() {
            return new TreeObjectHandler(onValue::accept);
        }

        @Override
        public Json.ArrayHandler onArrayStart() {
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

    static final class BaseTreeValueHandler implements Json.ValueHandler {
        Json result;
        final TreeValueHandler delegate;

        BaseTreeValueHandler() {
            this.result = null;
            this.delegate = new TreeValueHandler(value -> this.result = value);
        }

        @Override
        public Json.ObjectHandler onObjectStart() {
            return delegate.onObjectStart();
        }

        @Override
        public Json.ArrayHandler onArrayStart() {
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
