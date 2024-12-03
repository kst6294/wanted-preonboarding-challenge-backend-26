package com.wanted.market.common.dto;

public record ValidationError(String field, String value, String reason) {
}
