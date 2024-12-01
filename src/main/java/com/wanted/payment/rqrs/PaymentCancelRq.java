package com.wanted.payment.rqrs;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentCancelRq {
    private boolean isAll;
    private int[] productIds;
}
