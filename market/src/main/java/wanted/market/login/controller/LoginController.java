package wanted.market.login.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import wanted.market.login.domain.dto.request.LoginRequestDto;
import wanted.market.login.domain.dto.response.LoginResponseDto;
import wanted.market.login.service.LoginService;
import wanted.market.member.domain.entity.Member;

import static wanted.market.login.session.SessionConst.LOGIN_MEMBER;

@Tag(name = "로그인 기능", description = "로그인, 로그아웃")
@Slf4j
@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class LoginController {
    private final LoginService loginService;

    @PostMapping("/sign-in")
    public ResponseEntity<LoginResponseDto> login(@Valid @ModelAttribute LoginRequestDto loginRequestDto, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            log.info("필수 입력값 누락");
            return ResponseEntity.ok(new LoginResponseDto(false, null));
        }

        Member loginMember;
        try {
            loginMember = loginService.login(loginRequestDto.getLoginId(), loginRequestDto.getPassword());
        } catch (RuntimeException e) {
            return ResponseEntity.ok(new LoginResponseDto(false, null));
        }

        HttpSession session = request.getSession();
        session.setAttribute(LOGIN_MEMBER, loginMember.getId());

        return ResponseEntity.ok(new LoginResponseDto(true, loginMember.getMemberName()));

    }

    @PostMapping("/logout")
    public void logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }
}
