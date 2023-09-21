package co.kr.poptrend.member.repository;

import co.kr.poptrend.member.entity.LoginProvider;
import co.kr.poptrend.member.entity.Member;
import co.kr.poptrend.member.entity.MemberStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Repository
public interface SpringDataJpaMemberRep extends JpaRepository<Member, Long> {

    Optional<Member> findByOauthId(String oauthId);
    Optional<Member> findByOauthIdAndProvider(String oauthId, LoginProvider provider);
    boolean existsMemberByNickName(String nickName);
    List<Member> findAllByModifiedDateBeforeAndStatus(LocalDateTime yesterday, MemberStatus memberStatus);
    Long deleteMemberById(Long id);
}
