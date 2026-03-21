package net.wirelabs.etrex.uploader.tools;

import com.sun.net.httpserver.HttpExchange;
import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.strava.utils.LocalWebServer;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
public class StatsHuntersEmulator extends LocalWebServer {

    public StatsHuntersEmulator() throws IOException {}

    @Override
    protected void handleRequest(HttpExchange exchange) throws IOException {

        String response = "";
        String path = exchange.getRequestURI().getPath();
        switch (path) {
            case "/good" -> response = getSingleFileResponse("src/test/resources/statshunters-emulator/data/squares.json");
            case "/bad" -> response = "[some: nonconformant: {json}]";
        }
        sendResponse(exchange, response);
    }


    private String getSingleFileResponse(String pathname) throws IOException {
        File f = new File(pathname);
        if (f.exists()) {
            return FileUtils.readFileToString(f, StandardCharsets.UTF_8);
        } else {
            return "";
        }
    }

    private void sendResponse(HttpExchange exchange, String response) throws IOException {
        byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(response.startsWith("404") ? 404 : 200, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }
}
