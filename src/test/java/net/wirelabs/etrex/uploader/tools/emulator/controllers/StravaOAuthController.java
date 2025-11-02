package net.wirelabs.etrex.uploader.tools.emulator.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.Optional;

@RestController
public class StravaOAuthController {

    @GetMapping("/authorize")
    public ResponseEntity<?> authorize(@RequestParam Map<String, String> queryParams) {
        String url = queryParams.get("redirect_uri");
        String response = String.valueOf(callAuthCodeInterceptor(url + "?code=supersecret&scope=activity:read,activity:write,read_all"));
        return ResponseEntity.of(Optional.of(response));
    }

    private HttpResponse<String> callAuthCodeInterceptor(String url) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "?" + "code=supersecret&scope=activity:read,activity:write,read_all"))
                .GET()
                .build();
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new IllegalStateException("Exception");
        }
    }
}
