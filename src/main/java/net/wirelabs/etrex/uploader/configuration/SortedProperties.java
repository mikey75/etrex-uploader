package net.wirelabs.etrex.uploader.configuration;

import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.util.*;
import java.util.stream.Collectors;

public class SortedProperties extends Properties {
    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull
    @Override
    public Set<Map.Entry<Object, Object>> entrySet() {
        return sortedKeys().stream()
                .map(k -> new AbstractMap.SimpleEntry<Object, Object>(k, getProperty(k)))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public Enumeration<Object> keys() {
        Iterator<String> iterator = sortedKeys().iterator();
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

    private List<String> sortedKeys() {
        return keySet().stream().map(Object::toString).sorted().toList();
    }
}