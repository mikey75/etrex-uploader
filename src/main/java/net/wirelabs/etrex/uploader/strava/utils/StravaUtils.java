package net.wirelabs.etrex.uploader.strava.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.wirelabs.etrex.uploader.common.EventType;
import net.wirelabs.etrex.uploader.common.eventbus.EventBus;
import net.wirelabs.etrex.uploader.strava.client.StravaException;
import net.wirelabs.etrex.uploader.strava.model.SportType;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/*
 * Created 12/10/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StravaUtils {

    public static String guessUploadFileFormat(File file) throws StravaException {

        String[] allowedFileFormats = {"gpx", "gpx.gz", "fit", "fit.gz", "tcx", "tcx.gz"};
        String fname = file.getName().toLowerCase();

        for (String extension : allowedFileFormats) {
            if (fname.endsWith("." + extension)) return extension;
        }

        throw new StravaException("The file you're uploading is in unsupported format");
    }

    /**
     * Build upload form body for uploading track resulting in created activity
     * @param file file to upload
     * @param name name of the activity
     * @param description description of the activity
     * @param sportType sport type
     * @return form body object to use in POST request
     * @throws StravaException
     */
    public static MultipartForm createFileUploadForm(File file, String name, String description, SportType sportType) throws StravaException {

        return MultipartForm.newBuilder()
                .addPart("file", file.toPath())
                .addPart("data_type", guessUploadFileFormat(file))
                .addPart("name", name)
                .addPart("sport_type", sportType.getValue())
                .addPart("description", description);

    }

    public static final String ALLOWED_DAILY = "allowedDaily";
    public static final String ALLOWED_15MINS = "allowed15mins";
    public static final String CURRENT_DAILY = "currentDaily";
    public static final String CURRENT_15MINS = "current15mins";

    public static void sendRateLimitInfo(Map<String, List<String>> headers) {

        if (headers.containsKey("x-ratelimit-limit") && headers.containsKey("x-ratelimit-usage")) {

            HashMap<String, Integer> info = new HashMap<>();

            String rateLimitAllowed = headers.get("x-ratelimit-limit").get(0);
            String rateLimitCurrent = headers.get("x-ratelimit-usage").get(0);

            StringTokenizer tokenizer = new StringTokenizer(rateLimitAllowed, ",");
            info.put(ALLOWED_15MINS, Integer.parseInt(tokenizer.nextToken()));
            info.put(ALLOWED_DAILY, Integer.parseInt(tokenizer.nextToken()));

            tokenizer = new StringTokenizer(rateLimitCurrent, ",");
            info.put(CURRENT_15MINS, Integer.parseInt(tokenizer.nextToken()));
            info.put(CURRENT_DAILY, Integer.parseInt(tokenizer.nextToken()));

            EventBus.publish(EventType.RATELIMIT_INFO_UPDATE, info);
        }
    }
}


