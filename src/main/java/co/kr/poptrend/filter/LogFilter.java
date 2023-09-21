package co.kr.poptrend.filter;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

@Slf4j
public class LogFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) { log.info("log filter init"); }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        try {
            // MDC LocalThread를 이용한 쓰레드별 변수(traceId)를 적용하여 LogAop와 log에서 사용
            MDC.put("traceId", UUID.randomUUID().toString());

            // 클라이언트의 IP를 구해서 MDC에 변수 적용
            String ip = getIP(request);
            MDC.put("originIP", ip);

            chain.doFilter(request,response);
        } catch(Exception e) {
            log.error("LogFilter Error : ", e);
        } finally {
            // MDC LocalThread를 이용한 쓰레드별 변수(traceId)를 적용하여 LogAop와 log에서 사용
            MDC.clear();
        }
    }

    @Override
    public void destroy() { log.info("log filter destroy"); }

    private String getIP(ServletRequest servletRequest) {
        HttpServletRequest request = (HttpServletRequest) servletRequest;

        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}