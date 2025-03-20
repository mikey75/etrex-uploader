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
    public static Request createTokenRequest(String appId, String clientSecret, String authCode) {
        RequestBody body = new FormEncodingBuilder()
                .add("client_id", appId)
                .add("client_secret", clientSecret)
                .add("code", authCode)
                .add("grant_type", "authorization_code")
                .build();

        return getRequest(body);
    }



    /**
     * Build refresh token request as per strava docs
     * <p>
     * Returns the HttpRequest object equivalent to:
     * <p>
     * curl -X POST https://www.strava.com/oauth/token \
     * -d client_id=ReplaceWithClientID \
     * -d client_secret=ReplaceWithClientSecret \
     * -d grant_type=refresh_token \
     * -d refresh_token=ReplaceWithRefreshToken
     *
     * @param appId client id
     * @param clientSecret client secret
     * @param refreshToken refresh token
     * @return built request
     */
    public static Request createRefreshTokenRequest(String appId, String clientSecret, String refreshToken) {
        RequestBody body = new FormEncodingBuilder()
                .add("client_id", appId)
                .add("client_secret", clientSecret)
                .add("grant_type", "refresh_token")
                .add("refresh_token", refreshToken)
                .build();

        return getRequest(body);
    }

    private static Request getRequest(RequestBody body) {
        return new Request.Builder()
                .url(Constants.STRAVA_TOKEN_URL)
                .post(body)
                .build();
    }
}
