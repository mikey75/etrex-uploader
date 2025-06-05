package net.wirelabs.etrex.uploader.tools;

import com.strava.model.DetailedActivity;
import com.strava.model.UpdatableActivity;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.common.utils.SystemUtils;
import net.wirelabs.etrex.uploader.strava.utils.JsonUtil;
import net.wirelabs.etrex.uploader.strava.utils.NetworkingUtils;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static net.wirelabs.etrex.uploader.strava.utils.JsonUtil.deserialize;

@Slf4j
public class BasicStravaEmulator {
    /**
     * BasicStravaEmulator
     * -------------------
     * A lightweight HTTP server that emulates key Strava API endpoints for local/offline testing.
     * <p>
     * USAGE:
     * - Used automatically by StravaClientTest.java.
     * - Serves responses from static JSON files under src/test/resources/strava-emulator/data/.
     * - Supports GET, POST, and PUT for athletes, activities, stats, uploads, and token exchange.
     * - Handles application/json, form-encoded, and multipart POST bodies.
     * <p>
     * TO EXTEND:
     * - Add new endpoint handling in the switch statement in handleRequest().
     * - Place new JSON response files under the corresponding data directory.
     * - Adjust test cases to call your new endpoints as needed.
     * <p>
     * LIMITATIONS:
     * - Only serves a subset of Strava API endpoints.
     * - Does not simulate API rate limiting or advanced error scenarios unless explicitly coded.
     * <p>
     * For more details, see the test README.
     */
    private HttpServer server;
    @Getter
    private int port;

    public BasicStravaEmulator() {
        try {
            port = NetworkingUtils.getRandomFreeTcpPort();
            server = HttpServer.create(new InetSocketAddress("localhost", port), 0);
            server.createContext("/", BasicStravaEmulator::handleRequest);
            server.setExecutor(null);
            log.info("Server running at http://localhost:" + port);
        } catch (IOException e) {
            log.info("Strava emulator could not be run: {}", e.getMessage(), e);
            SystemUtils.systemExit(1);
        }
    }

    public void start() {
        server.start();
        log.info("Server started at {}:{}", server.getAddress().getHostName(), port);
    }

    public void teardown() {
        server.stop(0);
        log.info("Server stopped");
    }


