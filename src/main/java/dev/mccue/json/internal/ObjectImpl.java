package dev.mccue.json.internal;

import dev.mccue.json.*;
import dev.mccue.json.serialization.JsonSerializationProxy;
import dev.mccue.json.stream.JsonGenerator;

import java.io.Serial;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

@ValueCandidate
public record ObjectImpl(
        @InternalInvariant({
                "Must be non-null and no value within can be null.",
                "No key within can be null.",
                "No value within can be null.",
                "Must be either deeply immutable or fully owned by this class.",
                "Must be unmodifiable.",
        })
        Map<java.lang.String, Json> value
) implements JsonObject {
    public static final JsonObject EMPTY = new ObjectImpl(Map.of());

    public ObjectImpl(Map<java.lang.String, Json> value) {
        this.value = value;
    }

    @Override
    public int size() {
        return this.value.size();
    }

    @Override
    public boolean isEmpty() {
        return this.value.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return this.value.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.value.containsValue(value);
    }

    @Override
    public Json get(Object key) {
        return this.value.get(key);
    }

    @Override
    public Json put(java.lang.String key, Json value) {
        return this.value.put(key, value);
    }

    @Override
    public Json remove(Object key) {
        return this.value.remove(key);
    }

    @Override
    public void putAll(Map<? extends java.lang.String, ? extends Json> m) {
        this.value.putAll(m);
    }

    @Override
    public void clear() {
        this.value.clear();
    }

    @Override
    public Set<java.lang.String> keySet() {
        return this.value.keySet();
    }

    @Override
    public Collection<Json> values() {
        return this.value.values();
    }

    @Override
    public Set<Entry<java.lang.String, Json>> entrySet() {
        return this.value.entrySet();
    }

    @Override
    public Json getOrDefault(Object key, Json defaultValue) {
        return this.value.getOrDefault(key, defaultValue);
    }

    @Override
    public void forEach(BiConsumer<? super java.lang.String, ? super Json> action) {
        this.value.forEach(action);
    }

    @Override
    public void replaceAll(BiFunction<? super java.lang.String, ? super Json, ? extends Json> function) {
        this.value.replaceAll(function);
    }

    @Override
    public Json putIfAbsent(java.lang.String key, Json value) {
        return this.value.putIfAbsent(key, value);
    }

    @Override
    public boolean remove(Object key, java.lang.Object value) {
        return this.value.remove(key, value);
    }

    @Override
    public boolean replace(java.lang.String key, Json oldValue, Json newValue) {
        return this.value.replace(key, oldValue, newValue);
    }

    @Override
    public Json replace(java.lang.String key, Json value) {
        return this.value.replace(key, value);
    }

    @Override
    public Json computeIfAbsent(java.lang.String key, Function<? super java.lang.String, ? extends Json> mappingFunction) {
        return this.value.computeIfAbsent(key, mappingFunction);
    }

    @Override
    public Json computeIfPresent(java.lang.String key, BiFunction<? super java.lang.String, ? super Json, ? extends Json> remappingFunction) {
        return this.value.computeIfPresent(key, remappingFunction);
    }

    @Override
    public Json compute(java.lang.String key, BiFunction<? super java.lang.String, ? super Json, ? extends Json> remappingFunction) {
        return this.value.compute(key, remappingFunction);
    }

    @Override
    public Json merge(java.lang.String key, Json value, BiFunction<? super Json, ? super Json, ? extends Json> remappingFunction) {
        return this.value.merge(key, value, remappingFunction);
    }

    @Override
    public java.lang.String toString() {
        return Json.writeString(this);
    }

    @Serial
    private Object writeReplace() {
        return new JsonSerializationProxy(Json.writeString(this));
    }

    @Override
    public void write(JsonGenerator generator) {
        generator.writeObjectStart();
        this.forEach((k, v) -> {
            generator.writeFieldName(k);
            v.write(generator);
        });
        generator.writeObjectEnd();
    }
}
