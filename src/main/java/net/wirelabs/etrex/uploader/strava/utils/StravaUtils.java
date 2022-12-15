package net.wirelabs.etrex.uploader.strava.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.wirelabs.etrex.uploader.strava.client.StravaException;
import net.wirelabs.etrex.uploader.strava.model.SportType;

import java.io.File;

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
}


