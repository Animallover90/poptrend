package co.kr.poptrend.security;

import co.kr.poptrend.filter.LogFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
@Slf4j
@RequiredArgsConstructor
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    private final AuthenticationProvider authenticationProvider;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf()
                .ignoringAntMatchers("/member/**", "/blogList/**")
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())//csrf 토큰자동생성
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/actuator/**").access("hasIpAddress(\"127.0.0.1\") or hasIpAddress(\"34.64.39.224\") or hasIpAddress(\"121.160.123.144\") or hasIpAddress(\"::1\")")
                .antMatchers("/", "/css/**", "/images/**", "/js/**", "/fonts/**", "/login/**").permitAll()
                .antMatchers("/member/**").hasRole("USER")
                .and()
                .addFilterBefore(new LogFilter(), SecurityContextPersistenceFilter.class) // spring security filter 맨 앞
                .addFilterBefore(new AuthenticationTokenFilter(authenticationProvider),
                        UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(new LoginPageFilter(), UsernamePasswordAuthenticationFilter.class)
                // 인증 실패(로그인 안됨)
                .exceptionHandling().authenticationEntryPoint((httpServletRequest, httpServletResponse, e) -> httpServletResponse.sendRedirect("/login"))
                // 인가 실패(로그인했지만 권한 없음)
                .accessDeniedHandler((httpServletRequest, httpServletResponse, e) -> httpServletResponse.sendRedirect("/"));
    }
}