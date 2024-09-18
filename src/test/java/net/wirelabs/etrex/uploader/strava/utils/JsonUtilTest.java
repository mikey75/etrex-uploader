package net.wirelabs.etrex.uploader.strava.utils;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


class JsonUtilTest {

    @Test
    void shouldDeSerializeObject() {
        String json = "{\"name\":\"John\",\"age\":30,\"car\":null}";
        Person p = JsonUtil.deserialize(json, Person.class );

        String json2 = "{\"name\":\"Kaka\",\"age\":40,\"car\":\"Volvo\"}";
        Person p2 = new Person("Kaka",40,"Volvo");

        assertThat(p.age).isEqualTo(30);
        assertThat(p.car).isNull();
        assertThat(p.name).isEqualTo("John");

        json = JsonUtil.serialize(p2);
        assertThat(json).isEqualTo(json2);
    }

}

class Person {

    public Person(String name, Integer age, String car) {
        this.name = name;
        this.age = age;
        this.car = car;
    }
    String name;
    Integer age;
    String car;
}