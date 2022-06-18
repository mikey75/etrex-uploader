package net.wirelabs.etrex.uploader.strava.api.token;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import net.wirelabs.etrex.uploader.common.Constants;

/**
 * Created 11/3/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
public class ExchangeAuthForTokenRequest {
    /*
         * curl -X POST https://www.strava.com/oauth/token \
         -F client_id=YOURCLIENTID \
         -F client_secret=YOURCLIENTSECRET \
         -F code=AUTHORIZATIONCODE \
         -F grant_type=authorization_code
         */

    private final String authCode;
    private final String clientSecret;
    private final String appId;


    public ExchangeAuthForTokenRequest(String appId, String clientSecret, String authCode) {
        this.clientSecret = clientSecret;
        this.appId = appId;
        this.authCode = authCode;
    }

    public Request buildRequest() {
        return new Request.Builder()
                .url(Constants.STRAVA_TOKEN_URL)
                .post(buildRequestBody())
                .build();
    }

    private RequestBody buildRequestBody() {
        return new FormEncodingBuilder()
                .add("client_id", appId)
                .add("client_secret", clientSecret)
                .add("code", authCode)
                .add("grant_type", "authorization_code")
                .build();
    }



}
