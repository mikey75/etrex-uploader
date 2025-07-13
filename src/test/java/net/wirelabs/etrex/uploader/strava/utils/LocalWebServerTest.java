package net.wirelabs.etrex.uploader.strava.utils;


import net.wirelabs.etrex.uploader.tools.BaseTest;
import net.wirelabs.etrex.uploader.tools.TestHttpServer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static net.wirelabs.etrex.uploader.strava.utils.NetworkingUtils.isHostTcpPortReachable;
import static org.assertj.core.api.Assertions.assertThat;

class LocalWebServerTest extends BaseTest {

    @Test
    void shouldStartDefaultServer() throws IOException {
        TestHttpServer server = new TestHttpServer();
        server.start();
        assertThat(server.getServerInstance()).isNotNull();
        verifyLogged("Local http server started on port " + server.getListeningPort());
        verifyLogged("Document root: /");

        assertThat(server.getDocumentRoot()).isEqualTo("/");
        assertThat(isHostTcpPortReachable("localhost", server.getListeningPort(), 1000)).isTrue();
        server.stop();
    }

    @Test
    void shouldStartCustomServer() throws IOException {
        Executor executor = Executors.newSingleThreadExecutor();

        TestHttpServer server = new TestHttpServer(9999, "/kaka", executor);
        server.start();
        assertThat(server.getServerInstance()).isNotNull();
        assertThat(server.getListeningPort()).isEqualTo(9999);
        assertThat(isHostTcpPortReachable("localhost", server.getListeningPort(), 1000)).isTrue();

        verifyLogged("Local http server started on port 9999");
        verifyLogged("Document root: /kaka");
        server.stop();

    }

    @Test
    void shouldStartServerOnCustomPort() throws IOException {

        TestHttpServer server = new TestHttpServer(9999);
        server.start();
        assertThat(server.getServerInstance()).isNotNull();
        assertThat(server.getListeningPort()).isEqualTo(9999);
        assertThat(isHostTcpPortReachable("localhost", server.getListeningPort(), 1000)).isTrue();

        verifyLogged("Local http server started on port 9999");
        verifyLogged("Document root: /");
        server.stop();

    }

    @Test
    void shouldStartAndServeAHtmlDocument() throws IOException, URISyntaxException, InterruptedException {


        TestHttpServer server = new TestHttpServer(9999);
        server.start();

        HttpClient client = HttpClient.newHttpClient();
        URI uri = new URI("http://localhost:9999");

        HttpRequest clientRequest = HttpRequest.newBuilder().uri(uri).GET().build();

        //Send the request and get the response, assert that is the correct one
        HttpResponse<String> result = client.send(clientRequest, HttpResponse.BodyHandlers.ofString());
        assertThat(result.uri()).isEqualTo(uri);
        assertThat(result.headers().map()).containsEntry("Content-type", List.of("text/html"));
        assertThat(result.body()).contains("<html>Hello world!</html>");


    }

}