package dev.mccue.json.internal;

import dev.mccue.json.*;
import dev.mccue.json.serialization.JsonSerializationProxy;
import dev.mccue.json.stream.JsonGenerator;

import java.io.Serial;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

@ValueCandidate
public record ArrayImpl(
        @InternalInvariant({
                "Must be non-null and no value within can be null.",
                "No value within can be null.",
                "Must be either deeply immutable or fully owned by this class.",
                "Must be unmodifiable.",
        })
        List<Json> value
) implements JsonArray {
    public static final JsonArray EMPTY = new ArrayImpl(List.of());

    @Override
    public int size() {
        return this.value.size();
    }

    @Override
    public boolean isEmpty() {
        return this.value.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return this.value.contains(o);
    }

    @Override
    public Iterator<Json> iterator() {
        return this.value.iterator();
    }

    @Override
    public void forEach(Consumer<? super Json> action) {
        this.value.forEach(action);
    }

    @Override
    public Object[] toArray() {
        return this.value.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return this.value.toArray(a);
    }

    @Override
    public <T> T[] toArray(IntFunction<T[]> generator) {
        return this.value.toArray(generator);
    }

    @Override
    public boolean add(Json json) {
        return this.value.add(json);
    }

    @Override
    public boolean remove(Object o) {
        return this.value.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return this.value.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends Json> c) {
        return this.value.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends Json> c) {
        return this.value.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return this.value.removeAll(c);
    }

    @Override
    public boolean removeIf(Predicate<? super Json> filter) {
        return this.value.removeIf(filter);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return this.value.retainAll(c);
    }

    @Override
    public void replaceAll(UnaryOperator<Json> operator) {
        this.value.replaceAll(operator);
    }

    @Override
    public void sort(Comparator<? super Json> c) {
        this.value.sort(c);
    }

    @Override
    public void clear() {
        this.value.clear();
    }

    @Override
    public Json get(int index) {
        return this.value.get(index);
    }

    @Override
    public Json set(int index, Json element) {
        return this.value.set(index, element);
    }

    @Override
    public void add(int index, Json element) {
        this.value.add(index, element);
    }

    @Override
    public Json remove(int index) {
        return this.value.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return this.value.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return this.value.lastIndexOf(o);
    }

    @Override
    public ListIterator<Json> listIterator() {
        return this.value.listIterator();
    }

    @Override
    public ListIterator<Json> listIterator(int index) {
        return this.value.listIterator(index);
    }

    @Override
    public List<Json> subList(int fromIndex, int toIndex) {
        return this.value.subList(fromIndex, toIndex);
    }

    @Override
    public Spliterator<Json> spliterator() {
        return this.value.spliterator();
    }

    @Override
    public Stream<Json> stream() {
        return this.value.stream();
    }

    @Override
    public Stream<Json> parallelStream() {
        return this.value.parallelStream();
    }

    @Override
    public java.lang.String toString() {
        return Json.writeString(this);
    }

    @Serial
    private Object writeReplace() {
        return new JsonSerializationProxy(Json.writeString(this));
    }

    @Serial
    private Object readResolve() {
        throw new IllegalStateException();
    }

    @Override
    public void write(JsonGenerator generator) {
        generator.writeArrayStart();
        this.forEach(json -> json.write(generator));
        generator.writeArrayEnd();
    }
}
