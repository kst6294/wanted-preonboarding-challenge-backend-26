package org.wantedpayment.member.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.wantedpayment.member.domain.dto.request.FindPasswordRequest;
import org.wantedpayment.member.domain.dto.request.JoinRequest;
import org.wantedpayment.member.domain.dto.request.LoginRequest;
import org.wantedpayment.member.domain.dto.request.NewPasswordRequest;
import org.wantedpayment.member.domain.entity.Member;
import org.wantedpayment.member.repository.MemberRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void join(JoinRequest request) {
        if(memberRepository.findByLoginId(request.getId()).isPresent()) {
            log.info("Member with loginId {} already exists", request.getId());
            throw new RuntimeException("Member with loginId " + request.getId() + " already exists");
        }

        Member member = Member.builder()
                .loginId(request.getId())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .build();

        memberRepository.save(member);
    }

    public void login(LoginRequest request, HttpServletRequest httpServletRequest) {
        Member member = memberRepository.findByLoginId(request.getId())
                .orElseThrow(() -> new RuntimeException("Member with loginId " + request.getId() + " not found"));

        if(!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        httpServletRequest.getSession().invalidate();
        HttpSession session = httpServletRequest.getSession(true);  // Session이 없으면 생성

        session.setAttribute("memberId", member.getId());
        session.setMaxInactiveInterval(1800);
    }

    public void logout(HttpServletRequest httpServletRequest) {
        HttpSession session = httpServletRequest.getSession(false);
        if(session != null) {
            session.invalidate();
        } else {
            throw new RuntimeException("Session not found");
        }
    }

    @Transactional
    public void changePassword(NewPasswordRequest request, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member with id " + memberId + " not found"));

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }

        member.changePassword(passwordEncoder.encode(request.getNewPassword()));
    }

    @Transactional
    public void findPassword(FindPasswordRequest request) {
        Member member = memberRepository.findByLoginId(request.getLoginId())
                .orElseThrow(() -> new RuntimeException("Member with loginId " + request.getLoginId() + " not found"));

        if (!member.getName().equals(request.getName())) {
            throw new RuntimeException("Member with name " + request.getName() + " not found");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }

        member.changePassword(passwordEncoder.encode(request.getNewPassword()));
    }
}
