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
        Collection<String> list = List.of("ala","ma","kota");
        List<String> ss = ListUtils.iterableToList(list);
        assertThat(ss).isSameAs(list);

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
    void shouldConvertSingleElementStringListToPath() {
        // single element string
        String singleElementString ="z   ";
        // expected resulting path
        Path expectedSingleElementPath = Paths.get("z");
        // check single element path parsing
        List<Path> paths = ListUtils.convertStringListToPaths(singleElementString);
        assertThat(paths).containsExactly(expectedSingleElementPath);
    }

    @Test
    void shouldConvertMultiElementStringListToPath() {
        // multi element string
        String multiElementString = "a ,b ,doc and set";
        // expected paths
        Path expectedFirstPath = Paths.get("a");
        Path expectedSecondPath = Paths.get("b");
        Path expectedThirdPath = Paths.get("doc and set");
        // check multi element path parsing
        List<Path> paths = ListUtils.convertStringListToPaths(multiElementString);
        assertThat(paths).containsExactly(expectedFirstPath, expectedSecondPath, expectedThirdPath);
    }

    @Test
    void shouldParseEmptyConvertStringListToEmptyPath() {
        // check empty path parsing
        List<Path> paths = ListUtils.convertStringListToPaths("");
        assertThat(paths).isEmpty();
    }
    @Test
    void shouldParseNullStringListToEmptyPath() {
        // check null path parsing
        List<Path> paths = ListUtils.convertStringListToPaths(null);
        assertThat(paths).isEmpty();
    }

    @Test
    void shouldConvertPathListToString() {
        List<Path> p = List.of(Paths.get("Documents and Settings"),Paths.get("B"), Paths.get("C"));
        String result = ListUtils.convertPathListToString(p);
        assertThat(result).isEqualTo("Documents and Settings,B,C");
    }
}