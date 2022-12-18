package net.wirelabs.etrex.uploader.strava.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

/*
 * Created 12/17/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonUtil {

    private static final Gson jsonParser  = createJsonParser();

    private static Gson createJsonParser() {
        GsonBuilder gsonBuilder = new GsonBuilder()
                .registerTypeAdapter(OffsetDateTime.class, (JsonDeserializer<OffsetDateTime>)
                        (json, type, context) -> OffsetDateTime.parse(json.getAsString()));

        return gsonBuilder.create();
    }

    public  static <T> T deserialize(String json, Class<T> type) {
        return jsonParser.fromJson(json, type);
    }

    public static String serialize(Object body) {
        return jsonParser.toJson(body);
    }
}
