package com.wanted.payment.rqrs;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentRequest {
    private String paymentId;
    private int orderId;
}
