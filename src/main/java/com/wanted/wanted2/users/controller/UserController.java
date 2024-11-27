package com.wanted.wanted2.users.controller;

import com.wanted.wanted2.users.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;

    @PostMapping("validate")
    @Operation(summary = "이메일 중복 검증", description = "주어진 이메일이 이미 등록되어 있는지 확인합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "이메일 검증 성공"),
            @ApiResponse(responseCode = "404", description = "사용자 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @Parameter(name = "email", description = "검증할 이메일 주소", example = "example@example.com")
    public ResponseEntity<?> validate(@RequestBody String email) {
        return userService.findByEmail(email);
    }

    @PostMapping("signUp")
    @Operation(summary = "회원가입", description = "새 사용자 정보를 저장합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @Parameter(examples = {
            @ExampleObject(name = "exampleUserDto", value = "{\n  \"email\": \"example@example.com\",\n  \"password\": \"password123\",\n  \"name\": \"John Doe\"\n}")
    })
    public ResponseEntity<?> signUp(@RequestBody UserDto userDto) {
        return ResponseEntity.ok(userService.save(userDto));
    }

    @RequestMapping("authSuccess")
    @Operation(summary = "인증 성공", description = "사용자 인증 성공 후 사용자 정보 반환")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "인증 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<?> authSuccess(@AuthenticationPrincipal UserDetail userDetail) {
        return ResponseEntity.ok(userDetail.getUser());
    }

    @RequestMapping("authFailure")
    @Operation(summary = "인증 실패", description = "인증 실패 시 호출됩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "인증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<?> authFailure() {
        return ResponseEntity.ok("fail");
    }

    @RequestMapping("logoutSuccess")
    @Operation(summary = "로그아웃 성공", description = "사용자가 로그아웃 시 호출됩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<?> logoutSuccess() {
        return ResponseEntity.ok("success");
    }
}
