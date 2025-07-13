package net.wirelabs.etrex.uploader.tools;

import com.sun.net.httpserver.HttpExchange;
import lombok.Getter;
import net.wirelabs.etrex.uploader.strava.utils.LocalWebServer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Executor;

@Getter
public class TestHttpServer extends LocalWebServer {

    volatile String path = null;
    volatile String method = null;
    volatile String query = null;

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

        path = exchange.getRequestURI().getPath();
        method = exchange.getRequestMethod();
        query = exchange.getRequestURI().getQuery();

        String response;
        int status = 200;

        if ("/testfile.html".equals(path)) {
            response = Files.readString(Path.of("src/test/resources/httpserver/testfile.html"));
            exchange.getResponseHeaders().add("Content-type","text/html");
        } else if ("/error".equals(path)) {
            response = "Internal Server Error";
            status = 500;
        } else if (!"GET".equals(method)) {
            response = "Method Not Allowed";
            status = 405;
        } else if ("/".equals(path)) {
            response = "Hello!";
        } else {
            response = "Not found";
            status = 404;
        }
        exchange.sendResponseHeaders(status, response.length());
        exchange.getResponseBody().write(response.getBytes());
        exchange.getResponseBody().close();
    }
}