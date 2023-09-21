package co.kr.poptrend.member.repository;

import co.kr.poptrend.member.entity.LoginProvider;
import co.kr.poptrend.member.entity.Member;
import co.kr.poptrend.member.entity.MemberStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {

    private final SpringDataJpaMemberRep sem;

    @Override
    public Long save(Member member) {
        sem.save(member);
        return member.getId();
    }

    @Override
    public Optional<Member> findById(Long id) {
        return sem.findById(id);
    }

    @Override
    public Optional<Member> findByOauthId(String oauthId) {
        return sem.findByOauthId(oauthId);
    }

    @Override
    public Optional<Member> findByOauthIdAndProvider(String oauthId, LoginProvider provider) {
        return sem.findByOauthIdAndProvider(oauthId, provider);
    }

    @Override
    public Long deleteMemberById(Long id) {
        return sem.deleteMemberById(id);
    }

    @Override
    public boolean existsMemberByNickName(String nickName) {
        return sem.existsMemberByNickName(nickName);
    }

    @Override
    public List<Member> findAllByModifiedDateBeforeAndStatus(LocalDateTime yesterday, MemberStatus memberStatus) {
        return sem.findAllByModifiedDateBeforeAndStatus(yesterday, memberStatus);
    }
}

