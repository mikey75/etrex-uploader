package net.wirelabs.etrex.uploader.common.utils;


import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Created 6/23/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
class ListUtilsTest {

    @Test
    void iterableToListTest() {
        Path elements = Paths.get("Ala/ma/kota");
        List<Path> s = ListUtils.iterableToList(elements);
        assertThat(s).containsExactlyElementsOf(elements);

        // test if list is returned unchanged
        Collection<String> list = ListUtils.listOf("ala","ma","kota");
        List<String> ss = ListUtils.iterableToList(list);
        assertThat(ss).isSameAs(list);

    }

    @Test
    void shouldProduceList() {
        String[] elements = {"Ala","Ania","Kasia"};
        Collection<String> result = ListUtils.listOf(elements);
        Collection<String> result2 = ListUtils.listOf(elements);
        assertThat(result).containsOnly("Ala","Ania","Kasia");
        assertThat(result2).containsOnly("Ala","Ania","Kasia");
        assertThat(result).isNotSameAs(result2); // check if two different objects were created
        Collection<String> result3 = ListUtils.listOf();
        assertThat(result3).isNotNull().isEmpty();
    }

    @Test
    void listDifferenceTest() {

        List<String> listA = Arrays.asList("Ala","Ola","Kasia","Mariola");
        List<String> listB = Arrays.asList("Asia", "Ola", "Magda", "Zosia");
        Iterable<String> result;

        result = ListUtils.findElementsOfANotPresentInB(listA, listB);

        // should have elements of listA that are not present in listB
        assertThat(result).containsOnly("Ala","Kasia","Mariola");

        // empty lists?
        result = ListUtils.findElementsOfANotPresentInB(new ArrayList<>(), new ArrayList<>());
        assertThat(result).isEmpty();

    }

    @Test
    void testStringListToPath() {
        // multi element string
        String multiElementString = "a ,b ,doc and set";
        // expected resulting paths
        Path expected_first_path = Paths.get("a");
        Path expected_second_path = Paths.get("b");
        Path expected_third_path = Paths.get("doc and set");

        // single element string
        String singleElementString ="z   ";
        // expected resulting path
        Path expected_single_element_path = Paths.get("z");

        // check multi element path parsing
        List<Path> paths = ListUtils.convertStringListToPaths(multiElementString);
        assertThat(paths).containsExactly(expected_first_path, expected_second_path, expected_third_path);

        // check single element path parsing
        paths = ListUtils.convertStringListToPaths(singleElementString);
        assertThat(paths).containsExactly(expected_single_element_path);

        // check empty path parsing
        paths = ListUtils.convertStringListToPaths("");
        assertThat(paths).isEmpty();

        // check null path parsing
        paths = ListUtils.convertStringListToPaths(null);
        assertThat(paths).isEmpty();
    }

    @Test
    void testPathListToString () {
        List<Path> p = List.of(Paths.get("Documents and Settings"),Paths.get("B"), Paths.get("C"));
        String result = ListUtils.convertPathListToString(p);
        assertThat(result).isEqualTo("Documents and Settings,B,C");
    }
}