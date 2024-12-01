package com.wanted.payment.rqrs;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PaymentCancelRq {
    private boolean isAll;
    private List<Integer> productIds;
}
