package co.kr.poptrend.member.entity;

import co.kr.poptrend.blog.entity.BlogComment;
import co.kr.poptrend.blog.entity.BlogFavorite;
import co.kr.poptrend.blog.entity.CommentFavorite;
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
@Table(uniqueConstraints =
        { @UniqueConstraint(name = "UniqueOauthIdAndProvider", columnNames = { "oauthId", "provider" })})
public class Member extends DateEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column()
    private Long id;

    @NotNull
    private String oauthId;

    @NotNull
    @Column(unique = true)
    private String nickName;

    @NotNull
    @Enumerated(EnumType.STRING)
    private RoleStatus role;

    @NotNull
    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    @NotNull
    @Enumerated(EnumType.STRING)
    private LoginProvider provider;

    private String profileImg;

    private String sessionId;

    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
    private List<BlogFavorite> blogFavorites = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
    private List<BlogComment> blogComments = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
    private List<CommentFavorite> commentFavorites = new ArrayList<>();

    @Builder
    public Member(String nickName, String oauthId, RoleStatus role, MemberStatus status, LoginProvider provider, String sessionId) {
        this.nickName = nickName;
        this.oauthId = oauthId;
        this.role = role;
        this.status = status;
        this.provider = provider;
        this.sessionId = sessionId;
    }

    public Member() {

    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public void setProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public void setStatus(MemberStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Member{" +
                "id=" + id +
                ", oauthId='" + oauthId + '\'' +
                ", nickName='" + nickName + '\'' +
                ", role=" + role +
                ", status=" + status +
                ", provider=" + provider +
                ", sessionId='" + sessionId + '\'' +
                ", profileImg='" + profileImg + '\'' +
                ", createdDate='" + getCreatedDate() + '\'' +
                ", modifiedDate='" + getModifiedDate() + '\'' +
                '}';
    }

    public Map<String, Object> convertMap() {
        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("table_name", "Member");
        returnMap.put("id", this.id);
        returnMap.put("oauth_id", this.oauthId);
        returnMap.put("nick_name", this.nickName);
        returnMap.put("role", this.role.toString());
        returnMap.put("status", this.status.toString());
        returnMap.put("provider", this.provider.toString());
        returnMap.put("session_id", this.sessionId);
        returnMap.put("profile_img", this.profileImg);
        returnMap.put("created_date", getCreatedDate().toString());
        returnMap.put("modified_date", getModifiedDate().toString());
        return returnMap;
    }
}
