package co.kr.poptrend.member.controller;

import co.kr.poptrend.blog.entity.BlogResponseDTO;
import co.kr.poptrend.blog.service.BlogService;
import co.kr.poptrend.googleStorage.GCSService;
import co.kr.poptrend.member.entity.Member;
import co.kr.poptrend.member.entity.MemberDTO;
import co.kr.poptrend.member.service.MemberService;
import io.micrometer.core.lang.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final BlogService blogService;
    private final GCSService gcsService;

    @GetMapping("/profile")
    public String profileMain(Model model, Principal principal) {
        List<BlogResponseDTO> blogResponseDTOS = blogService.pagingBlogFavoriteAllByMemberIdOrderBlogDESC(1,
                Long.valueOf(principal.getName()));
        if (blogResponseDTOS.isEmpty()) {
            model.addAttribute("blogs", null);
        } else {
            model.addAttribute("blogs", blogResponseDTOS);
        }

        model.addAttribute("profile", new MemberDTO(memberService.findById(Long.valueOf(principal.getName()))));
        return "profile/profile";
    }

    @GetMapping("/blogList/{startNum}")
    public String memberBlogList(Model model, @PathVariable int startNum, Principal principal) {
        model.addAttribute("blogs", blogService.pagingBlogFavoriteAllByMemberIdOrderBlogDESC(startNum,
                Long.valueOf(principal.getName())));
        return "index::#contentsChild";
    }

    @PostMapping("/profile")
    @ResponseBody
    public Map<String, Object> updateProfile(@Validated @ModelAttribute("profile") MemberDTO profile, BindingResult bindingResult, @Nullable @RequestPart MultipartFile image, Principal principal) {
        HashMap<String, Object> result = new HashMap<>();

        if (bindingResult.hasErrors()) {
            result.put("result", "310");
            result.put("message", bindingResult.getAllErrors().get(0).getDefaultMessage());
            return result;
        }

        if (image != null) {
            if (image.getSize() > 512000) {
                result.put("result", "300");
                return result;
            }

            String fileURL = gcsService.uploadFileToGCS(image);
            return memberService.updateProfileById(Long.valueOf(principal.getName()), fileURL , profile.getProfileNickName());
        } else {
            return memberService.updateProfileById(Long.valueOf(principal.getName()),null , profile.getProfileNickName());
        }
    }

    @GetMapping("/withdraw")
    public String withdraw(Principal principal, RedirectAttributes redirectAttributes) {
        Member member = memberService.updateMemberInactive(Long.valueOf(principal.getName()));
        if (member == null) {
            redirectAttributes.addFlashAttribute("status", "220");
            return "redirect:/";
        }

        return "redirect:/member/logout";
    }
}
