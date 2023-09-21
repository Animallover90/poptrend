package co.kr.poptrend.oauth2.google;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Getter
@NoArgsConstructor
@ToString
public class GoogleLoginDTO {

    private String id;
    private String email;
    private String verifiedEmail;
    private String picture;
}
