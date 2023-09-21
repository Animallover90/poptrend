package co.kr.poptrend.oauth2.utils;

import co.kr.poptrend.error.CustomException;
import co.kr.poptrend.error.ErrorCode;
import co.kr.poptrend.error.UnexpectedException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

public class OAuth2Util {

    public static ResponseEntity<String> httpGetPost(HttpMethod getPostType, HttpHeaders headParams, MultiValueMap<String, String> bodyParams, String url, Class returnType) {

        // spring에서 지원하는 http 전송 class RestTemplate
        RestTemplate restTemplate = new RestTemplate();

        // header와 body파라미터를 가지고 HttpEntity class객체 생성
        HttpEntity<MultiValueMap<String, String>> requestParams = new HttpEntity<>(bodyParams, headParams);

        // restTemplate.exchange로 http 전송하고 ResponseEntity으로 지정한 class로 반환받음
        // get일 경우 requestParams가 null이어도 된다
        // host가 잘못될 경우 IOException도 발생하니 Exception으로 받아 처리
        try {
            return restTemplate.exchange(url, getPostType, requestParams, returnType);
        } catch (HttpStatusCodeException e) {
            throw new CustomException(ErrorCode.OAuth2HTTPError, e);
        } catch (Exception ee) {
            throw new UnexpectedException(ee);
        }
    }
}