    private static void handleRequest(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        URI uri = exchange.getRequestURI();
        String path = uri.getPath();
        String query = uri.getRawQuery();
        Headers headers = exchange.getRequestHeaders();
        String contentType = headers.getFirst("Content-Type");
        String authHeader = headers.getFirst("Authorization");

        Map<String, String> queryParams = parseQueryParams(query);
        String rawBody = readBody(exchange);
        Map<String, Object> bodyData = new HashMap<>();

        if (contentType != null) {
            if (contentType.contains("application/json")) {
                bodyData = deserialize(rawBody, Map.class);
            } else if (contentType.contains("application/x-www-form-urlencoded")) {
                Map<String, String> form = parseQueryParams(rawBody);
                if (form.containsKey("payload")) {
                    bodyData = deserialize(form.get("payload"), Map.class);
                } else {
                    bodyData.putAll(form);
                }
            } else if (contentType.contains("multipart/form-data")) {
                Map<String, String> form = parseMultipartFormData(contentType, rawBody);
                if (form.containsKey("payload")) {
                    bodyData = deserialize(form.get("payload"), Map.class);
                } else {
                    bodyData.putAll(form);
                }
            }
        }

        String response = "";

        switch (method + " " + path) {
            // get activity by id
            case "GET /activities/777111" -> {
                response = getSingleFileResponse("src/test/resources/strava-emulator/data/activities/777111/activity.json");
            }
            // get activity's stats
            case "GET /activities/777111/streams" -> {
                response = getSingleFileResponse("src/test/resources/strava-emulator/data/activities/777111/activity-stream.json");
            }
            // get current athlete
            case "GET /athlete" -> {
                response = getSingleFileResponse("src/test/resources/strava-emulator/data/current_athlete/athlete.json");
            }
            // get current athlete's activities
            case "GET /athlete/activities" -> {
                File dir = new File("src/test/resources/strava-emulator/data/current_athlete/activities");
                if (dir.exists() && dir.isDirectory()) {
                    List<File> files = FileUtils.listFiles(dir, new String[]{"json"}, false).stream().filter(f -> f.getName().contains("activity")).toList();
                    if (files.size() == 1) {
                        response = FileUtils.readFileToString(files.get(0), StandardCharsets.UTF_8);
                    } else {
                        List<Map<String, Object>> list = new ArrayList<>();
                        for (File f : files) {
                            String fs = FileUtils.readFileToString(f, StandardCharsets.UTF_8);
                            list.add(deserialize(fs, Map.class));
                        }
                        response = JsonUtil.serialize(list);

                    }
                }

            }
            // current athlete stats - current athlete id is 12345678
            case "GET /athletes/12345678/stats" -> {
                response = getSingleFileResponse("src/test/resources/strava-emulator/data/current_athlete/stats/stats.json");
            }
            // other athlete stats - other athlete id is 87654321
            case "GET /athletes/87654321/stats" -> {
                response = getSingleFileResponse("src/test/resources/strava-emulator/data/other_athlete/stats/stats.json");
            }
            // put on concrete activity -> update activity
            case "PUT /activities/777111" -> {

                // get updatable activity object from request i.e. what we sent
                UpdatableActivity activity = JsonUtil.deserialize(rawBody, UpdatableActivity.class);

                // get original activity by id and change it according to put request
                String activityFile = getSingleFileResponse("src/test/resources/strava-emulator/data/activities/777111/activity.json");

                DetailedActivity updatedOriginalActivity = JsonUtil.deserialize(activityFile, DetailedActivity.class);
                updatedOriginalActivity.setCommute(activity.isCommute());
                updatedOriginalActivity.setName(activity.getName());
                updatedOriginalActivity.setSportType(activity.getSportType());
                updatedOriginalActivity.setDescription(activity.getDescription());
                response = JsonUtil.serialize(updatedOriginalActivity);
            }
            // post on uploads -> upload activity
            // get the upload by id returns the posted upload
            case "POST /uploads", "GET /uploads/999999" -> {
                response = getSingleFileResponse("src/test/resources/strava-emulator/data/uploads/upload.json");
            }
            // post to oauth/token means either refresh token or authorize
            case "POST /oauth/token" -> {
                if (rawBody.contains("grant_type=authorization_code")) {
                    response = getSingleFileResponse("src/test/resources/token/access_token.json");
                }
                if (rawBody.contains("grant_type=refresh_token")) {
                    response = getSingleFileResponse("src/test/resources/token/refresh_token.json");
                }
            }
            default -> response = "404 Not Found: " + method + " " + path;
        }


        byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(response.startsWith("404") ? 404 : 200, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }

    private static String getSingleFileResponse(String pathname) throws IOException {
        File f = new File(pathname);
        if (f.exists()) {
            return FileUtils.readFileToString(f, StandardCharsets.UTF_8);
        } else {
            return "";
        }
    }

    private static String readBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private static Map<String, String> parseQueryParams(String raw) {
        Map<String, String> params = new HashMap<>();
        if (raw == null || raw.isEmpty()) return params;
        for (String pair : raw.split("&")) {
            String[] kv = pair.split("=", 2);

            String k = URLDecoder.decode(kv[0], StandardCharsets.UTF_8);
            String v = kv.length > 1 ? URLDecoder.decode(kv[1], StandardCharsets.UTF_8) : "";
            params.put(k, v);

        }
        return params;
    }

    private static Map<String, String> parseMultipartFormData(String contentType, String body) {
        Map<String, String> form = new HashMap<>();
        String boundary = contentType.split("boundary=")[1];
        String[] parts = body.split("--" + boundary);
        for (String part : parts) {
            if (part.trim().isEmpty() || part.equals("--")) continue;
            String[] sections = part.split("\r\n\r\n", 2);
            if (sections.length < 2) continue;

            String headers = sections[0];
            String content = sections[1].trim();

            String name = null;
            for (String line : headers.split("\r\n")) {
                if (line.startsWith("Content-Disposition")) {
                    for (String dispPart : line.split(";")) {
                        dispPart = dispPart.trim();
                        if (dispPart.startsWith("name=")) {
                            name = dispPart.substring(6).replace("\"", "");
                        }
                    }
                }
            }

            if (name != null) {
                form.put(name, content);
            }
        }
        return form;
    }

}