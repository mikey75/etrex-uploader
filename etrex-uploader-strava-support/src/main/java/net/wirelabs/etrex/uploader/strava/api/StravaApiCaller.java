package net.wirelabs.etrex.uploader.strava.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;
import java.time.OffsetDateTime;

/**
 * Created 11/3/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
public abstract class StravaApiCaller {
    protected final OkHttpClient httpClient;
    protected Gson jsonParser;

    public StravaApiCaller() {
        this.jsonParser = createJsonParser();
        this.httpClient = new OkHttpClient();
    }

    private Gson createJsonParser() {

        GsonBuilder gsonBuilder = new GsonBuilder()
                .registerTypeAdapter(OffsetDateTime.class, (JsonDeserializer<OffsetDateTime>)
                        (json, type, context) -> OffsetDateTime.parse(json.getAsString()));

        jsonParser = gsonBuilder.create();
        return jsonParser;
    }

    protected String execute(Request request) throws StravaApiException {
        Response response;
        try {
            response = httpClient.newCall(request).execute();
            try (ResponseBody body = response.body()) {
                    return body.string();
            }
        } catch (IOException e) {
            throw new StravaApiException(e.getMessage());
        }

    }
}
