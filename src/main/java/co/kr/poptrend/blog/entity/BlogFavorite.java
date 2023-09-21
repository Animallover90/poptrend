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
public class BlogFavorite {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Blog blog;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    public BlogFavorite(Blog blog, Member member) {
        this.blog = blog;
        this.member = member;
    }

    public BlogFavorite() {

    }

    public Map<String, Object> convertMap() {
        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("table_name", "BlogFavorite");
        returnMap.put("id", this.id);
        returnMap.put("blog_id", blog.getId());
        returnMap.put("member_id", member.getId());
        return returnMap;
    }
}
