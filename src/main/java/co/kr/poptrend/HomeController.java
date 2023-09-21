package co.kr.poptrend;

import co.kr.poptrend.blog.entity.*;
import co.kr.poptrend.blog.service.BlogService;
import co.kr.poptrend.member.entity.LoginProvider;
import co.kr.poptrend.member.entity.Member;
import co.kr.poptrend.member.entity.MemberDTO;
import co.kr.poptrend.member.service.MemberService;
import co.kr.poptrend.utility.ValidationUtil;
import io.micrometer.core.lang.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final BlogService blogService;
    private final MemberService memberService;

    @GetMapping("/")
    public String home(Model model, @Nullable @RequestParam String tag, @Nullable @RequestParam String search, Principal principal) {
        if (tag != null && search != null) {
            return "redirect:/";
        }

        if (tag != null) {
            if (ValidationUtil.checkSpace(tag)) {
                return "redirect:/";
            }
            blogService.saveSearch(tag);
            model.addAttribute("blogs", blogService.findAllByTagOrderByCreatedDateDesc(0, tag));
        } else if (search != null) {
            if (ValidationUtil.checkSpace(search) || search.length() > 15) {
                return "redirect:/";
            }
            blogService.saveSearch(search);
            model.addAttribute("blogs", blogService.findAllBySearchParamOrderByCreatedDateDesc(0, search, search, search));
        } else {
            model.addAttribute("blogs", blogService.pagingBlogAllOrderDESC(0));
        }

        model.addAttribute("ranking",
                (blogService.findTop5ByCreatedDateAfterOrderByCountDesc(LocalDateTime.now().minusDays(1L)))
                .stream().map(Search::getTerm).collect(Collectors.toList()));

        if (principal != null && memberService.isActiveMember(Long.valueOf(principal.getName()))) {
            model.addAttribute("profile", new MemberDTO(memberService.findById(Long.valueOf(principal.getName()))));
        }
        return "index";
    }

//    @PostConstruct
    void init() {
        for (int i = 0; i < 15; i++) {
            Blog blog = Blog.builder()
                    .title((i < 10 ? "홍제동" + i : "서대문구" + i))
                    .content((i < 10 ? "서대문구" + i : "홍제동" + i))
                    .source("https://www.naver.com")
                    .tag((i < 10 ? "서울" : "부산"))
                    .build();

            Blog savedBlog = blogService.saveBlog(blog);

            Member member = memberService.save(UUID.randomUUID().toString().toUpperCase(), LoginProvider.GOOGLE, "aaababxcvzxvsdf");
            Member member2 = memberService.save(UUID.randomUUID().toString().toUpperCase(), LoginProvider.GOOGLE, "aaababxcvzxvsdf");

            BlogComment blogComment = new BlogComment(savedBlog, member, "ㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁ");

            blogService.saveBlogComment(blogComment);

            BlogComment blogComment2 = new BlogComment(savedBlog, member2, "ㄴㄴㄴㄴㄴㄴㄴㄴㄴㄴㄴㄴㄴㄴㄴㄴㄴㄴㄴ");

            blogService.saveBlogComment(blogComment2);

            BlogFavorite blogFavorite = new BlogFavorite(savedBlog, member);

            blogService.saveBlogFavorite(blogFavorite);

            BlogFavorite blogFavorite2 = new BlogFavorite(savedBlog, member2);

            blogService.saveBlogFavorite(blogFavorite2);

            Member member3 = memberService.save(UUID.randomUUID().toString().toUpperCase(), LoginProvider.GOOGLE, "aaababxcvzxvsdf");

            CommentFavorite commentFavorite = new CommentFavorite(blogComment, member3);

            blogService.saveCommentFavorite(commentFavorite);
        }
        blogService.saveSearch("서대문구");
        blogService.saveSearch("서대문구");
        blogService.saveSearch("서대문구");
        blogService.saveSearch("서대문구");
        blogService.saveSearch("홍제동");
        blogService.saveSearch("홍제동");
        blogService.saveSearch("서울");
        blogService.saveSearch("서울");
        blogService.saveSearch("한국");
        blogService.saveSearch("지구");
        blogService.saveSearch("우주");
    }
}
