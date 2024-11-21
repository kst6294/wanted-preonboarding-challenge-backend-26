package wanted.market.login.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wanted.market.member.domain.entity.Member;
import wanted.market.member.repository.MemberRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginService {
    private final MemberRepository memberRepository;

    public Member login(String loginId, String password) {
        Member findMember = memberRepository.findMemberByLoginIdAndPassword(loginId, password)
                .orElseThrow(()-> new RuntimeException("멤버 찾을 수 없음"));
        return findMember;
    }
}
