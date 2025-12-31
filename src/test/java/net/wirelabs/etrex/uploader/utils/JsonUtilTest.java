package net.wirelabs.etrex.uploader.utils;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


class JsonUtilTest {

    @Test
    void shouldDeSerializeObject() {

        String json = """
                {
                    "name":"John",
                    "age":30,
                    "active":true
                }""";

        Person person = JsonUtil.deserialize(json, Person.class);

        assertThat(person.getAge()).isEqualTo(30);
        assertThat(person.isActive()).isTrue();
        assertThat(person.getName()).isEqualTo("John");

    }

    @Test
    void shouldSerializeObject() {
        String json = """
                {
                    "name":"Alex",
                    "age":40,
                    "active": false
                }""";

        Person person = new Person("Alex", 40, false);

        String p2Serialized = JsonUtil.serialize(person);
        assertThat(p2Serialized).isEqualToIgnoringWhitespace(json);
    }

    @Test
    void shouldDeserializeParametrizedType() {
        String json = """
                {
                  "name": "Alice",
                  "active": true
                }
                """;
        Type expectedType = new TypeToken<Map<String, Object>>() {
        }.getType();
        Map<String, Object> expectedObject = JsonUtil.deserialize(json, expectedType);
        assertThat(expectedObject).isInstanceOf(Map.class)
                .containsEntry("name", "Alice")
                .containsEntry("active", true);

    }


    @Test
    void shouldSerializeAndDeserializePojo() {
        Person alice = new Person("Alice", 30, true);

        String json = JsonUtil.serialize(alice);
        Person deserialized = JsonUtil.deserialize(json, Person.class);

        assertThat(deserialized).isNotNull();
        assertThat(deserialized).usingRecursiveComparison().isEqualTo(alice);
    }

    @Test
    void shouldDeserializeParameterizedMap() {
        String json = """
                {
                  "name": "Alice",
                  "active": true,
                  "age": 30
                }
                """;

        Type mapType = new TypeToken<Map<String, Object>>() {
        }.getType();
        Map<String, Object> result = JsonUtil.deserialize(json, mapType);

        assertThat(result).isNotNull().containsEntry("name", "Alice");

        // Boolean values become Boolean
        assertThat(result.get("active")).isInstanceOf(Boolean.class).isEqualTo(Boolean.TRUE);

        // Gson deserializes numbers to Double when target is Object
        assertThat(result.get("age")).isInstanceOf(Number.class).isEqualTo(30.0);
    }

    @Test
    void shouldDeserializeListOfPojoUsingParameterizedType() {
        List<Person> people = List.of(new Person("Alice", 30, true), new Person("Bob", 25, false));
        Type listType = new TypeToken<List<Person>>() {
        }.getType();

        String json = JsonUtil.serialize(people);
        List<Person> deserialized = JsonUtil.deserialize(json, listType);

        assertThat(deserialized).isNotNull().hasSize(2);
        assertThat(deserialized.get(0)).usingRecursiveComparison().isEqualTo(people.get(0));
        assertThat(deserialized.get(1)).usingRecursiveComparison().isEqualTo(people.get(1));
    }

    @Test
    void shouldRoundTripParameterizedTypes() {
        Map<String, List<Person>> data = Map.of(
                "groupA", List.of(new Person("Alice", 30, true)),
                "groupB", List.of(new Person("Bob", 25, false))
        );

        Type type = new TypeToken<Map<String, List<Person>>>() {
        }.getType();
        String json = JsonUtil.serialize(data);
        Map<String, List<Person>> deserialized = JsonUtil.deserialize(json, type);

        assertThat(deserialized).isNotNull().hasSize(2);
        assertThat(deserialized.get("groupA")).hasSize(1);
        assertThat(deserialized.get("groupA").get(0)).usingRecursiveComparison().isEqualTo(data.get("groupA").get(0));
    }

    @Test
    void shouldHandleNullAndSerializeNull() {

        Person p = JsonUtil.deserialize(null, Person.class);
        assertThat(p).isNull();

        Type listType = new TypeToken<List<Person>>() {
        }.getType();
        List<Person> list = JsonUtil.deserialize(null, listType);
        assertThat(list).isNull();

        String serializedNull = JsonUtil.serialize(null);
        assertThat(serializedNull).isEqualTo("null");
    }

    @Test
    void shouldThrowOnInvalidJsonForPojo() {
        String bad = "not a json";
        assertThrows(JsonSyntaxException.class, () -> JsonUtil.deserialize(bad, Person.class));
    }

    // todo turn it into a record after global dependency lib version updates
    @Getter
    @RequiredArgsConstructor
    static class Person {
        private final String name;
        private final Integer age;
        private final boolean active;
    }
}