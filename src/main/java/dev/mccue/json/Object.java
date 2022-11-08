package dev.mccue.json;

import java.io.StringWriter;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

record Object(Map<java.lang.String, Json> value) implements Json.Object {
    Object(Map<java.lang.String, Json> value) {
        Objects.requireNonNull(value, "Json.Object value must be nonnull");
        this.value = value
                .entrySet()
                .stream()
                .collect(Collectors.toUnmodifiableMap(
                        entry -> {
                            Objects.requireNonNull(entry.getKey(), "Json.Object cannot have null keys");
                            return entry.getKey();
                        },
                        entry -> entry.getValue() == null
                                ? Json.Null.instance()
                                : entry.getValue()
                ));
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
    public boolean containsKey(java.lang.Object key) {
        return this.value.containsKey(key);
    }

    @Override
    public boolean containsValue(java.lang.Object value) {
        return this.value.containsValue(value);
    }

    @Override
    public Json get(java.lang.Object key) {
        return this.value.get(key);
    }

    @Override
    public Json put(java.lang.String key, Json value) {
        return this.value.put(key, value);
    }

    @Override
    public Json remove(java.lang.Object key) {
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
    public Json getOrDefault(java.lang.Object key, Json defaultValue) {
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
    public boolean remove(java.lang.Object key, java.lang.Object value) {
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
}
