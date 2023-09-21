package co.kr.poptrend.security;

import co.kr.poptrend.member.service.MemberService;
import co.kr.poptrend.sessionRedis.RedisDTO;
import co.kr.poptrend.sessionRedis.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationProvider {

    private final MemberService memberService;
    private final UserDetailsService userDetailsService;
    private final RedisUtil redisUtil;

    // DB에서 유저 정보 조회 후 있을 경우 Authentication 생성하여 반환
    public Authentication getAuthentication(RedisDTO user) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getId());
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public void setRedisDTOToRedis(String uuid, RedisDTO user) {
        redisUtil.setDTOOps(uuid, user, 20, TimeUnit.MINUTES);
    }

    public RedisDTO getRedisDTOByRedis(String key) {
        RedisDTO redisUser = redisUtil.getDTOOps(key);
        redisUtil.deleteDTOOps(key);
        return redisUser;
    }

    public String resolveToken(HttpServletRequest request) {
        String token = null;
        Cookie cookie = WebUtils.getCookie(request, "X-AUTH-TOKEN");
        if(cookie != null) token = cookie.getValue();
        return token;
    }

    public void setCookie(HttpServletResponse response, String uuid) {
        ResponseCookie cookie = ResponseCookie.from("X-AUTH-TOKEN", uuid)
                .path("/")
                .secure(true)
                .sameSite("Lax")
                .httpOnly(true)
                .build();
        response.setHeader("Set-Cookie", cookie.toString());
    }

    public void setSessionId(Long id, String uuid) {
        memberService.findByIdSetSessionId(id, uuid);
    }
}
