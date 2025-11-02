package net.wirelabs.etrex.uploader.tools.emulator.common;

import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.GsonHttpMessageConverter;

@Configuration
public class GsonConfig {
    // this is to force spring to use gson serializer/deserializer to retain compatibility with old emulator
    // and test jsons
    @Bean
    public Gson gson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Converters.registerAll(gsonBuilder);
        gsonBuilder.serializeNulls();
        return gsonBuilder.create();
    }

    @Bean
    public GsonHttpMessageConverter gsonHttpMessageConverter(Gson gson) {
        GsonHttpMessageConverter converter = new GsonHttpMessageConverter();
        converter.setGson(gson);
        return converter;
    }
}