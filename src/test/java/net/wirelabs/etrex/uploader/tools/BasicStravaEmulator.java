package net.wirelabs.etrex.uploader.tools;

import com.strava.model.DetailedActivity;
import com.strava.model.SummaryActivity;
import com.strava.model.UpdatableActivity;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.utils.JsonUtil;
import net.wirelabs.etrex.uploader.strava.utils.LocalWebServer;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static net.wirelabs.etrex.uploader.utils.HttpUtils.*;
import static net.wirelabs.etrex.uploader.utils.JsonUtil.deserialize;

@Slf4j
public class BasicStravaEmulator extends LocalWebServer {
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

    public BasicStravaEmulator() throws IOException {
        log.info("Started Strava emulator");
    }

    @Override
    protected void handleRequest(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        URI uri = exchange.getRequestURI();
        String path = uri.getPath();
        String query = uri.getRawQuery();
        Headers headers = exchange.getRequestHeaders();
        String contentType = headers.getFirst("Content-Type");
        String authHeader = headers.getFirst("Authorization");

        Map<String, String> queryParams = parseQueryParams(query);
        String rawBody = readBody(exchange);
        Map<String, Object> bodyData = getBodyData(contentType, rawBody);

        String response = "";
        log.info("[Auth] authHeader: {}", authHeader);
        log.info("[Query] queryParams: {}", queryParams);
        log.info("[Body] body: {}", bodyData);

        switch (method + " " + path) {
            // get activity by id
            case "GET /activities/777111" -> response = getSingleFileResponse("src/test/resources/strava-emulator/data/activities/777111/activity.json");
            // get activity's stats
            case "GET /activities/777111/streams" -> response = getSingleFileResponse("src/test/resources/strava-emulator/data/activities/777111/activity-stream.json");
            // get current athlete
            case "GET /athlete" -> response = getSingleFileResponse("src/test/resources/strava-emulator/data/current_athlete/athlete.json");
            // get current athlete's activities
            case "GET /athlete/activities" -> response = getCurrentAthleteActivities();
            // current athlete stats - current athlete id is 12345678
            case "GET /athletes/12345678/stats" -> response = getSingleFileResponse("src/test/resources/strava-emulator/data/current_athlete/stats/stats.json");
            // other athlete stats - other athlete id is 87654321
            case "GET /athletes/87654321/stats" -> response = getSingleFileResponse("src/test/resources/strava-emulator/data/other_athlete/stats/stats.json");
            // put on concrete activity -> update activity
            case "PUT /activities/777111" -> response = updateActivity(rawBody);
            // post on uploads -> upload activity
            // get the upload by id returns the posted upload
            case "POST /uploads", "GET /uploads/999999" -> response = getSingleFileResponse("src/test/resources/strava-emulator/data/uploads/upload.json");
            // post to oauth/token means either refresh token or authorize
            case "POST /oauth/token" -> response = getToken(rawBody);

            // default response if none of the above is met
            default -> response = "404 Not Found: " + method + " " + path;
        }

        sendResponse(exchange, response);
    }

    private static Map<String, Object> getBodyData(String contentType, String rawBody) {
        Map<String,Object> bodyData = new HashMap<>();
        if (contentType != null) {
            if (contentType.contains(ContentTypes.JSON)) {
                bodyData = deserialize(rawBody, Map.class);
            } else if (contentType.contains(ContentTypes.FORM)) {
                Map<String, String> form = parseQueryParams(rawBody);
                if (form.containsKey("payload")) {
                    bodyData = deserialize(form.get("payload"), Map.class);
                } else {
                    bodyData.putAll(form);
                }
            } else if (contentType.contains(ContentTypes.MULTIPART_FORM)) {
                Map<String, String> form = parseMultipartFormData(contentType, rawBody);
                if (form.containsKey("payload")) {
                    bodyData = deserialize(form.get("payload"), Map.class);
                } else {
                    bodyData.putAll(form);
                }
            }
        }
        return bodyData;
    }

    private void sendResponse(HttpExchange exchange, String response) throws IOException {
        byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(response.startsWith("404") ? 404 : 200, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }

    private String updateActivity(String rawBody) throws IOException {
        String response = "";
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
        return response;
    }

    private String getToken(String rawBody) throws IOException {
        String token = "";
        if (rawBody.contains("grant_type=authorization_code")) {
            token = getSingleFileResponse("src/test/resources/token/access_token.json");
        }
        if (rawBody.contains("grant_type=refresh_token")) {
            token = getSingleFileResponse("src/test/resources/token/refresh_token.json");
        }
        return token;
    }

    private String getCurrentAthleteActivities() throws IOException {
        String response = "";
        File dir = new File("src/test/resources/strava-emulator/data/current_athlete/activities");
        if (dir.exists() && dir.isDirectory()) {
            List<File> files = FileUtils.listFiles(dir, new String[]{"json"}, false).stream().filter(f -> f.getName().contains("activity")).toList();
            List<SummaryActivity> activities = new ArrayList<>();
            for (File f : files) {
                String fs = getSingleFileResponse(f.getPath());
                activities.add(deserialize(fs, SummaryActivity.class));
            }
            response = JsonUtil.serialize(activities);
        }
        return response;
    }

    private String getSingleFileResponse(String pathname) throws IOException {
        File f = new File(pathname);
        if (f.exists()) {
            return FileUtils.readFileToString(f, StandardCharsets.UTF_8);
        } else {
            return "";
        }
    }

    private String readBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

}