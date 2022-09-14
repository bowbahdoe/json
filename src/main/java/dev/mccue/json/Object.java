package dev.mccue.json;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

record Object(Map<String, Json> value) implements Json.Object {
    Object(Map<String, Json> value) {
        Objects.requireNonNull(value, "Json.Table value must be nonnull");
        this.value = Map.copyOf(value);
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
    public Json put(String key, Json value) {
        return this.value.put(key, value);
    }

    @Override
    public Json remove(java.lang.Object key) {
        return this.value.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ? extends Json> m) {
        this.value.putAll(m);
    }

    @Override
    public void clear() {
        this.value.clear();
    }

    @Override
    public Set<String> keySet() {
        return this.value.keySet();
    }

    @Override
    public Collection<Json> values() {
        return this.value.values();
    }

    @Override
    public Set<Entry<String, Json>> entrySet() {
        return this.value.entrySet();
    }

    @Override
    public Json getOrDefault(java.lang.Object key, Json defaultValue) {
        return this.value.getOrDefault(key, defaultValue);
    }

    @Override
    public void forEach(BiConsumer<? super String, ? super Json> action) {
        this.value.forEach(action);
    }

    @Override
    public void replaceAll(BiFunction<? super String, ? super Json, ? extends Json> function) {
        this.value.replaceAll(function);
    }

    @Override
    public Json putIfAbsent(String key, Json value) {
        return this.value.putIfAbsent(key, value);
    }

    @Override
    public boolean remove(java.lang.Object key, java.lang.Object value) {
        return this.value.remove(key, value);
    }

    @Override
    public boolean replace(String key, Json oldValue, Json newValue) {
        return this.value.replace(key, oldValue, newValue);
    }

    @Override
    public Json replace(String key, Json value) {
        return this.value.replace(key, value);
    }

    @Override
    public Json computeIfAbsent(String key, Function<? super String, ? extends Json> mappingFunction) {
        return this.value.computeIfAbsent(key, mappingFunction);
    }

    @Override
    public Json computeIfPresent(String key, BiFunction<? super String, ? super Json, ? extends Json> remappingFunction) {
        return this.value.computeIfPresent(key, remappingFunction);
    }

    @Override
    public Json compute(String key, BiFunction<? super String, ? super Json, ? extends Json> remappingFunction) {
        return this.value.compute(key, remappingFunction);
    }

    @Override
    public Json merge(String key, Json value, BiFunction<? super Json, ? super Json, ? extends Json> remappingFunction) {
        return this.value.merge(key, value, remappingFunction);
    }
}
