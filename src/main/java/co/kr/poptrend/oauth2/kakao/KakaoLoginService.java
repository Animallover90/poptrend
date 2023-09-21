package co.kr.poptrend.oauth2.kakao;

import co.kr.poptrend.error.CustomException;
import co.kr.poptrend.error.ErrorCode;
import co.kr.poptrend.oauth2.utils.OAuth2Util;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Service
@RequiredArgsConstructor
public class KakaoLoginService {

    private final KakaoConfig kakaoConfig;

    public KakaoLoginDTO getUserInfo(String code) {
        // 인가코드로 AccessCode 받아오기
        String accessToken = getAccessToken(code);

        // AccessCode로 유저 정보 받아오기
        return getUserInfoByToken(accessToken);
    }

    public String getAccessToken(String code) {
        // HttpHeaders class를 통해 header에 파라미터 추가
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");

        // MultiVauleMap으로 body부분에 파라미터 추가
        MultiValueMap<String, String> bodyParams = new LinkedMultiValueMap<>();
        bodyParams.add("grant_type", "authorization_code");
        bodyParams.add("client_id", kakaoConfig.getClientId());
        bodyParams.add("redirect_uri", kakaoConfig.getRedirectUri());
        bodyParams.add("code", code);

        ResponseEntity<String> response = OAuth2Util.httpGetPost(HttpMethod.POST, httpHeaders, bodyParams,
                kakaoConfig.getKakaoOauth2AuthUrl() + "/oauth/token", String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL); // NULL이 아닌 값만 응답받기(NULL인 경우는 생략)

        try {
            return objectMapper.readValue(response.getBody(), new TypeReference<KakaoResponseDTO>() {
            }).getAccessToken();
        } catch (JsonProcessingException e) {
            throw new CustomException(ErrorCode.ParameterError, e);
        }
    }

    public KakaoLoginDTO getUserInfoByToken(String token) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + token);
        httpHeaders.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        ResponseEntity<String> response = OAuth2Util.httpGetPost(HttpMethod.POST, httpHeaders, new LinkedMultiValueMap<>(),
                kakaoConfig.getKakaoAuthUrl() + "/v2/user/me", String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        try {
            return objectMapper.readValue(response.getBody(), new TypeReference<KakaoLoginDTO>() {
            });
        } catch (JsonProcessingException e) {
            throw new CustomException(ErrorCode.ParameterError, e);
        }
    }
}
