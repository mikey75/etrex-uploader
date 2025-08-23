package net.wirelabs.etrex.uploader.strava.utils;


import net.wirelabs.etrex.uploader.tools.BaseTest;
import net.wirelabs.etrex.uploader.tools.TestHttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

import static net.wirelabs.etrex.uploader.common.utils.NetworkingUtils.isHostTcpPortReachable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LocalWebServerTest extends BaseTest {

    private TestHttpServer server;

    @BeforeEach
    void setUp() throws Exception {
        server = new TestHttpServer();
        server.start();
    }

    @AfterEach
    void tearDown() {
        if (server != null) server.stop();
    }

    @Test
    void shouldStartDefaultServer() {

        assertThat(server.getServerInstance()).isNotNull();
        verifyLogged("Local http server started on port " + server.getListeningPort());
        verifyLogged("Document root: /");

        assertThat(server.getDocumentRoot()).isEqualTo("/");
        assertThat(isHostTcpPortReachable("localhost", server.getListeningPort(), 1000)).isTrue();

    }

    @Test
    // this test uses custom server, so we create it explicitly
    // and don't use the one created in beforeEach method
    void shouldStartCustomServer() throws IOException {
        Executor executor = Executors.newSingleThreadExecutor();

        TestHttpServer customServer = new TestHttpServer(9999, "/kaka", executor);
        customServer.start();
        assertThat(customServer.getServerInstance()).isNotNull();
        assertThat(customServer.getListeningPort()).isEqualTo(9999);
        assertThat(isHostTcpPortReachable("localhost", customServer.getListeningPort(), 1000)).isTrue();

        verifyLogged("Local http server started on port 9999");
        verifyLogged("Document root: /kaka");
        customServer.stop();

    }

    @Test
    // this test uses custom server, so we create it explicitly
    // and don't use the one created in beforeEach method
    void shouldStartServerOnCustomPort() throws IOException {

        TestHttpServer customServer = new TestHttpServer(9999);
        customServer.start();
        assertThat(customServer.getServerInstance()).isNotNull();
        assertThat(customServer.getListeningPort()).isEqualTo(9999);
        assertThat(isHostTcpPortReachable("localhost", customServer.getListeningPort(), 1000)).isTrue();

        verifyLogged("Local http server started on port 9999");
        verifyLogged("Document root: /");
        customServer.stop();

    }

    @Test
    void shouldStartAndServeAHtmlDocument() throws IOException {

        String response = createRequest("/testfile.html", "GET");
        assertTrue(response.startsWith("200|<html>Hello world!</html>"));
        assertEquals("/testfile.html", server.getPath());
        assertEquals("GET", server.getMethod());
    }

    @Test
    void testDefault() throws IOException {

        String resp = createRequest("/", "GET");
        assertTrue(resp.startsWith("200|Hello!"));
        assertEquals("/", server.getPath());
        assertEquals("GET", server.getMethod());
    }

    @Test
    void testNotFound() throws IOException {
        String resp = createRequest("/unknown", "GET");
        assertTrue(resp.startsWith("404|Not found"));
        assertEquals("/unknown", server.getPath());
    }

    @Test
    void testMethodNotAllowed() throws IOException {
        String resp = createRequest("/", "POST");
        assertTrue(resp.startsWith("405|Method Not Allowed"));
        assertEquals("POST", server.getMethod());
    }

    @Test
    void testInternalServerError() throws IOException {
        String resp = createRequest("/error", "GET");
        assertTrue(resp.startsWith("500|Internal Server Error"));
    }

    @Test
    void testQueryStringParsing() throws IOException {
        String resp = createRequest("/?foo=bar&baz=qux", "GET");
        assertTrue(resp.startsWith("200|Hello!"));
        assertEquals("foo=bar&baz=qux", server.getQuery());
    }

    @Test
    void testConcurrentRequests() throws Exception {
        int threads = 10;
        ExecutorService exec = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);
        ConcurrentLinkedQueue<String> results = new ConcurrentLinkedQueue<>();
        for (int i = 0; i < threads; i++) {
            exec.submit(() -> {
                try {
                    String res = createRequest("/", "GET");
                    results.add(res);
                } catch (IOException e) {
                    results.add("error");
                } finally {
                    latch.countDown();
                }
            });
        }
        assertThat(latch.await(5, TimeUnit.SECONDS)).isTrue();
        assertEquals(threads, results.size());
        assertTrue(results.stream().allMatch(r -> r.startsWith("200|Hello!")));
        exec.shutdown();
    }

    @Test
    void testMalformedRequestGraceful() throws Exception {
        // Simulate a client connecting and closing without sending a full HTTP request.
        try (Socket s = new Socket("127.0.0.1", server.getListeningPort())) {
            s.getOutputStream().write("BAD REQUEST\r\n\r\n".getBytes());
            // Should not crash server
        }
        // Second request after malformed one, should still succeed
        String resp = createRequest("/", "GET");
        assertTrue(resp.startsWith("200|Hello!"));
    }

    private String createRequest(String path, String method) throws IOException {
        URL url = new URL("http://127.0.0.1:" + server.getListeningPort() + path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod(method);
        int code = conn.getResponseCode();
        InputStream in = (code < 400) ? conn.getInputStream() : conn.getErrorStream();
        String body = new BufferedReader(new InputStreamReader(in)).lines().reduce("", (a, b) -> a + b);
        in.close();
        return code + "|" + body ;
    }

}