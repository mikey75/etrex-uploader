package net.wirelabs.etrex.uploader;

import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ListUtils {

    public static <T> List<T> convertIterableToList(Iterable<T> i) {
        if (i instanceof List) {
            return (List<T>) i;
        }

        List<T> l = new ArrayList<>();
        for (T t : i) {
            l.add(t);
        }
        return l;
    }
}
