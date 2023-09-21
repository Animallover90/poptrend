package co.kr.poptrend.security;

import co.kr.poptrend.aop.LogAop;
import co.kr.poptrend.sessionRedis.RedisDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class AuthenticationTokenFilter extends OncePerRequestFilter {

    private final AuthenticationProvider authenticationProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String token = authenticationProvider.resolveToken(request);
        if (token != null && !isStaticURL(request)) {
            RedisDTO redisUser = authenticationProvider.getRedisDTOByRedis(token);
            if (redisUser != null) {
                try {
                    MDC.put("userID", redisUser.getOauthId());
                    Authentication authentication = authenticationProvider.getAuthentication(redisUser);
                    String uuid = String.valueOf(UUID.randomUUID()).replace("-", "").toUpperCase();
                    authenticationProvider.setRedisDTOToRedis(uuid, redisUser);
                    authenticationProvider.setCookie(response, uuid);
                    authenticationProvider.setSessionId(Long.valueOf(redisUser.getId()), uuid);

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } catch (Exception e) {
                    log.warn("ip={}||user_id={}||class_name={}||message={}||exception={}",
                            MDC.get("originIP"), MDC.get("userID"), "AuthenticationTokenFilter", e.getMessage(), LogAop.getStackTrace(e));
                    response.sendRedirect("/");
                    return; // filter에서 return을 하지 않으면 문제가 있을 때 계속 다음으로 넘어가 Controller로 전달된다.
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private Boolean isStaticURL(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        List<String> staticURL = List.of("/css", "/images", "/fonts", "/js");
        boolean returnData = false;
        for (String url : staticURL) {
            if (requestURI.startsWith(url)) {
                returnData = true;
                break;
            }
        }

        return returnData;
    }
}
