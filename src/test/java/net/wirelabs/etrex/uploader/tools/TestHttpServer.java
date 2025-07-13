package net.wirelabs.etrex.uploader.tools;

import com.sun.net.httpserver.HttpExchange;
import net.wirelabs.etrex.uploader.strava.utils.LocalWebServer;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Executor;


public class TestHttpServer extends LocalWebServer {

    public TestHttpServer(int port) throws IOException {
        super(port);
    }

    public TestHttpServer(int port, String docRoot, Executor executor) throws IOException {
        super(port, docRoot, executor);
    }

    public TestHttpServer() throws IOException {

    }

    @Override
    protected void handleRequest(HttpExchange exchange) throws IOException {

        final String serverResponse = Files.readString(Path.of("src/test/resources/httpserver/testfile.html"));

        byte[] responseBytes = serverResponse.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-type", "text/html");
        exchange.sendResponseHeaders(200, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }
}