package co.kr.poptrend.blog.entity;

import co.kr.poptrend.entity.DateEntity;
import co.kr.poptrend.member.entity.Member;
import com.sun.istack.NotNull;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Getter
public class BlogComment extends DateEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String content;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "blog_id")
    private Blog blog;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "blogComment", cascade = CascadeType.ALL)
    private List<CommentFavorite> commentFavorites = new ArrayList<>();

    @Builder
    public BlogComment(Blog blog, Member member, String content) {
        this.blog = blog;
        this.member = member;
        this.content = content;
    }

    public BlogComment() {

    }

    @Override
    public String toString() {
        return "Blog{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", createdDate='" + getCreatedDate() + '\'' +
                ", modifiedDate='" + getModifiedDate() + '\'' +
                '}';
    }

    public Map<String, Object> convertMap() {
        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("table_name", "BlogComment");
        returnMap.put("id", this.id);
        returnMap.put("content", this.content);
        returnMap.put("created_date", getCreatedDate().toString());
        returnMap.put("modified_date", getModifiedDate().toString());
        returnMap.put("blog_id", blog.getId());
        returnMap.put("member_id", member.getId());
        return returnMap;
    }
}
