package com.wanted.wanted2.users.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    ROLE_ADMIN("ROLE_ADMIN"),
    ROLE_USER("ROLE_USER"),
    ROLE_SELLER("ROLE_SELLER");

    private final String role;

    public String getAuthority() {
        return role;
    }
}
