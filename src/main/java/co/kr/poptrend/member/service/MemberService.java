package co.kr.poptrend.member.service;

import co.kr.poptrend.bigquery.BigqueryService;
import co.kr.poptrend.error.CustomException;
import co.kr.poptrend.error.ErrorCode;
import co.kr.poptrend.member.entity.LoginProvider;
import co.kr.poptrend.member.entity.Member;
import co.kr.poptrend.member.entity.MemberStatus;
import co.kr.poptrend.member.entity.RoleStatus;
import co.kr.poptrend.member.repository.MemberRepositoryImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Transactional // try catch로 잡아서 RuntimeException 이외의 것은 반환하지 말 것
@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepositoryImpl memberRepository;
    private final BigqueryService bigqueryService;

    public Member save(String oauthId, LoginProvider provider, String sessionId) {
        String randomStr = createRandomStr();
        Member member = Member.builder()
                .oauthId(oauthId)
                .nickName(randomStr)
                .role(RoleStatus.ROLE_USER)
                .status(MemberStatus.ACTIVE)
                .provider(provider)
                .sessionId(sessionId)
                .build();
        memberRepository.save(member);
        return member;
    }

    public Member findById(Long id) {
        Optional<Member> member = memberRepository.findById(id);
        return member.orElse(null);
    }

    public Map<String, Object> updateProfileById(Long id, String imageURL, String nickName) {
        HashMap<String, Object> result = new HashMap<>();
        Optional<Member> findMember = memberRepository.findById(id);
        Member member = findMember.orElse(null);
        if (member != null) {
            if (imageURL != null) {
                member.setProfileImg(imageURL);
            }

            if (!nickName.equals(member.getNickName())) {
                if (memberRepository.existsMemberByNickName(nickName)) {
                    result.put("result", "210");
                    return result;
                }
                member.setNickName(nickName);
            }
            result.put("result", "200");
        }
        return result;
    }

    public Member findBydOauthIdAndProvider(String oauthId, LoginProvider provider) {
        Optional<Member> member = memberRepository.findByOauthIdAndProvider(oauthId, provider);
        return member.orElse(null);
    }

    public Member updateMemberInactive(Long id) {
        Optional<Member> member = memberRepository.findById(id);
        if (member.isPresent()) {
            member.get().setStatus(MemberStatus.INACTIVE);
            return member.get();
        } else {
            return null;
        }
    }

    public boolean isActiveMember(Long id) {
        boolean isActive = false;
        Optional<Member> member = memberRepository.findById(id);
        if (member.isPresent() && member.get().getStatus().equals(MemberStatus.ACTIVE)) {
            isActive = true;
        }
        return isActive;
    }

    public Long deleteMemberById(Long id) {
        return memberRepository.deleteMemberById(id);
    }

    public void findByIdSetSessionId(Long id, String sessionId) {
        Optional<Member> member = memberRepository.findById(id);
        member.ifPresent(value -> value.setSessionId(sessionId));
    }

    public void delete1dayAgoWithdrawMemberAndBackup(LocalDateTime yesterday) {
        List<Member> memberList = memberRepository.findAllByModifiedDateBeforeAndStatus(yesterday, MemberStatus.INACTIVE);
        if (memberList.isEmpty()) {
            return;
        }

        List<Map<String, Object>> MemberMapData = new ArrayList<>();
        for (Member m : memberList) {
            Long aLong = memberRepository.deleteMemberById(m.getId());
            if (aLong != 1) {
                throw new CustomException(ErrorCode.UnExpectedError);
            }

            MemberMapData.add(m.convertMap());
        }

        bigqueryService.insertDataInBigquery(MemberMapData, "Member");
    }

    public void findByOauthAndProviderSetSessionId(String oauthId, LoginProvider provider, String sessionId) {
        Optional<Member> member = memberRepository.findByOauthIdAndProvider(oauthId, provider);
        member.ifPresent(value -> value.setSessionId(sessionId));
    }

    private String createRandomStr() {
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("USER.");
        for (int i = 0; i < 15; i++) {
            // Random.nextBoolean() : 랜덤으로 true, false 리턴 (true : 랜덤 소문자 영어, false : 랜덤 숫자)
            if (random.nextBoolean()) {
                // 26 : a-z 알파벳 개수
                // 97 : letter 'a' 아스키코드
                // (int)(random.nextInt(26)) + 97 : 랜덤 소문자 아스키코드
                stringBuilder.append((char)(random.nextInt(26) + 97));
            } else {
                stringBuilder.append(random.nextInt(10));
            }
        }
        return stringBuilder.toString();
    }
}
