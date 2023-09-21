package co.kr.poptrend.oauth2.kakao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class KakaoResponseDTO {

    String tokenType;
    String nickName;
    String expiresIn;
    String refreshTokenExpiresIn;
    String scope;
    String accessToken;
    String refreshToken;

}