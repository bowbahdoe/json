package dev.mccue.json.internal;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class MapUtil {
    private MapUtil() {}

    /**
     * Equivalent to Map.copyOf that preserves order.
     */
    public static <K, V> Map<K, V> orderedCopyOf(Map<K, V> m) {
        var copy = new LinkedHashMap<K, V>();
        for (var entry : m.entrySet()) {
            Objects.requireNonNull(entry.getKey());
            Objects.requireNonNull(entry.getValue());
            copy.put(entry.getKey(), entry.getValue());
        }
        return Collections.unmodifiableMap(copy);
    }
}
