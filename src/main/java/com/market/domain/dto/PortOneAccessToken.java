package com.market.domain.dto;

public record PortOneAccessToken(
        int code,
        String message,
        AuthAnnotation response) {
}
