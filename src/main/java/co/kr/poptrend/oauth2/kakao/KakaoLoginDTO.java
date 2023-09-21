package co.kr.poptrend.oauth2.kakao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.json.simple.JSONObject;

import java.util.Date;

@Getter
@ToString
@NoArgsConstructor
public class KakaoLoginDTO {

    private String id;
    private Date connectedAt;
    private JSONObject properties;
    private JSONObject kakaoAccount;

}
