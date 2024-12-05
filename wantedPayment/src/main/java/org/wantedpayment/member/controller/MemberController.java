package org.wantedpayment.member.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wantedpayment.global.util.CommonController;
import org.wantedpayment.member.domain.dto.request.FindPasswordRequest;
import org.wantedpayment.member.domain.dto.request.JoinRequest;
import org.wantedpayment.member.domain.dto.request.LoginRequest;
import org.wantedpayment.member.domain.dto.request.NewPasswordRequest;
import org.wantedpayment.member.service.MemberService;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController extends CommonController {
    private final MemberService memberService;

    @PostMapping(path = "/join")
    public void join(@RequestBody JoinRequest joinRequest) {
        memberService.join(joinRequest);
    }

    @PostMapping(path = "/login")
    public void login(@RequestBody LoginRequest request, HttpServletRequest httpServletRequest) {
        memberService.login(request, httpServletRequest);
    }

    @PostMapping(path = "/logout")
    public void logout(HttpServletRequest httpServletRequest) {
        memberService.logout(httpServletRequest);
    }

    @PostMapping(path = "/new-password")
    public void changePassword(@RequestBody NewPasswordRequest request,
                               HttpServletRequest httpServletRequest) {
        memberService.changePassword(request, getLoginMemberId(httpServletRequest));
    }

    @PostMapping(path = "/find-password")
    public void findPassword(@RequestBody FindPasswordRequest request) {
        memberService.findPassword(request);
    }
}
