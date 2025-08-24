package net.wirelabs.etrex.uploader.configuration;

import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SortedProperties extends Properties {
    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull
    @Override
    public Set<Map.Entry<Object, Object>> entrySet() {
        final Stream<AbstractMap.SimpleEntry<Object, Object>> stream = sortedKeys().map(k -> new AbstractMap.SimpleEntry<>(k, getProperty(k)));
        return stream.collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public synchronized Enumeration<Object> keys() {
        Iterator<String> iterator = sortedKeys().toList().iterator();
        return new Enumeration<>() {
            @Override
            public boolean hasMoreElements() {
                return iterator.hasNext();
            }

            @Override
            public Object nextElement() {
                return iterator.next();
            }
        };
    }

    private Stream<String> sortedKeys() {
        return keySet().stream().map(Object::toString).sorted();
    }
}