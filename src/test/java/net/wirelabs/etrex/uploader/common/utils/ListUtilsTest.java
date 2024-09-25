package net.wirelabs.etrex.uploader.common.utils;


import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

        ArrayList<String> LISTA = new ArrayList<>(ListUtils.listOf("Ala","Ola","Kasia","Mariola"));
        ArrayList<String> LISTB = new ArrayList<>(ListUtils.listOf("Asia","Ola","Magda","Zosia"));
        Iterable<String> result;

        result = ListUtils.findElementsOfANotPresentInB(LISTA, LISTB);

        // should have elements of listA that are not present in listB
        assertThat(result).containsOnly("Ala","Kasia","Mariola");

        // empty lists?
        result = ListUtils.findElementsOfANotPresentInB(new ArrayList<>(), new ArrayList<>());
        assertThat(result).isEmpty();

    }

    @Test
    void testStringToPath() {
        String s_many = "a ,b ,doc and set";
        String s_single ="a   ";
        String s_none ="";
        String s_NULL = null;
        List<Path> paths = ListUtils.convertStringListToPaths(s_many);
        assertThat(paths).containsExactly(Paths.get("a"),Paths.get("b"), Paths.get("doc and set"));
        paths = ListUtils.convertStringListToPaths(s_single);
        assertThat(paths).containsExactly(Paths.get("a"));
        paths = ListUtils.convertStringListToPaths(s_none);
        assertThat(paths).isEmpty();
        paths = ListUtils.convertStringListToPaths(s_NULL);
        assertThat(paths).isEmpty();
    }

    @Test
    void testPathListToString () {
        List<Path> p = List.of(Paths.get("Documents and Settings"),Paths.get("B"), Paths.get("C"));
        String result = ListUtils.convertPathListToString(p);
        assertThat(result).isEqualTo("Documents and Settings,B,C");
    }
}