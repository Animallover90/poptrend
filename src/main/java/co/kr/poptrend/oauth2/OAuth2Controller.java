package co.kr.poptrend.oauth2;

import co.kr.poptrend.error.CustomException;
import co.kr.poptrend.error.ModelCustomException;
import co.kr.poptrend.member.entity.LoginProvider;
import co.kr.poptrend.member.entity.Member;
import co.kr.poptrend.member.entity.MemberStatus;
import co.kr.poptrend.member.service.MemberService;
import co.kr.poptrend.oauth2.google.GoogleLoginDTO;
import co.kr.poptrend.oauth2.google.GoogleOAuth2Service;
import co.kr.poptrend.oauth2.kakao.KakaoLoginDTO;
import co.kr.poptrend.oauth2.kakao.KakaoLoginService;
import co.kr.poptrend.oauth2.naver.NaverLoginDTO;
import co.kr.poptrend.oauth2.naver.NaverLoginService;
import co.kr.poptrend.sessionRedis.RedisDTO;
import co.kr.poptrend.sessionRedis.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Controller
@RequiredArgsConstructor
public class OAuth2Controller {

    private final MemberService memberService;
    private final GoogleOAuth2Service googleOAuth2Service;
    private final KakaoLoginService kakaoLoginService;
    private final NaverLoginService naverLoginService;
    private final RedisUtil redisUtil;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/login/google/redirect")
    public String googleRedirect(HttpServletRequest request, RedirectAttributes redirectAttributes, HttpServletResponse response) {
        try {
            GoogleLoginDTO googleUserInfo = googleOAuth2Service.getUserInfo(request);

            return loginProcess(response, redirectAttributes, googleUserInfo.getId(), LoginProvider.GOOGLE);

        } catch (CustomException e) {
            throw new ModelCustomException(e.getThrowable(), "/");
        } catch (IOException e) {
            return "error/500";
        }
    }

    @GetMapping("/login/kakao/redirect")
    public String kakaoRedirect(HttpServletResponse response, RedirectAttributes redirectAttributes, String code) {
        try {
            KakaoLoginDTO kakaoUserInfo = kakaoLoginService.getUserInfo(code);

            return loginProcess(response, redirectAttributes, kakaoUserInfo.getId(), LoginProvider.KAKAO);

        } catch (CustomException e) {
            throw new ModelCustomException(e.getThrowable(), "/");
        } catch (IOException e) {
            return "error/500";
        }
    }

    @GetMapping("/login/naver/redirect")
    public String naverRedirect(HttpServletResponse response, RedirectAttributes redirectAttributes, String code) {
        try {
            NaverLoginDTO naverUserInfo = naverLoginService.getUserInfo(code);

            return loginProcess(response, redirectAttributes, naverUserInfo.getId(), LoginProvider.NAVER);

        } catch (CustomException e) {
            throw new ModelCustomException(e.getThrowable(), "/");
        } catch (IOException e) {
            return "error/500";
        }
    }

    @GetMapping("/member/logout")
    public String logOut(HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie = WebUtils.getCookie(request, "X-AUTH-TOKEN");
        if (cookie != null) {
            String token = cookie.getValue();
            redisUtil.deleteDTOOps(token);
            deleteCookie(response);
            return "redirect:/";
        }

        return "redirect:/";
    }

    private String loginProcess(HttpServletResponse response, RedirectAttributes redirectAttributes, String id, LoginProvider provider) throws IOException {
        Member member = memberService.findBydOauthIdAndProvider(id, provider);

        String uuid = String.valueOf(UUID.randomUUID()).replace("-", "").toUpperCase();
        if (member == null) {
            Member savedMember = memberService.save(id, provider, uuid);
            if (savedMember.getId() == null) {
                return "redirect:/";
            }

            setSessionAndCookie(response, savedMember, uuid);
            return "redirect:/";
        }

        if (member.getStatus().equals(MemberStatus.INACTIVE)) {
            redirectAttributes.addFlashAttribute("status", "250");
            return "redirect:/";
        }

        memberService.findByIdSetSessionId(member.getId(), uuid);
        setSessionAndCookie(response, member, uuid);
        return "redirect:/";
    }

    private void setSessionAndCookie(HttpServletResponse response, Member member, String uuid) {
        RedisDTO redisDTO = new RedisDTO(member);
        redisUtil.setDTOOps(uuid, redisDTO, 30, TimeUnit.SECONDS);

        ResponseCookie cookie = ResponseCookie.from("X-AUTH-TOKEN", uuid)
                .path("/")
                .secure(true)
                .sameSite("Lax")
                .httpOnly(true)
                .build();
        response.setHeader("Set-Cookie", cookie.toString());
    }

    private void deleteCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("X-AUTH-TOKEN", null)
                .maxAge(0)
                .path("/")
                .secure(true)
                .sameSite("Lax")
                .httpOnly(true)
                .build();
        response.setHeader("Set-Cookie", cookie.toString());
    }
}
