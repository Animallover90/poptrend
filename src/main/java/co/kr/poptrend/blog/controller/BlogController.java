package co.kr.poptrend.blog.controller;

import co.kr.poptrend.blog.entity.*;
import co.kr.poptrend.blog.service.BlogService;
import co.kr.poptrend.member.entity.Member;
import co.kr.poptrend.member.service.MemberService;
import co.kr.poptrend.utility.ValidationUtil;
import io.micrometer.core.lang.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;

@Slf4j
@Controller
@RequiredArgsConstructor
public class BlogController {

    private final MemberService memberService;
    private final BlogService blogService;

    @GetMapping("/blogList/{startNum}")
    public String blogList(Model model, @PathVariable int startNum, @Nullable @RequestParam String tag, @Nullable @RequestParam String search) {
        if (tag != null && search != null) {
            return "redirect:/";
        }

        if (tag != null) {
            if (ValidationUtil.checkSpace(tag)) {
                return "redirect:/";
            }
            model.addAttribute("blogs", blogService.findAllByTagOrderByCreatedDateDesc(startNum, tag));
        } else if (search != null) {
            if (ValidationUtil.checkSpace(search) || search.length() > 15) {
                return "redirect:/";
            }
            blogService.saveSearch(search);
            model.addAttribute("blogs", blogService.findAllBySearchParamOrderByCreatedDateDesc(startNum, search, search, search));
        } else {
            model.addAttribute("blogs", blogService.pagingBlogAllOrderDESC(startNum));
        }

        return "index::#contentsChild";
    }

    @PostMapping("/member/like")
    @ResponseBody
    public Map<String, Object> blogLike(@RequestBody Map<String, Object> data, Principal principal) {
        HashMap<String, Object> result = new HashMap<>();
        if (!ValidationUtil.isContainKeyAndValue(data, new ArrayList<>(Collections.singletonList("id")))) {
            result.put("result", "300");
            return result;
        }

        Long blogId = Long.valueOf(data.get("id").toString());

        BlogFavorite existBlogFavorite = blogService.findBlogFavoriteByMemberIdAndBlogId(
                Long.valueOf(principal.getName()), blogId);
        if (existBlogFavorite == null) {
            Member member = memberService.findById(Long.valueOf(principal.getName()));
            Blog blog = blogService.findBlogById(blogId);

            BlogFavorite blogFavorite = blogService.saveBlogFavorite(new BlogFavorite(blog, member));
            if (blogFavorite.getId() != null) {
                result.put("count", blogService.countBlogFavoritesByBlogId(blog.getId()));
                result.put("result", "200");
            } else {
                result.put("result", "300");
            }
        } else {
            Long deleteResult = blogService.deleteBlogFavoriteById(existBlogFavorite.getId());
            if (deleteResult == 1L) {
                result.put("count", blogService.countBlogFavoritesByBlogId(blogId));
                result.put("result", "210");
            } else {
                result.put("result", "300");
            }
        }

        return result;
    }

    @PostMapping("/member/comment/like")
    @ResponseBody
    public Map<String, Object> commentLike(@RequestBody Map<String, Object> data, Principal principal) {
        HashMap<String, Object> result = new HashMap<>();
        if (!ValidationUtil.isContainKeyAndValue(data, new ArrayList<>(Collections.singletonList("id")))) {
            result.put("result", "300");
            return result;
        }

        Long commentId = Long.valueOf(data.get("id").toString());

        CommentFavorite existCommentFavorite =
                blogService.findCommentFavoriteByMemberIdAndBlogCommentId(Long.valueOf(principal.getName()), commentId);
        if (existCommentFavorite == null) {
            Member member = memberService.findById(Long.valueOf(principal.getName()));

            BlogComment blogComment = blogService.findBlogCommentById(commentId);
            if (blogComment.getMember().getId() == Long.valueOf(principal.getName())) {
                result.put("result", "320");
                return result;
            }

            CommentFavorite commentFavorite = blogService.saveCommentFavorite(new CommentFavorite(blogComment, member));
            if (commentFavorite.getId() != null) {
                result.put("count", blogService.countCommentFavoritesByBlogCommentId(blogComment.getId()));
                result.put("result", "200");
            } else {
                result.put("result", "300");
            }
        } else {
            Long deleteResult = blogService.deleteCommentFavoriteById(existCommentFavorite.getId());
            if (deleteResult == 1L) {
                result.put("count", blogService.countCommentFavoritesByBlogCommentId(commentId));
                result.put("result", "210");
            } else {
                result.put("result", "300");
            }
        }

        return result;
    }

    @PostMapping("/member/comment/delete")
    @ResponseBody
    public Map<String, Object> commentDelete(@RequestBody Map<String, Object> data, Principal principal) {
        HashMap<String, Object> result = new HashMap<>();
        if (!ValidationUtil.isContainKeyAndValue(data, new ArrayList<>(Collections.singletonList("id")))) {
            result.put("result", "300");
            return result;
        }

        Long commentId = Long.valueOf(data.get("id").toString());

        BlogComment blogCommentById = blogService.findBlogCommentById(commentId);
        if (blogCommentById == null) {
            result.put("result", "300");
            return result;
        }

        if (!Long.valueOf(principal.getName()).equals(blogCommentById.getMember().getId())) {
            result.put("result", "310");
            return result;
        }

        long blogId = blogCommentById.getBlog().getId();

        Long deleteResult = blogService.deleteBlogCommentById(commentId);
        if (deleteResult != 1L) {
            result.put("result", "300");
            return result;
        }

        result.put("result", "200");
        result.put("count", blogService.countBlogCommentByBlogId(blogId));

        return result;
    }

    @PostMapping("/member/comment")
    @ResponseBody
    public Map<String, Object> commentSave(@RequestBody Map<String, Object> data, Principal principal) {
        HashMap<String, Object> result = new HashMap<>();
        if (!ValidationUtil.isContainKeyAndValue(data, new ArrayList<>(Arrays.asList("id", "comment")))) {
            result.put("result", "300");
            return result;
        }

        Long blogId = Long.valueOf(data.get("id").toString());
        String comment = data.get("comment").toString();

        BlogComment blogComment = blogService.saveBlogComment(
                new BlogComment(blogService.findBlogById(blogId),
                        memberService.findById(Long.valueOf(principal.getName())),
                        comment)
        );

        if (blogComment.getId() == null) {
            result.put("result", "300");
            return result;
        }

        result.put("result", "200");
        result.put("count", blogService.countBlogCommentByBlogId(blogId));
        result.put("commentResult", new CommentResponseDTO(blogComment));

        return result;
    }
}
