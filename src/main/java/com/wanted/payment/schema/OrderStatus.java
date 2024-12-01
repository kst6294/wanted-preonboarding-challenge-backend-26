package com.wanted.payment.schema;

public enum OrderStatus {
    PAYMENT_COMPLETE, // 결제 완료
    PAYMENT_NOT_COMPLETE,
    REFUND, // 환불
    EXCHANGE // 교환
}
