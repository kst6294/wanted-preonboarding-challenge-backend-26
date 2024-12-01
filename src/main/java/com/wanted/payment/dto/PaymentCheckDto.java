package com.wanted.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(staticName = "of")
@Getter
public class PaymentCheckDto {
    private String paymentId;
}
