package net.wirelabs.etrex.uploader.common.utils;

import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/*
 * Created 12/17/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonUtil {

    private static final Gson jsonParser  = createJsonParser();

    private static Gson createJsonParser() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Converters.registerAll(gsonBuilder);  // Registers all Java time converters

        return gsonBuilder.create();
    }

    public  static <T> T deserialize(String json, Class<T> type) {
        return jsonParser.fromJson(json, type);
    }

    public static String serialize(Object body) {
        return jsonParser.toJson(body);
    }
}
