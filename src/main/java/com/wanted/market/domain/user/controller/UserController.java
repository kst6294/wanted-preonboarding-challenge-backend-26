package com.wanted.market.domain.user.controller;

import com.wanted.market.common.dto.ResponseDto;
import com.wanted.market.domain.user.dto.LoginRequest;
import com.wanted.market.domain.user.dto.TokenResponse;
import com.wanted.market.domain.user.dto.UserCreateRequest;
import com.wanted.market.domain.user.dto.UserResponse;
import com.wanted.market.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController implements UserControllerSpec {

    private final UserService userService;

    @Override
    @PostMapping
    public ResponseEntity<ResponseDto<UserResponse>> createUser(
            @RequestBody @Valid UserCreateRequest request
    ) {
        UserResponse user = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseDto.success(user));
    }

    @Override
    @PostMapping("/login")
    public ResponseEntity<ResponseDto<TokenResponse>> login(
            @RequestBody @Valid LoginRequest request
    ) {
        TokenResponse token = userService.login(request);
        return ResponseEntity.ok(ResponseDto.success(token));
    }

    @Override
    @GetMapping("/me")
    public ResponseEntity<ResponseDto<UserResponse>> getMyInfo(
            @AuthenticationPrincipal Long userId
    ) {
        return ResponseEntity.ok(ResponseDto.success(userService.getMyInfo()));
    }
}