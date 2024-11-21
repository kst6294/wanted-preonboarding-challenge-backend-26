package com.market.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserRole {

    ROLE_ADMIN("관리자"),
    ROLE_USER("회원");

    private final String name;
}
