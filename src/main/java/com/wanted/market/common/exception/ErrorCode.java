package com.wanted.market.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // 4XX Client Error
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "입력값이 올바르지 않습니다"),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "리소스를 찾을 수 없습니다"),
    DUPLICATE_RESOURCE(HttpStatus.CONFLICT, "리소스가 이미 존재합니다"),
    UNAUTHORIZED_ACCESS(HttpStatus.FORBIDDEN, "해당 리소스에 대한 권한이 없습니다"),
    NOT_AUTHENTICATED(HttpStatus.UNAUTHORIZED, "인증되지 않은 사용자입니다"),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다"),

    // User 도메인
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다"),
    DUPLICATE_EMAIL(HttpStatus.BAD_REQUEST, "이미 사용 중인 이메일입니다"),

    // Product 도메인
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다"),
    INVALID_PRODUCT_STATUS(HttpStatus.BAD_REQUEST, "상품 상태가 올바르지 않습니다"),
    PRODUCT_NOT_AVAILABLE(HttpStatus.BAD_REQUEST, "상품이 현재 구매 가능한 상태가 아닙니다"),
    INSUFFICIENT_STOCK(HttpStatus.BAD_REQUEST, "상품의 재고가 부족합니다"),

    // Transaction 도메인
    TRANSACTION_NOT_FOUND(HttpStatus.NOT_FOUND, "거래를 찾을 수 없습니다"),
    INVALID_TRANSACTION_PARTICIPANT(HttpStatus.BAD_REQUEST, "잘못된 거래 참여자입니다"),
    INVALID_STATUS_UPDATE(HttpStatus.BAD_REQUEST, "잘못된 거래 상태 변경입니다"),
    UNAUTHORIZED_TRANSACTION_ACCESS(HttpStatus.FORBIDDEN, "해당 거래에 대한 접근 권한이 없습니다"),

    // 5XX Server Error
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "내부 서버 오류가 발생했습니다");

    private final HttpStatus status;
    private final String message;
}
