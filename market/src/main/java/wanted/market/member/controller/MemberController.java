package wanted.market.member.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wanted.market.member.domain.dto.request.*;
import wanted.market.member.domain.dto.response.*;
import wanted.market.member.domain.dto.response.service.ResetPasswordResultServiceDto;
import wanted.market.member.domain.dto.response.service.ResetPasswordServiceDto;
import wanted.market.member.domain.dto.response.service.ResetPasswordValidationServiceDto;
import wanted.market.member.domain.entity.Member;
import wanted.market.member.service.EmailService;
import wanted.market.member.service.MemberService;

import java.util.Objects;
import java.util.UUID;

import static wanted.market.global.util.BaseController.*;

@Tag(name = "회원 기능", description = "회원 가입, 회원 탈퇴")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;
    private final EmailService emailService;

    private Integer validationNumber;
    private String uuid;

    @PostMapping("/signup")
    public ResponseEntity<MemberResponseDto> signup(MemberRequestDto dto) {
        Member member = memberService.memberJoin(dto);
        if (member.equals(null)) {
            return ResponseEntity.ok(new MemberResponseDto(false, null));
        }
        return ResponseEntity.ok(new MemberResponseDto(true, member.getMemberName()));
    }

    @PostMapping("/delete-id")
    public ResponseEntity<MemberResponseDto> deleteId(MemberRequestDto dto) {
        try {
            memberService.deleteMember(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.ok(new MemberResponseDto(false, null));
        }

        return ResponseEntity.ok(new MemberResponseDto(true, dto.getUsername()));
    }

    //비밀번호 재설정을 위한 이메일 인증 - 인증 메일 전송
    @PostMapping("/reset/password/request")
    public ResponseEntity<ResetPasswordUserResponseDto> resetPasswordRequest(ResetPasswordRequestDto dto) {
        ResetPasswordServiceDto sendMailResponse = emailService.sendMail(dto);
        validationNumber = sendMailResponse.getValidationNumber();
        if (Objects.nonNull(validationNumber)) {
            return ResponseEntity.ok(new ResetPasswordUserResponseDto(true, "인증 메일이 전송되었습니다."));
        } else {
            return ResponseEntity.ok(new ResetPasswordUserResponseDto(false, "인증 메일 전송에 실패하였습니다. 잘못된 요청."));
        }
    }

    @GetMapping("/reset/password/validation")
    public ResponseEntity<ResetPasswordValidationResponseDto> resetPasswordValidation(ResetPasswordValidationRequestDto dto, HttpServletRequest request) {
        ResetPasswordValidationServiceDto validationResult = emailService.mailValidationCompareToNumber(dto, validationNumber);
        if (validationResult.isResetSuccess() == true) {
            uuid = UUID.randomUUID().toString();
            log.info("password 재설정을 위한 uuid 발급 성공 : userId : {} , uuid : {} ", validationResult.getUserId(), uuid);
            return ResponseEntity.ok(
                    new ResetPasswordValidationResponseDto(
                    validationResult.getUserId(),
                    validationResult.isResetSuccess(),
                    uuid,
                    validationResult.getMessage()
                    ));
        } else {
            return ResponseEntity.ok(new ResetPasswordValidationResponseDto(
                    validationResult.getUserId(),
                    validationResult.isResetSuccess(),
                    null,
                    validationResult.getMessage()
            ));
        }
    }

    @PostMapping("/reset/password/{userId}")
    public ResponseEntity<ResetPasswordResultResponseDto> resetPassword(@PathVariable Long userId, NewPasswordRequestDto dto, HttpServletRequest request) {
        validationNumber = null;
        if (!dto.getUuid().equals(uuid)) {
            return ResponseEntity.ok(new ResetPasswordResultResponseDto(userId,false, "메일 검증이 되지 않은 사용자입니다."));
        }
        ResetPasswordResultServiceDto resetResult = memberService.resetNewPassword(dto.getNewPassword(), userId);
        if (Objects.nonNull(resetResult.getUserId())) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
                // 해당 로그인 한 유저의 세션을 만료시킴 (로그아웃 시킴)
            }
            return ResponseEntity.ok(new ResetPasswordResultResponseDto(resetResult.getUserId(), resetResult.isResetPasswordSuccess(), "PASSWORD 재설정 성공, 새로운 비밀번호로 로그인 하세요."));
        } else {
            return ResponseEntity.ok(new ResetPasswordResultResponseDto(userId, resetResult.isResetPasswordSuccess(), "PASSWORD 재설정 실패, 해당 계정을 찾을 수 없습니다."));
        }
    }
}
