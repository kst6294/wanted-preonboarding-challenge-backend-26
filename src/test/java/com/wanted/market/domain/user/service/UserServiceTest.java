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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService 테스트")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider tokenProvider;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserCreateRequest createRequest;
    private final String RAW_PASSWORD = "password123!@#";

    @BeforeEach
    void setUp() {
        createRequest = new UserCreateRequest(
                "test@example.com",
                RAW_PASSWORD,
                "Test User"
        );

        // 가짜 해시값
        String encodedPasswordHash = "encoded_password_hash";
        user = User.builder()
                .email(createRequest.getEmail())
                .password(encodedPasswordHash)
                .name(createRequest.getName())
                .build();
        ReflectionTestUtils.setField(user, "id", 1L);
    }

    @Nested
    @DisplayName("회원가입 테스트")
    class CreateUser {

        @Test
        @DisplayName("성공: 정상적인 회원가입")
        void createUser_Success() {
            // Given
            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            when(userRepository.save(any(User.class))).thenReturn(user);

            // When
            UserResponse response = userService.createUser(createRequest);

            // Then
            assertThat(response.getId()).isEqualTo(user.getId());
            assertThat(response.getEmail()).isEqualTo(createRequest.getEmail());
            assertThat(response.getName()).isEqualTo(createRequest.getName());
            verify(passwordEncoder).encode(anyString());
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("실패: 중복된 이메일로 가입 시도")
        void createUser_DuplicateEmail() {
            // Given
            when(userRepository.existsByEmail(anyString())).thenReturn(true);

            // When & Then
            CustomException exception = assertThrows(CustomException.class,
                    () -> userService.createUser(createRequest));
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.DUPLICATE_EMAIL);
            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("로그인 테스트")
    class Login {

        @Test
        @DisplayName("성공: 정상적인 로그인")
        void login_Success() {
            // Given
            String token = "test.jwt.token";
            LoginRequest loginRequest = new LoginRequest(createRequest.getEmail(), RAW_PASSWORD);

            when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
            when(tokenProvider.createToken(anyString())).thenReturn(token);

            // When
            TokenResponse response = userService.login(loginRequest);

            // Then
            assertThat(response.getToken()).isEqualTo(token);
            verify(passwordEncoder).matches(RAW_PASSWORD, user.getPassword());
            verify(tokenProvider).createToken(user.getEmail());
        }

        @Test
        @DisplayName("실패: 잘못된 비밀번호")
        void login_InvalidPassword() {
            // Given
            LoginRequest loginRequest = new LoginRequest(createRequest.getEmail(), "wrongPassword");

            when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

            // When & Then
            CustomException exception = assertThrows(CustomException.class,
                    () -> userService.login(loginRequest));
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_PASSWORD);
            verify(tokenProvider, never()).createToken(anyString());
        }

        @Test
        @DisplayName("실패: 존재하지 않는 이메일")
        void login_UserNotFound() {
            // Given
            LoginRequest loginRequest = new LoginRequest("unknown@example.com", RAW_PASSWORD);
            when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

            // When & Then
            CustomException exception = assertThrows(CustomException.class,
                    () -> userService.login(loginRequest));
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
            verify(passwordEncoder, never()).matches(anyString(), anyString());
        }
    }

    @Nested
    @DisplayName("사용자 정보 조회 테스트")
    class GetUser {

        @Test
        @DisplayName("성공: 내 정보 조회")
        void getMyInfo_Success() {
            // Given
            try (var securityUtilMock = mockStatic(SecurityUtil.class)) {
                securityUtilMock.when(() -> SecurityUtil.getCurrentUser(userRepository))
                        .thenReturn(user);

                // When
                UserResponse response = userService.getMyInfo();

                // Then
                assertThat(response.getId()).isEqualTo(user.getId());
                assertThat(response.getEmail()).isEqualTo(user.getEmail());
            }
        }

        @Test
        @DisplayName("성공: ID로 사용자 정보 조회")
        void getUserById_Success() {
            // Given
            when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

            // When
            UserResponse response = userService.getUserById(user.getId());

            // Then
            assertThat(response.getId()).isEqualTo(user.getId());
            assertThat(response.getEmail()).isEqualTo(user.getEmail());
        }

        @Test
        @DisplayName("실패: 존재하지 않는 사용자 조회")
        void getUserById_UserNotFound() {
            // Given
            when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

            // When & Then
            CustomException exception = assertThrows(CustomException.class,
                    () -> userService.getUserById(999L));
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("회원 탈퇴 테스트")
    class DeleteUser {

        @Test
        @DisplayName("성공: 정상 탈퇴")
        void deleteUser_Success() {
            // Given
            when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

            // When
            userService.deleteUser(user.getId());

            // Then
            verify(userRepository).delete(user);
        }

        @Test
        @DisplayName("실패: 존재하지 않는 사용자 탈퇴")
        void deleteUser_UserNotFound() {
            // Given
            when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

            // When & Then
            CustomException exception = assertThrows(CustomException.class,
                    () -> userService.deleteUser(999L));
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
            verify(userRepository, never()).delete(any(User.class));
        }
    }
}
