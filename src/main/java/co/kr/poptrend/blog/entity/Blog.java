package co.kr.poptrend.blog.entity;

import co.kr.poptrend.entity.DateEntity;
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
public class Blog extends DateEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String title;

    @NotNull
    private String content;

    @NotNull
    private String source;

    @NotNull
    private String tag;

    @OneToMany(mappedBy = "blog", cascade = CascadeType.REMOVE)
    private List<BlogFavorite> blogFavorites = new ArrayList<>();

    @OneToMany(mappedBy = "blog", cascade = CascadeType.REMOVE)
    private List<BlogComment> blogComments = new ArrayList<>();

    @Builder
    public Blog(String title, String content, String source, String tag) {
        this.title = title;
        this.content = content;
        this.source = source;
        this.tag = tag;
    }

    public Blog() {

    }

    @Override
    public String toString() {
        return "Blog{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", source='" + source + '\'' +
                ", tag='" + tag + '\'' +
                ", createdDate='" + getCreatedDate() + '\'' +
                ", modifiedDate='" + getModifiedDate() + '\'' +
                '}';
    }

    public Map<String, Object> convertMap() {
        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("table_name", "Blog");
        returnMap.put("id", this.id);
        returnMap.put("title", this.title);
        returnMap.put("content", this.content);
        returnMap.put("source", this.source);
        returnMap.put("tag", this.tag);
        returnMap.put("created_date", getCreatedDate().toString());
        returnMap.put("modified_date", getModifiedDate().toString());
        return returnMap;
    }
}
