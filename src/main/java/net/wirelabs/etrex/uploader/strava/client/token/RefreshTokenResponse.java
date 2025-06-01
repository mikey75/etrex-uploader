package net.wirelabs.etrex.uploader.strava.client.token;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class RefreshTokenResponse {
    
    @SerializedName("token_type")
    String tokenType;
    @SerializedName("access_token")
    String accessToken;
    @SerializedName("expires_at")
    Long expiresAt;
    @SerializedName("expires_in")
    Long expiresIn;
    @SerializedName("refresh_token")
    String refreshToken;
    

}
