package net.wirelabs.etrex.uploader.strava.api.token;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import net.wirelabs.etrex.uploader.common.Constants;

/**
 * Created 11/3/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
public class RefreshTokenRequest  {

    private final String appId;
    private final String clientSecret;
    private final String refreshToken;
    /*
     * curl -X POST https://www.strava.com/api/v3/oauth/token \
     *   -d client_id=ReplaceWithClientID \
     *   -d client_secret=ReplaceWithClientSecret \
     *   -d grant_type=refresh_token \
     *   -d refresh_token=ReplaceWithRefreshToken
     */


    public RefreshTokenRequest(String appId, String clientSecret, String refreshToken) {

        this.appId = appId;
        this.clientSecret = clientSecret;
        this.refreshToken = refreshToken;
    }

    public Request buildRequest() {
        return new Request.Builder()
                .url(Constants.STRAVA_TOKEN_URL)
                .post(buildRequestBody())
                .build();
    }

    private  RequestBody buildRequestBody() {
        return new FormEncodingBuilder()
                .add("client_id", appId)
                .add("client_secret", clientSecret)
                .add("grant_type", "refresh_token")
                .add("refresh_token", refreshToken)
                .build();
    }

}
