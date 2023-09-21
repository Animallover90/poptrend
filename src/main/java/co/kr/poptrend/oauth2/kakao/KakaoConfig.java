package co.kr.poptrend.oauth2.kakao;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class KakaoConfig {

    @Value("${kakao_client_id}")
    private String clientId;

    @Value("${kakao_redirect_uri}")
    private String redirectUri;

    @Value("${kakao.oauth2.client.authUri}")
    private String kakaoOauth2AuthUrl;

    @Value("${kakao.client.authUri}")
    private String kakaoAuthUrl;

}
