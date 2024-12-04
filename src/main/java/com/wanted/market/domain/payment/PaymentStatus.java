package com.wanted.market.domain.payment;

public enum PaymentStatus {
    READY,          // 결제 준비 상태 (가상계좌 발급 전)
    PENDING,        // 가상계좌 발급됨, 입금 대기 중
    PAID,           // 결제 완료
    FAILED,         // 결제 실패
    CANCELLED,      // 결제 취소됨
    REFUNDED        // 환불 완료
}
