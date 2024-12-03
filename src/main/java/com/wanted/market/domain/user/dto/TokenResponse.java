package com.wanted.market.domain.user.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TokenResponse {
    
    @NotNull
    private String token;
    private String tokenType = "Bearer";

    @Builder
    public TokenResponse(String token) {
        this.token = token;
    }
}
