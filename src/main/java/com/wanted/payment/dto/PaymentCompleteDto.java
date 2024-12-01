package com.wanted.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentCompleteDto {
    private String paymentId;
    private Integer orderId;
}
