package co.kr.poptrend.sessionRedis;

import co.kr.poptrend.member.entity.Member;
import co.kr.poptrend.member.entity.RoleStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;


@Getter
@ToString
public class RedisDTO  {

    private String id;
    private String oauthId;
    private RoleStatus role;

    @Builder
    public RedisDTO(String id, String oauthId, RoleStatus role) {
        this.id = id;
        this.oauthId = oauthId;
        this.role = role;
    }

    public RedisDTO(Member member) {
        this.id = String.valueOf(member.getId());
        this.oauthId = member.getOauthId();
        this.role = member.getRole();
    }

    public RedisDTO() {
    }
}
