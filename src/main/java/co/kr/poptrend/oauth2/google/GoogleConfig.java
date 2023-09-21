package co.kr.poptrend.oauth2.google;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class GoogleConfig {
    @Value("${oauth2.google.client.authUri}")
    private String googleOauth2AuthUrl;

    @Value("${google.client.authUri}")
    private String googleAuthUrl;

    @Value("${google.redirect-uri}")
    private String googleRedirectUrl;

    @Value("${google.client-id}")
    private String googleClientId;

    @Value("${google.client-secret}")
    private String googleSecret;

    @Value("${google.scope}")
    private String scopes;

}
