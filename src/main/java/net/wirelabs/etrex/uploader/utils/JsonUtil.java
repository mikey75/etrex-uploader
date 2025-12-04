package net.wirelabs.etrex.uploader.utils;

import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.lang.reflect.Type;


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

    /*
     * Deserialize json to a concrete type (default usage)
     */
    public static <T> T deserialize(String json, Class<T> type) {
        return jsonParser.fromJson(json, type);
    }

    /*
     * Deserialize json to parametrized type like Map<A,B> ...
     * to avoid type erasure and warnings
     */
    public static <T> T deserialize(String json, Type type) {
        return jsonParser.fromJson(json, type);
    }

    public static String serialize(Object body) {
        return jsonParser.toJson(body);
    }
}
