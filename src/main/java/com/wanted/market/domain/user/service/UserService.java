package com.wanted.market.domain.user.service;

import com.wanted.market.common.exception.CustomException;
import com.wanted.market.common.exception.ErrorCode;
import com.wanted.market.config.security.JwtTokenProvider;
import com.wanted.market.config.security.SecurityUtil;
import com.wanted.market.domain.user.User;
import com.wanted.market.domain.user.dto.LoginRequest;
import com.wanted.market.domain.user.dto.TokenResponse;
import com.wanted.market.domain.user.dto.UserCreateRequest;
import com.wanted.market.domain.user.dto.UserResponse;
import com.wanted.market.domain.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    @Transactional
    public UserResponse createUser(@Valid UserCreateRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .name(request.getName())
                .build();

        user.encodePassword(passwordEncoder);
        return UserResponse.from(userRepository.save(user));
    }

    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        return TokenResponse.builder()
                .token(tokenProvider.createToken(user.getEmail()))
                .build();
    }

    public UserResponse getMyInfo() {
        return UserResponse.from(SecurityUtil.getCurrentUser(userRepository));
    }

    public UserResponse getUserById(Long id) {
        return UserResponse.from(findUserById(id));
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = findUserById(id);
        userRepository.delete(user);
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }
}
