package wanted.market.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wanted.market.member.domain.dto.request.MemberRequestDto;
import wanted.market.member.domain.dto.request.NewPasswordRequestDto;
import wanted.market.member.domain.dto.response.ResetPasswordResultResponseDto;
import wanted.market.member.domain.dto.response.service.ResetPasswordResultServiceDto;
import wanted.market.member.domain.entity.Member;
import wanted.market.member.repository.MemberRepository;


@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Member memberJoin(MemberRequestDto requestDto) {
        if (memberRepository.findByLoginId(requestDto.getLogin_id()).isPresent()) {
            throw new RuntimeException("회원 중복으로 인한 회원가입 실패");
        }
        Member member = Member.builder()
                .loginId(requestDto.getLogin_id())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .memberName(requestDto.getUsername())
                .email(requestDto.getEmail())
                .build();
        return memberRepository.save(member);

    }

    @Transactional
    public Member findOne(Long memberId) {
        return memberRepository.findById(memberId).get();
    }

    @Transactional
    public boolean deleteMember(MemberRequestDto requestDto) {
        Member member = memberRepository.findMemberByLoginIdAndPassword(requestDto.getLogin_id(), requestDto.getPassword()).orElseThrow(() -> new RuntimeException("일치하는 회원 없음"));
        memberRepository.delete(member);
        return true;
    }

    @Transactional
    public ResetPasswordResultServiceDto resetNewPassword(String newPassword, Long userId) {
        try {
            Member findMember = memberRepository.findById(userId).orElseThrow(() -> new RuntimeException("해당 계정을 찾을 수 없습니다."));
            findMember.setPassword(passwordEncoder.encode(newPassword));
            log.info("DB 에서 pw 변경 완료");
            return new ResetPasswordResultServiceDto(findMember.getId(), true);
        } catch (RuntimeException e) {
            return new ResetPasswordResultServiceDto(null, false);
        }
    }

}
