package org.wantedpayment.global.login;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.wantedpayment.member.domain.entity.Member;
import org.wantedpayment.member.repository.MemberRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoginCheckInterceptor implements HandlerInterceptor {
    private final MemberRepository memberRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        HttpSession session = request.getSession();

        if(session == null) {
            throw new RuntimeException("You are not logged in");
        }

        memberRepository.findById((Long) session.getAttribute("memberId"))
                .orElseThrow(() -> new RuntimeException("User not found"));

        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}
