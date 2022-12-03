package net.wirelabs.etrex.uploader.strava.authorizer;


import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import net.wirelabs.etrex.uploader.model.strava.SummaryAthlete;

@Getter
@Setter
public class AuthResponse {
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