package com.wanted.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PgPaymentCancelDto {
    private String paymentId;
    private boolean isAll;
    private int price;

    public static PgPaymentCancelDto of(String paymentId, boolean isAll) {
        return new PgPaymentCancelDto(paymentId, isAll, 0);
    }

    public void savePrice(int price) {
        this.price = price;
    }
}
