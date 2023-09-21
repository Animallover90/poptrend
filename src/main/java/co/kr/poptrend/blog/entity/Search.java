package co.kr.poptrend.blog.entity;

import co.kr.poptrend.entity.DateEntity;
import com.sun.istack.NotNull;
import lombok.Getter;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Entity
@Getter
public class Search extends DateEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String term;

    @Column(columnDefinition = "integer default 1")
    private int count;

    public Search(String term) {
        this.term = term;
    }

    public Search() {

    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "Search{" +
                "id=" + id +
                ", term='" + term + '\'' +
                ", count=" + count +
                ", createdDate='" + getCreatedDate() + '\'' +
                ", modifiedDate='" + getModifiedDate() + '\'' +
                '}';
    }

    public Map<String, Object> convertMap() {
        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("table_name", "Search");
        returnMap.put("id", this.id);
        returnMap.put("term", this.term);
        returnMap.put("count", this.count);
        returnMap.put("created_date", getCreatedDate().toString());
        returnMap.put("modified_date", getModifiedDate().toString());
        return returnMap;
    }
}
