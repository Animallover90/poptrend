package co.kr.poptrend.blog.entity;

import co.kr.poptrend.member.entity.Member;
import lombok.Getter;
import lombok.ToString;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Entity
@Getter
@ToString
public class CommentFavorite {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private BlogComment blogComment;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    public CommentFavorite(BlogComment blogComment, Member member) {
        this.blogComment = blogComment;
        this.member = member;
    }

    public CommentFavorite() {

    }

    public Map<String, Object> convertMap() {
        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("table_name", "CommentFavorite");
        returnMap.put("id", this.id);
        returnMap.put("blog_comment_id", blogComment.getId());
        returnMap.put("member_id", member.getId());
        return returnMap;
    }
}
