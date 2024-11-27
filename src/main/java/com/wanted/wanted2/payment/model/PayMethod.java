package com.wanted.wanted2.payment.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PayMethod {
    VIRTUAL_ACCOUNT("가상계좌"),
    CARD("카드 결제"),
    EASY_PAYMENT("간편 결제");

    private final String paymentMethod;
}
