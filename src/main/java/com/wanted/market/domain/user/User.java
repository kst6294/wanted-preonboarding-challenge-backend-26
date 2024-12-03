package com.wanted.market.domain.user;

import com.wanted.market.domain.base.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Table(
    name = "users",
    indexes = {
        @Index(name = "idx_email", columnList = "email")
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email
    @NotBlank
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank
    @Size(min = 8)
    @Column(nullable = false)
    private String password;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @Version
    private Long version;

    @Builder
    private User(String email, String password, String name) {
        validateUserInfo(email, password, name);
        
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public void encodePassword(PasswordEncoder passwordEncoder) {
        if (passwordEncoder == null) {
            throw new IllegalArgumentException("PasswordEncoder must not be null");
        }
        this.password = passwordEncoder.encode(this.password);
    }

    private void validateUserInfo(String email, String password, String name) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email must not be empty");
        }
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name must not be empty");
        }
    }
}
