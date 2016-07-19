package com.novoda.github.reports.data.db.converter;

import java.util.Map;

public abstract class ImmutableMapEntry<K, V> implements Map.Entry<K, V> {

    @Override
    public V setValue(V value) {
        throw new IllegalStateException("The map is immutable, you cannot set a value.");
    }
}
