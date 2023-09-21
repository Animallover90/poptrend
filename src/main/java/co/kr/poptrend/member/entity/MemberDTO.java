package co.kr.poptrend.member.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@ToString
@Getter
@Setter
public class MemberDTO {

    @NotNull
    private String profileImg;
    @NotNull
    @Size(min = 2, max = 20, message = "닉네임은 2~20 사이입니다.")
    private String profileNickName;

    public MemberDTO(Member member) {
        this.profileImg = (member.getProfileImg() == null) ? "/images/guest.png" : member.getProfileImg();
        this.profileNickName = member.getNickName();
    }

    public MemberDTO() {
    }
}
