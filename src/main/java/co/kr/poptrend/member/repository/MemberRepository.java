package co.kr.poptrend.member.repository;

import co.kr.poptrend.member.entity.LoginProvider;
import co.kr.poptrend.member.entity.Member;
import co.kr.poptrend.member.entity.MemberStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MemberRepository {
    Long save(Member member);

    Optional<Member> findById(Long id);

    Optional<Member> findByOauthId(String oauthId);

    Optional<Member> findByOauthIdAndProvider(String oauthId, LoginProvider provider);

    Long deleteMemberById(Long id);

    boolean existsMemberByNickName(String nickName);

    List<Member> findAllByModifiedDateBeforeAndStatus(LocalDateTime yesterday, MemberStatus memberStatus);
}
