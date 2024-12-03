package com.wanted.market.domain.transaction;

public enum TransactionStatus {
    REQUESTED,  // 구매 요청됨
    APPROVED,   // 판매자 승인됨
    CONFIRMED,  // 구매 확정됨 (2단계)
    COMPLETED   // 거래 완료
}