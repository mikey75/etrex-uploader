package net.wirelabs.etrex.uploader.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


class JsonUtilTest {

    @Test
    void shouldDeSerializeObject() {

        String json = """
                {
                    "name":"John",
                    "age":30,
                    "car":null
                }""";

        Person person = JsonUtil.deserialize(json, Person.class);

        assertThat(person.getAge()).isEqualTo(30);
        assertThat(person.getCar()).isNull();
        assertThat(person.getName()).isEqualTo("John");

    }

    @Test
    void shouldSerializeObject() {
        String json = """
                {
                    "name":"Alex",
                    "age":40,
                    "car":"Volvo"
                }""";

        Person person = new Person("Alex", 40, "Volvo");

        String p2Serialized = JsonUtil.serialize(person);
        assertThat(p2Serialized).isEqualToIgnoringWhitespace(json);
    }

}
// todo turn it into a record after global dependency lib version updates
@Getter
@RequiredArgsConstructor
class Person {
    private final String name;
    private final Integer age;
    private final String car;
}