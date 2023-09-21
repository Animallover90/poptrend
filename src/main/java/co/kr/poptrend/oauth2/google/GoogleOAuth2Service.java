package co.kr.poptrend.oauth2.google;

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

import javax.servlet.http.HttpServletRequest;

@Service
@RequiredArgsConstructor
public class GoogleOAuth2Service {

    private final GoogleConfig googleConfigUtils;

    public GoogleLoginDTO getUserInfo(HttpServletRequest request) {
        // 인가코드로 AccessCode 받아오기
        GoogleResponseDTO googleResponseDTO = authByGoogleCode(request);

        // AccessCode로 유저 정보 받아오기
        return getGoogleUserInfo(googleResponseDTO);
    }

    // callback 받은 code로 구글에 token id를 받아옴
    public GoogleResponseDTO authByGoogleCode(HttpServletRequest request) throws CustomException {
        String code = request.getParameter("code");

        HttpHeaders headParam = new HttpHeaders();
        headParam.add("Content-type", "application/x-www-form-urlencoded");

        MultiValueMap<String, String> bodyParam = new LinkedMultiValueMap<>();
        bodyParam.add("code", code);
        bodyParam.add("client_id", googleConfigUtils.getGoogleClientId());
        bodyParam.add("client_secret", googleConfigUtils.getGoogleSecret());
        bodyParam.add("redirect_uri", googleConfigUtils.getGoogleRedirectUrl());
        bodyParam.add("grant_type", "authorization_code");

        ResponseEntity<String> res = OAuth2Util.httpGetPost(HttpMethod.POST, headParam, bodyParam,
                googleConfigUtils.getGoogleAuthUrl() + "/oauth2/v4/token", String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL); // NULL이 아닌 값만 응답받기(NULL인 경우는 생략)

        try {
            return objectMapper.readValue(res.getBody(), new TypeReference<GoogleResponseDTO>() {
            });
        } catch (JsonProcessingException e) {
            throw new CustomException(ErrorCode.ParameterError, e);
        }
    }

    // Access Token으로 유저 정보를 불러옴 email, name, picture 등
    public GoogleLoginDTO getGoogleUserInfo(GoogleResponseDTO googleLoginResponse) {
        String accessToken = googleLoginResponse.getAccessToken();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + accessToken);
        ResponseEntity<String> res = OAuth2Util.httpGetPost(HttpMethod.GET, httpHeaders, new LinkedMultiValueMap<>(),
                googleConfigUtils.getGoogleAuthUrl() + "/oauth2/v2/userinfo", String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL); // NULL이 아닌 값만 응답받기(NULL인 경우는 생략)

        try {
            return objectMapper.readValue(res.getBody(), new TypeReference<GoogleLoginDTO>() {
            });
        } catch (JsonProcessingException e) {
            throw new CustomException(ErrorCode.ParameterError, e);
        }
    }
}