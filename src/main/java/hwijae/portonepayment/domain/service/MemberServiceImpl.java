package hwijae.portonepayment.domain.service;

import hwijae.portonepayment.domain.entity.Member;
import hwijae.portonepayment.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    @Override
    public Member autoRegister() {
        Member member = Member.builder()
                .username(UUID.randomUUID().toString())
                .email("example@example.com")
                .build();

        return memberRepository.save(member);
    }
}
