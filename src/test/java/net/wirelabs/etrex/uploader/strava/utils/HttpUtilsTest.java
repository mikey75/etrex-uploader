package net.wirelabs.etrex.uploader.strava.utils;

import net.wirelabs.etrex.uploader.tools.BaseTest;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static net.wirelabs.etrex.uploader.strava.utils.HttpUtils.parseMultipartFormData;
import static org.assertj.core.api.Assertions.assertThat;

class HttpUtilsTest extends BaseTest {

    @Test
    void testQueryParsing() {

        String query = "p1=kaka&p2=lipa&p3=k";

        Map<String, String> result = HttpUtils.parseQueryParams(query);

        assertThat(result)
                .containsEntry("p1", "kaka")
                .containsEntry("p2", "lipa")
                .containsEntry("p3", "k");


    }

    @Test
    void testIllegalKVQueryParse() {
        // when key value pair has more than two elements (split on '='), the method returns empty map
        String query = "p1=kaka=kipa";
        Map<String, String> result = HttpUtils.parseQueryParams(query);
        assertThat(result).isEmpty();
    }

    @Test
    void testMultipartFormParsing() {

        String boundary = "dupa";
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
}