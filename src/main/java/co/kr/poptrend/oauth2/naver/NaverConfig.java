package co.kr.poptrend.oauth2.naver;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class NaverConfig {

    @Value("${naver.client-id}")
    private String clientId;

    @Value("${naver.client-secret}")
    private String clientSecret;

    @Value("${naver.redirect-uri}")
    private String redirectUri;

    @Value("${naver.token-uri}")
    private String naverOauth2AuthUrl;

    @Value("${naver.user-info-uri}")
    private String naverAuthUrl;

}
