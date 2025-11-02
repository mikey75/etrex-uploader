package net.wirelabs.etrex.uploader.tools.emulator.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class TokenController {
    @Autowired
    private FileReader fileReader;
    @PostMapping("/oauth/token")
    public String putToken(@RequestParam Map<String, String> params) {
        return getToken(params);
    }

    private String getToken(Map<String, String> params) {
        String token = "";
        String grantType = params.get("grant_type");
        if (!grantType.isEmpty()) {
            if (grantType.equals("authorization_code")) {
                token = fileReader.readFileContents("src/test/resources/token/access_token.json");
            }
            if (grantType.equals("refresh_token")) {
                token = fileReader.readFileContents("src/test/resources/token/refresh_token.json");
            }
        }
        return token;
    }
}
