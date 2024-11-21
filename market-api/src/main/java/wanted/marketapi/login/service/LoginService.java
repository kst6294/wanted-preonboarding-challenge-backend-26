package wanted.marketapi.login.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wanted.marketapi.domain.member.Member;
import wanted.marketapi.repository.MemberRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginService {
    private final MemberRepository memberRepository;

    public Member login(String loginId, String password) {
        List<Member> findMember = memberRepository.findByLoginIdAndPassword(loginId, password);
        if (findMember.isEmpty()) {
            return null;
        } else {
            return findMember.get(0);
        }
    }
}
