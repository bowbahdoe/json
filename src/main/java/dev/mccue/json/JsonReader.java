package dev.mccue.json;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface JsonReader extends Iterable<Json> {
    Json read();

    default Stream<Json> stream() {
        var spliterator = Spliterators.spliteratorUnknownSize(
                this.iterator(),
                Spliterator.NONNULL & Spliterator.ORDERED
        );

        return StreamSupport.stream( spliterator, false);
    }

    @Override
    default Iterator<Json> iterator() {
        var self = this;
        return new Iterator<>() {
            boolean done = false;
            Json next = null;

            @Override
            public boolean hasNext() {
                if (!done) {
                    next = self.read();
                    if (next == null) {
                        done = true;
                    }
                }

                return !done;
            }

            @Override
            public Json next() {
                if (!done && next == null) {
                    return self.read();
                }
                else {
                    return next;
                }
            }
        };
    }
}
