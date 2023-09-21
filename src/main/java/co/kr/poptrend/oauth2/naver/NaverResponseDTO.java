package co.kr.poptrend.oauth2.naver;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class NaverResponseDTO {

    String tokenType;
    String expiresIn;
    String error;
    String errorDescription;
    String accessToken;
    String refreshToken;

}