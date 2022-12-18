package net.wirelabs.etrex.uploader.strava.client;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.wirelabs.etrex.uploader.common.Constants;

/*
 * Created 12/17/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TokenRequest {


    /**
     * Build get token request as per strava docs
     * <p>
     * Returns the HttpRequest object equivalent to:
     * <p>
     * curl -X POST https://www.strava.com/oauth/token \
     * -F client_id=YOURCLIENTID \
     * -F client_secret=YOURCLIENTSECRET \
     * -F code=AUTHORIZATIONCODE \
     * -F grant_type=authorization_code
     *
     * @return built request
     */
    public static Request of(String appId, String clientSecret, String authCode) {
        RequestBody body = new FormEncodingBuilder()
                .add("client_id", appId)
                .add("client_secret", clientSecret)
                .add("code", authCode)
                .add("grant_type", "authorization_code")
                .build();

        return new Request.Builder()
                .url(Constants.STRAVA_TOKEN_URL)
                .post(body)
                .build();
    }

}
