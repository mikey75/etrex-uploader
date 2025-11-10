package net.wirelabs.etrex.uploader.utils;

import net.wirelabs.etrex.uploader.tools.BaseTest;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static net.wirelabs.etrex.uploader.utils.HttpUtils.decorateUrlWithParams;
import static net.wirelabs.etrex.uploader.utils.HttpUtils.parseMultipartFormData;
import static org.assertj.core.api.Assertions.assertThat;

class HttpUtilsTest extends BaseTest {

    @Test
    void testQueryParsing() {

        String query = "p1=param1&p2=param2&p3=param3";

        Map<String, String> result = HttpUtils.parseQueryParams(query);

        assertThat(result)
                .containsEntry("p1", "param1")
                .containsEntry("p2", "param2")
                .containsEntry("p3", "param3");


    }

    @Test
    void testIllegalKVQueryParse() {
        // when key value pair has more than two elements (split on '='), the method returns empty map
        String query = "p1=p2=p3";
        Map<String, String> result = HttpUtils.parseQueryParams(query);
        assertThat(result).isEmpty();
    }

    @Test
    void testMultipartFormParsing() {

        String boundary = "test_boundary";
        String contentType = "multipart/form-data; boundary=" + boundary;

        String body = "--" + boundary + "\r\n" +
                "Content-Disposition: form-data; name=\"username\"\r\n\r\n" +
                "user\r\n" +
                "--" + boundary + "\r\n" +
                "Content-Disposition: form-data; name=\"email\"\r\n\r\n" +
                "user@example.com\r\n" +
                "--" + boundary + "--";

        Map<String, String> form = parseMultipartFormData(contentType, body);

        assertThat(form).hasSize(2)
                .containsEntry("username","user")
                .containsEntry("email","user@example.com");



    }

    @Test
    void shouldDecorateUrl() {
        String url = "http://www.321nonexistent123.pl";

        Map<String,String> params = new HashMap<>();
        params.put("k1","v1");
        params.put("k2","v2");
        params.put("k3","v3");

        String finalUrl = decorateUrlWithParams(url,params);

        assertThat(finalUrl).isEqualTo(url+"?k1=v1&k2=v2&k3=v3");

    }
}