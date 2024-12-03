package com.wanted.market.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserCreateRequest {
    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "올바르지 않은 이메일 형식입니다")
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다")
    private String password;

    @NotBlank(message = "이름은 필수입니다")
    private String name;

    @Builder
    public UserCreateRequest(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }
}
