package dev.mccue.json;

import java.io.StringWriter;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

record Array(List<Json> value) implements Json.Array {
    Array(List<Json> value) {
        Objects.requireNonNull(value, "Json.Array value must be nonnull");
        value.forEach(json -> Objects.requireNonNull(json, "Each value in a Json.Array must be nonnull"));
        this.value = List.copyOf(value);
    }

    @Override
    public List<Json> value() {
        return Collections.unmodifiableList(this.value);
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
    public boolean contains(java.lang.Object o) {
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
    public java.lang.Object[] toArray() {
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
    public boolean remove(java.lang.Object o) {
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
    public int indexOf(java.lang.Object o) {
        return this.value.indexOf(o);
    }

    @Override
    public int lastIndexOf(java.lang.Object o) {
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
}
