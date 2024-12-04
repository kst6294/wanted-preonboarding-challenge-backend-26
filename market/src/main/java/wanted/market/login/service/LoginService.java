package wanted.market.login.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;

    public Member login(String loginId, String password) {
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new RuntimeException("멤버 찾을 수 없음"));
        if (passwordEncoder.matches(password, member.getPassword()) == true) {
            return member;
        } else {
            throw new RuntimeException("비밀번호가 일치하지 않습니다");
        }
    }
}
