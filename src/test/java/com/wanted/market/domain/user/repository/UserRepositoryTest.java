package com.wanted.market.domain.user.repository;

import com.wanted.market.config.TestJpaConfig;
import com.wanted.market.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import(TestJpaConfig.class)
@DisplayName("User Repository 테스트")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .email("test@example.com")
                .password("password12345")
                .name("Test User")
                .build();
    }

    @Test
    @DisplayName("사용자 저장 성공")
    void saveUser() {
        // When
        User savedUser = userRepository.save(user);

        // Then
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo("test@example.com");
        assertThat(savedUser.getName()).isEqualTo("Test User");
        assertThat(savedUser.getVersion()).isNotNull();
    }

    @Test
    @DisplayName("이메일로 사용자 조회")
    void findByEmail() {
        // Given
        userRepository.save(user);

        // When
        User foundUser = userRepository.findByEmail("test@example.com").orElseThrow();

        // Then
        assertThat(foundUser.getEmail()).isEqualTo("test@example.com");
        assertThat(foundUser.getName()).isEqualTo("Test User");
    }

    @Test
    @DisplayName("이메일 중복 확인")
    void existsByEmail() {
        // Given
        userRepository.save(user);

        // When & Then
        assertThat(userRepository.existsByEmail("test@example.com")).isTrue();
        assertThat(userRepository.existsByEmail("nonexistent@example.com")).isFalse();
    }

    @Test
    @DisplayName("이메일과 이름으로 사용자 조회")
    void findByEmailAndName() {
        // Given
        userRepository.save(user);

        // When & Then
        assertThat(userRepository.findByEmailAndName("test@example.com", "Test User"))
                .isPresent();
        assertThat(userRepository.findByEmailAndName("test@example.com", "Wrong Name"))
                .isEmpty();
    }

    @Test
    @DisplayName("특정 시간 이후 가입한 사용자 수 조회")
    void countUsersJoinedAfter() {
        // Given
        LocalDateTime criterion = LocalDateTime.now().minusHours(1);
        userRepository.save(user);

        // When
        long count = userRepository.countUsersJoinedAfter(criterion);

        // Then
        assertThat(count).isEqualTo(1);
    }
}
