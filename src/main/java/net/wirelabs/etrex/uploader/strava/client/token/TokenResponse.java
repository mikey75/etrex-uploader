package net.wirelabs.etrex.uploader.strava.client.token;


import com.google.gson.annotations.SerializedName;
import com.strava.model.SummaryAthlete;
import lombok.Getter;

@Getter
public class TokenResponse {
    @SerializedName("token_type")
    String tokenType;
    @SerializedName("expires_at")
    Long expiresAt;
    @SerializedName("expires_in")
    Long expiresIn;
    @SerializedName("access_token")
    String accessToken;
    @SerializedName("refresh_token")
    String refreshToken;
    @SerializedName("athlete")
    SummaryAthlete athlete;
}