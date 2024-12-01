package com.wanted.payment.dto;

import com.wanted.payment.rqrs.PaymentCancelRq;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class PaymentCancelDto {
    private String paymentId;
    private int orderId;
    private boolean isAll;
    private List<Integer> productIds;

    public static PaymentCancelDto of(String paymentId, int orderId, PaymentCancelRq rq) {
        return new PaymentCancelDto(paymentId, orderId, rq.isAll(), rq.getProductIds());
    }
}
