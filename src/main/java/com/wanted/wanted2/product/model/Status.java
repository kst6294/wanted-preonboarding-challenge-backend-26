package com.wanted.wanted2.product.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Status {
    ON_SALE("판매중"),
    RESERVED("예약중"),
    COMPLETED("완료");

    private final String status;
}
