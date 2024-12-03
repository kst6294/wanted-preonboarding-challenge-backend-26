package com.wanted.market.common.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ResponseDto<T> {
    private boolean success;
    private T data;
    private String message;

    public static <T> ResponseDto<T> success(T data) {
        return new ResponseDto<>(true, data, null);
    }

    public static <T> ResponseDto<T> success(T data, String message) {
        return new ResponseDto<>(true, data, message);
    }

    public static <T> ResponseDto<T> fail(String message) {
        return new ResponseDto<>(false, null, message);
    }

    public static <T> ResponseDto<T> fail(T data, String message) {
        return new ResponseDto<>(false, data, message);
    }
}