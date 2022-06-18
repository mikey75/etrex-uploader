package net.wirelabs.etrex.uploader.common.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ListUtils {
    /**
     * Convert iterable to list 
     * @param iterable iterable collection
     * @param <T> type of element in iterable 
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
     * @param elements elements
     * @param <T> type of element
     * @return list of elements
     */
    public static <T> Collection<T> listOf(T... elements) {
        return new ArrayList<>(Arrays.asList(elements));
    }


    /**
     * Given two lists/iterables finds and returns elements of listA not present in listB
     * @param listA listA
     * @param listB listB
     * @param <T> type of elements in lists
     * @return collection of elements of listA not present in listB
     */
    public static <T> Collection<T> findElementsOfANotPresentInB(List<T> listA, List<T> listB) {
        return listA.stream()
                .filter(f->!listB.contains(f))
                .collect(Collectors.toList());
    }
}
