package wanted.market.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wanted.market.member.domain.dto.request.MemberRequestDto;
import wanted.market.member.domain.entity.Member;
import wanted.market.member.repository.MemberRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    @Transactional
    public Member memberJoin(MemberRequestDto requestDto) {
        if (memberRepository.findByLoginId(requestDto.getLogin_id()).isPresent()) {
            throw new RuntimeException("회원 중복으로 인한 회원가입 실패");
        }
        Member member = Member.builder()
                .loginId(requestDto.getLogin_id())
                .password(requestDto.getPassword())
                .memberName(requestDto.getUsername())
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
}
