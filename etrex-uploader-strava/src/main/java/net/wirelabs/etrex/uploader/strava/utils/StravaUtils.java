package net.wirelabs.etrex.uploader.strava.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.wirelabs.etrex.uploader.common.Constants;
import net.wirelabs.etrex.uploader.strava.client.StravaException;
import net.wirelabs.etrex.uploader.strava.model.SportType;

import java.io.File;
import java.net.URI;
import java.net.http.HttpRequest;

/*
 * Created 12/10/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StravaUtils {
    /**
     * Build OAuth request URL that will go to Strava app authorization page
     * i.e "Connect with Strava" functionality
     * <p>
     * <a href="https://developers.strava.com/docs/authentication/#requestingaccess">More info</a>
     *
     * @param redirectURL   your OAuth redirect URL
     * @param applicationId registered Application ID
     * @return request that will be issued to Strava
     */
    public static String buildAuthRequestUrl(String redirectURL, String applicationId) {

        return UrlBuilder.newBuilder()
                .baseUrl(Constants.STRAVA_AUTHORIZATION_URL)
                .addQueryParam("client_id", applicationId)
                .addQueryParam("redirect_uri", redirectURL)
                .addQueryParam("response_type", "code")
                .addQueryParam("approval_prompt", "force")
                .addQueryParam("scope", Constants.STRAVA_DEFAULT_APP_ACCESS_SCOPE)
                .build();
    }

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
    public static HttpRequest buildGetTokenRequest(String appId, String clientSecret, String authCode) {

        String formData = FormBuilder.newBuilder()
                .add("client_id", appId)
                .add("client_secret", clientSecret)
                .add("code", authCode)
                .add("grant_type", "authorization_code")
                .build();

        return HttpRequest.newBuilder()
                .uri(URI.create(Constants.STRAVA_TOKEN_URL))
                .POST(HttpRequest.BodyPublishers.ofString(formData))
                .build();
    }

    /**
     * Build refresh token request as per strava docs
     * <p>
     * Returns the HttpRequest object equivalent to:
     * <p>
     * curl -X POST https://www.strava.com/api/v3/oauth/token \
     * -d client_id=ReplaceWithClientID \
     * -d client_secret=ReplaceWithClientSecret \
     * -d grant_type=refresh_token \
     * -d refresh_token=ReplaceWithRefreshToken
     *
     * @return built request
     */
    public static HttpRequest buildTokenRefreshRequest(String appId, String clientSecret, String refreshToken) {

        String formData = FormBuilder.newBuilder()
                .add("client_id", appId)
                .add("client_secret", clientSecret)
                .add("grant_type", "refresh_token")
                .add("refresh_token", refreshToken)
                .build();

        return HttpRequest.newBuilder()
                .uri(URI.create(Constants.STRAVA_TOKEN_URL))
                .POST(HttpRequest.BodyPublishers.ofString(formData))
                .build();
    }

    public static String guessUploadFileFormat(File file) throws StravaException {

        String[] allowedFileFormats = {"gpx", "gpx.gz", "fit", "fit.gz", "tcx", "tcx.gz"};
        String fname = file.getName().toLowerCase();

        for (String extension : allowedFileFormats) {
            if (fname.endsWith("." + extension)) return extension;
        }

        throw new StravaException("The file you're uploading is in unsupported format");
    }

    public static MultipartForm createFileUploadForm(File file, String name, String description, SportType sportType) throws StravaException {

        return MultipartForm.newBuilder()
                .addPart("file", file.toPath())
                .addPart("data_type", guessUploadFileFormat(file))
                .addPart("name", name)
                .addPart("sport_type", sportType.getValue())
                .addPart("description", description);

    }
}


