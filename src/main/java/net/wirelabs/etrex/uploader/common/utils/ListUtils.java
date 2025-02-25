package net.wirelabs.etrex.uploader.common.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ListUtils {
    /**
     * Convert iterable to list
     *
     * @param iterable iterable collection
     * @param <T>      type of element in iterable
     * @return list of elements of iterable
     */
    public static <T> List<T> iterableToList(Iterable<T> iterable) {
        if (iterable instanceof List) {
            return (List<T>) iterable;
        }

        List<T> l = new ArrayList<>();
        for (T t : iterable) {
            l.add(t);
        }
        return l;
    }

    /**
     * Create ArrayList of elements
     *
     * @param elements elements
     * @param <T>      type of element
     * @return list of elements
     */
    @SafeVarargs
    public static <T> Collection<T> listOf(T... elements) {
        return new ArrayList<>(Arrays.asList(elements));
    }


    /**
     * Given two lists/iterables finds and returns elements of listA not present in listB
     *
     * @param listA listA
     * @param listB listB
     * @param <T>   type of elements in lists
     * @return collection of elements of listA not present in listB
     */
    public static <T> Collection<T> findElementsOfANotPresentInB(List<T> listA, List<T> listB) {
        return listA.stream()
                .filter(f -> !listB.contains(f))
                .toList();
    }

    public static List<Path> convertStringListToPaths(String commaSeparatedStrings) {
        if (commaSeparatedStrings != null) {
            List<String> strings = Stream.of(commaSeparatedStrings.split(",", -1))
                    .toList();

            return strings.stream()
                    .filter(f -> !f.isBlank())
                    .map(String::strip)
                    .map(Paths::get)
                    .toList();
        }
        return Collections.emptyList();
    }


    public static String convertPathListToString(List<Path> userStorageRoots) {
        return userStorageRoots.stream()
                .map(Path::toString)
                .collect(Collectors.joining(","));
    }
}
