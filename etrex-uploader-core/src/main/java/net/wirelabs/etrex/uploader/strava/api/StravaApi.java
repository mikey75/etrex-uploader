package net.wirelabs.etrex.uploader.strava.api;

import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.common.configuration.Configuration;
import net.wirelabs.etrex.uploader.strava.tokenmanager.TokenManager;

import java.util.Map;

/**
 * Created 11/1/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
@Slf4j
public abstract class StravaApi extends StravaApiCaller {

    protected Configuration configuration;
    protected TokenManager tokenManager;
    protected String apiBaseUrl;

    protected StravaApi(Configuration configuration) {
        this.configuration = configuration;
        this.apiBaseUrl = configuration.getStravaApiBaseUrl();
        this.tokenManager = new TokenManager(configuration);
    }

    protected <T> T makePostRequest(String endpointUrl, RequestBody body, Class<T> type) throws StravaApiException {

        tokenManager.getNewAccessTokenIfExpired();
        Request request = new Request.Builder()
                .header("Authorization", "Bearer " + configuration.getStravaAccessToken())
                .header("Accept", "application/json")
                .url(endpointUrl).post(body).build();
        String result = execute(request);
        return jsonParser.fromJson(result, type);


    }

    protected <T> T makeGetRequest(String endpointUrl, Class<T> type) throws StravaApiException {

        tokenManager.getNewAccessTokenIfExpired();
        Request request = new Request.Builder()
                .header("Authorization", "Bearer " + configuration.getStravaAccessToken())
                .header("Accept", "application/json")
                .url(endpointUrl)
                .get()
                .build();
        String result = execute(request);
        return jsonParser.fromJson(result, type);


    }

    protected <T> T makeParameterizedGetRequest(String endpointUrl, Map<String, String> parameters, Class<T> type) throws StravaApiException {
        String finalUrl = applyParameterToURL(endpointUrl, parameters);
        return makeGetRequest(finalUrl,type);
    }

    private String applyParameterToURL(String endpointUrl, Map<String,String> parameters) {
        HttpUrl.Builder url = HttpUrl.parse(endpointUrl).newBuilder();
        for (Map.Entry<String,String> entry: parameters.entrySet()) {
            url.addQueryParameter(entry.getKey(), entry.getValue());
        }
        return url.build().toString();
    }

}
